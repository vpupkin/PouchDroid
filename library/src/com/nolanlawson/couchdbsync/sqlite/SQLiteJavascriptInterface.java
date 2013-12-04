/**
 * Mostly taken from the Android PhoneGap SQLite plugin.
 * 
 * Acts as an interface to the sqlite_native_interface.js file.  Interacts with SQLite and pretends to
 * be the WebSQL standard.
 * 
 * @author nolan
 */
package com.nolanlawson.couchdbsync.sqlite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.nolanlawson.couchdbsync.util.UtilLogger;

public class SQLiteJavascriptInterface {

    private static UtilLogger log = new UtilLogger(SQLiteJavascriptInterface.class);
    
    private static final long BATCH_TRANSACTION_DELAY = 10;
    
    private Activity activity;
    private WebView webView;
    
    private final Map<String, SQLiteDatabase> dbs = new HashMap<String, SQLiteDatabase>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final LinkedBlockingQueue<WebSqlTransaction> transactions = new LinkedBlockingQueue<WebSqlTransaction>();
    private final BatchTransactionRunnable batchTransactionRunnable = new BatchTransactionRunnable();;

    public SQLiteJavascriptInterface(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }
    
    private void sendCallback(JavascriptCallback callback) {
        sendCallback(Collections.singletonList(callback));
    }
    
