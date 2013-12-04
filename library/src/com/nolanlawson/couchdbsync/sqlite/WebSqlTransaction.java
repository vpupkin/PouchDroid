package com.nolanlawson.couchdbsync.sqlite;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Abstraction of a web sqlite transaction, so that we can batch queries and do other performance improvements
 * 
 * @author nolan
 *
 */
public class WebSqlTransaction {

    private String dbName;
    private int transactionId;
    private String successId;
    private String errorId;
    private LinkedBlockingQueue<WebSqlQuery> queries = new LinkedBlockingQueue<WebSqlQuery>();
    private boolean begun = false;
    private boolean shouldEnd = false;
    private boolean markAsSuccessful = false;
    
    public WebSqlTransaction(String dbName, int transactionId, String successId, String errorId) {
        this.dbName = dbName;
        this.transactionId = transactionId;
        this.successId = successId;
        this.errorId = errorId;
    }
    
    public boolean isMarkAsSuccessful() {
        return markAsSuccessful;
    }
    public void setMarkAsSuccessful(boolean markAsSuccessful) {
        this.markAsSuccessful = markAsSuccessful;
    }
    public boolean isShouldEnd() {
        return shouldEnd;
    }
    public void setShouldEnd(boolean shouldEnd) {
        this.shouldEnd = shouldEnd;
    }
    public boolean isBegun() {
        return begun;
    }

    public void setBegun(boolean begun) {
        this.begun = begun;
    }

    public String getDbName() {
        return dbName;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public LinkedBlockingQueue<WebSqlQuery> getQueries() {
        return queries;
    }

    public String getSuccessId() {
        return successId;
    }

    public String getErrorId() {
        return errorId;
    }

    @Override
    public String toString() {
        return "WebSqlTransaction [dbName=" + dbName + ", transactionId=" + transactionId + ", successId=" + successId
                + ", errorId=" + errorId + ", queries=" + queries + ", begun=" + begun + ", shouldEnd=" + shouldEnd
                + ", markAsSuccessful=" + markAsSuccessful + "]";
    }
}