    private void sendCallback(List<JavascriptCallback> callbacks) {
        log.d("sendCallback(%s)", callbacks);

        try {
            final StringBuilder url = new StringBuilder().append("javascript:(function(){");
            for (JavascriptCallback callback : callbacks) {
                url.append("SQLiteNativeDB.callbacks['")
                    .append(callback.getCallbackId())
                    .append("'](")
                    .append(callback.getArg1() != null ? objectMapper.writeValueAsString(callback.getArg1()) : "")
                    .append(");");
            }
            url.append("})();");

            log.d("calling javascript: %s", url);

            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url.toString());
                }
            });
        } catch (IOException e) {
            // shouldn't happen
            log.e(e, "unexpected");
        }
    }
    
    @JavascriptInterface
    public void open(final String dbName, final String callbackId) {
        log.d("open(%s, %s)", dbName, callbackId);
        try {
            SQLiteDatabase db = dbs.get(dbName);
            if (db == null) { // doesn't exist yet
                db = activity.openOrCreateDatabase(dbName + "_nwebsql.db", 0, null);
                dbs.put(dbName, db);
            }
            sendCallback(new JavascriptCallback(callbackId, null, false));
        } catch (Exception e) {
            // shouldn't happen
            log.e(e, "unexpected");
        } 
    }

    @JavascriptInterface
    public void startTransaction(int transactionId, final String dbName, final String successId, final String errorId) {
        log.d("startTransaction(%s, %s, %s, %s)", transactionId, dbName, successId, errorId);
        try {
            synchronized (transactions) {
                transactions.put(new WebSqlTransaction(dbName, transactionId, successId, errorId));
            }
            sendCallback(new JavascriptCallback(successId, null, false));
            handler.removeCallbacks(batchTransactionRunnable);
            handler.post(batchTransactionRunnable);
        } catch (Exception e) {
            log.e(e, "error");
            sendCallback(new JavascriptCallback(errorId, null, true));
        }
    }
    
    @JavascriptInterface
    public void endTransaction(int transactionId, String dbName, String successId, String errorId,
            boolean markAsSuccessful) {
        
        log.d("endTransaction(%s, %s, %s, %s, %s)", transactionId, dbName, successId, errorId, markAsSuccessful);
        
        try {
            WebSqlTransaction transaction = findTransactionById(transactionId);
            transaction.setMarkAsSuccessful(markAsSuccessful);
            transaction.setShouldEnd(true);
            handler.removeCallbacks(batchTransactionRunnable);
            handler.postDelayed(batchTransactionRunnable, BATCH_TRANSACTION_DELAY);
        } catch (Exception e) {
            log.e(e, "unexpected");
            sendCallback(new JavascriptCallback(errorId, createSqlError(e.getMessage()), true));
        }
    }
    
    @JavascriptInterface
    public void executeSql(int transactionId, final String dbName, final String sql, final String selectArgsJson, 
            final String querySuccessId, final String queryErrorId) {
        log.d("executeSql(%s, %s, %s, %s, %s, %s)", transactionId, dbName, sql, selectArgsJson, querySuccessId, queryErrorId);
        
        WebSqlTransaction transaction = findTransactionById(transactionId);
        if (transaction == null) {
            sendCallback(new JavascriptCallback(queryErrorId, createSqlError("transaction was invalidated"), true));
        } else {
            // valid transaction
            try {
                transaction.getQueries().put(new WebSqlQuery(sql, selectArgsJson, querySuccessId, queryErrorId));
            } catch (InterruptedException e) {
                log.d(e, "unexpected");
                sendCallback(new JavascriptCallback(queryErrorId, createSqlError(e.getMessage()), true));
                return;
            }
            handler.removeCallbacks(batchTransactionRunnable);
            handler.postDelayed(batchTransactionRunnable, BATCH_TRANSACTION_DELAY);
        }
    }
    
    @SuppressLint("NewApi")
    private JavascriptCallback execute(WebSqlQuery webSqlQuery, WebSqlTransaction transaction) {
        
        log.d("execute %s, %s", webSqlQuery, transaction);
        
        String dbName = transaction.getDbName();
        String selectArgsJson = webSqlQuery.getSelectArgsJson();
        String sql = webSqlQuery.getSql();
        String querySuccessId = webSqlQuery.getQuerySuccessId();
        String queryErrorId = webSqlQuery.getQueryErrorId();
        
        SQLiteDatabase db = dbs.get(dbName);
        try {
            List<Object> selectArgs = getSelectArgs(selectArgsJson);
            String query = sql;
            List<Object> batchResults = new ArrayList<Object>();
            ObjectNode queryResult = null;

            // /* OPTIONAL changes for new Android SDK from HERE:
            if (android.os.Build.VERSION.SDK_INT >= 11
                    && (query.toLowerCase().startsWith("update") || query.toLowerCase().startsWith("delete"))) {
                synchronized (db) {
                    SQLiteStatement myStatement = db.compileStatement(query);

                    if (selectArgs != null) {
                        for (int j = 0; j < selectArgs.size(); j++) {
                            if (selectArgs.get(j) instanceof Float || selectArgs.get(j) instanceof Double) {
                                myStatement.bindDouble(j + 1, (Double) selectArgs.get(j));
                            } else if (selectArgs.get(j) instanceof Integer) {
                                myStatement.bindLong(j + 1, (Integer) selectArgs.get(j));
                            } else if (selectArgs.get(j) instanceof Long) {
                                myStatement.bindLong(j + 1, (Long) selectArgs.get(j));
                            } else if (selectArgs.get(j) == null) {
                                myStatement.bindNull(j + 1);
                            } else {
                                myStatement.bindString(j + 1, (String) selectArgs.get(j));
                            }
                        }
                    }

                    int rowsAffected = myStatement.executeUpdateDelete();

                    queryResult = objectMapper.createObjectNode();
                    queryResult.put("rowsAffected", rowsAffected);
                }
            } else // to HERE. */
            if (query.toLowerCase().startsWith("insert") && selectArgs != null) {
                synchronized (db) {
                    SQLiteStatement myStatement = db.compileStatement(query);

                    for (int j = 0; j < selectArgs.size(); j++) {
                        if (selectArgs.get(j) instanceof Float || selectArgs.get(j) instanceof Double) {
                            myStatement.bindDouble(j + 1, (Double) selectArgs.get(j));
                        } else if (selectArgs.get(j) instanceof Integer) {
                            myStatement.bindLong(j + 1, (Integer) selectArgs.get(j));
                        } else if (selectArgs.get(j) instanceof Long) {
                            myStatement.bindLong(j + 1, (Long) selectArgs.get(j));
                        } else if (selectArgs.get(j) == null) {
                            myStatement.bindNull(j + 1);
                        } else {
                            myStatement.bindString(j + 1, (String) selectArgs.get(j));
                        }
                    }

                    long insertId = myStatement.executeInsert();

                    int rowsAffected = (insertId == -1) ? 0 : 1;

                    queryResult = objectMapper.createObjectNode();
                    queryResult.put("insertId", insertId);
                    queryResult.put("rowsAffected", rowsAffected);
                }
            } else {
                String[] params = null;

                if (selectArgs != null) {
                    params = new String[selectArgs.size()];

                    for (int j = 0; j < selectArgs.size(); j++) {
                        if (selectArgs.get(j) == null)
                            params[j] = "";
                        else
                            params[j] = String.valueOf(selectArgs.get(j));
                    }
                }

                synchronized (db) {
                    Cursor myCursor = db.rawQuery(query, params);

                    queryResult = this.getRowsResultFromQuery(myCursor);

                    myCursor.close();
                }
            }
            

            if (queryResult != null) {
                ObjectNode r = objectMapper.createObjectNode();

                r.put("type", "success");
                r.put("result", queryResult);

                batchResults.add(r);
            }
            log.d("query success %s", webSqlQuery);
            return new JavascriptCallback(querySuccessId, queryResult, false);
        } catch (Exception e) {
            log.e(e, "unexpected");
            return new JavascriptCallback(queryErrorId, createSqlError(e.getMessage()), true);
        }
    }
    
    private void endTransaction(WebSqlTransaction transaction) {
        
        boolean error = false;
        
        SQLiteDatabase db = dbs.get(transaction.getDbName());
        try {
            if (transaction.isMarkAsSuccessful()) {
                synchronized (db) {
                    db.setTransactionSuccessful();
                }
            }
        } catch (Exception e) {
            log.e(e, "unexpected");
            error = true;
        } finally {
            synchronized (db) {
                try {
                    db.endTransaction();
                } catch (Exception e) {
                    log.e(e, "unexpected");
                    error = true;
                }
            }
            synchronized (transactions) {
                if (transaction != null) {
                    transactions.remove(transaction);
                }
            }
            
            if (error) {
                sendCallback(new JavascriptCallback(transaction.getErrorId(), null, true));
            } else {
                sendCallback(new JavascriptCallback(transaction.getSuccessId(), null, false));
            }
        }
    }
    
    private ObjectNode createSqlError(String message) {
        ObjectNode sqlErrorObject = objectMapper.createObjectNode();

        sqlErrorObject.put("type", "error");
        sqlErrorObject.put("result", message);
        
        return sqlErrorObject;
    }

    private List<Object> getSelectArgs(String selectArgsJson) {

        if (TextUtils.isEmpty(selectArgsJson)) {
            return null;
        }

        log.d("getSelectArgs(%s)", selectArgsJson);
        try {
            return objectMapper.readValue(selectArgsJson, new TypeReference<List<Object>>() {
            });
        } catch (IOException e) {
            // ignore
            log.e(e, "unexpected error");
        }
        return null;
    }

    /**
     * Get rows results from query cursor.
     * 
     * @param cur
     *            Cursor into query results
     * 
     * @return results in string form
     * 
     */
    @SuppressLint("NewApi")
    private ObjectNode getRowsResultFromQuery(Cursor cur) {
        ObjectNode rowsResult = objectMapper.createObjectNode();

        ArrayNode rowsArrayResult = rowsResult.putArray("rows");

        // If query result has rows
        if (cur.moveToFirst()) {
            String key = "";
            int colCount = cur.getColumnCount();

            // Build up JSON result object for each row
            do {
                ObjectNode row = objectMapper.createObjectNode();
                try {
                    for (int i = 0; i < colCount; ++i) {
                        key = cur.getColumnName(i);

                        // for old Android SDK remove lines from HERE:
                        if (android.os.Build.VERSION.SDK_INT >= 11) {
                            switch (cur.getType(i)) {
                                case Cursor.FIELD_TYPE_NULL:
                                    row.putNull(key);
                                    break;
                                case Cursor.FIELD_TYPE_INTEGER:
                                    row.put(key, cur.getInt(i));
                                    break;
                                case Cursor.FIELD_TYPE_FLOAT:
                                    row.put(key, cur.getFloat(i));
                                    break;
                                case Cursor.FIELD_TYPE_STRING:
                                    row.put(key, cur.getString(i));
                                    break;
                                case Cursor.FIELD_TYPE_BLOB:
                                    row.put(key, new String(Base64.encode(cur.getBlob(i), Base64.DEFAULT)));
                                    break;
                            }
                        } else // to HERE.
                        {
                            row.put(key, cur.getString(i));
                        }
                    }

                    rowsArrayResult.add(row);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (cur.moveToNext());
        }

        return rowsResult;
    }

    public void close() {
        this.activity = null;
        for (Entry<String, SQLiteDatabase> entry : dbs.entrySet()) {
            String dbName = entry.getKey();
            SQLiteDatabase db = entry.getValue();
            
            if (db != null) {
                log.d("closing database with name %s", dbName);
                db.close();
                log.d("closed database with name %s", dbName);
            }
        }
    }
    
    private WebSqlTransaction findTransactionById(int transactionId) {
        for (WebSqlTransaction candidate : transactions) {
            if (candidate.getTransactionId() == transactionId) {
                return candidate;
            }
        }
        return null;
    }    

    private class BatchTransactionRunnable implements Runnable {

        @Override
        public void run() {
            synchronized (this) {
                log.d("BatchTransactionRunnable.run()");
                
                WebSqlTransaction transaction;
                
                synchronized (transactions) {
                    transaction = transactions.peek();
                    
                    if (transaction == null) {
                        return;
                    }
                }
                
                // end the transaction
                if (transaction.isShouldEnd()) {
                    log.d("ending transaction %s", transaction.getTransactionId());
                    endTransaction(transaction);
                    synchronized (transactions) {
                        transactions.remove(transaction);
                    }
                    return;
                }
                
                // start the transaction
                if (!transaction.isBegun()) {
                    log.d("beginning transaction %s", transaction.getTransactionId());
                    try {
                        SQLiteDatabase db = dbs.get(transaction.getDbName());
                        synchronized (db) {
                            db.beginTransaction();
                            transaction.setBegun(true);
                        }
                    } catch (Exception e) {
                        // couldn't even begin
                        sendCallback(new JavascriptCallback(transaction.getErrorId(), null, true));
                        return;
                    }
                }
                
                // execute one or more queries for the transaction;
                log.d("ending queries for transaction %s", transaction.getTransactionId());
                
                List<JavascriptCallback> callbacks = null;
                
                WebSqlQuery webSqlQuery;
                while ((webSqlQuery = transaction.getQueries().poll()) != null) {
                    JavascriptCallback callback = execute(webSqlQuery, transaction);

                    if (callbacks == null) {
                        callbacks = new ArrayList<JavascriptCallback>();
                    }
                    callbacks.add(callback);
                    
                    if (callback.isError()) {
                        break; // per w3c spec, we stop the world and wait for judgment on this failure
                    }
                }
                
                if (callbacks != null) {
                    sendCallback(callbacks);
                }
            }
        }
    }
}
