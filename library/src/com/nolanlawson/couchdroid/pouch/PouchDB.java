package com.nolanlawson.couchdroid.pouch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.type.TypeReference;

import android.app.Activity;
import android.text.TextUtils;

import com.nolanlawson.couchdroid.CouchDroidRuntime;
import com.nolanlawson.couchdroid.util.JsonUtil;
import com.nolanlawson.couchdroid.util.UtilLogger;

public class PouchDB<T extends PouchDocument> {

    private static UtilLogger log = new UtilLogger(PouchDB.class);

    private static final AtomicInteger POUCH_IDS = new AtomicInteger(0);

    private int id;
    private CouchDroidRuntime runtime;

    private Class<T> documentClass;
    
    /**
     * <p>
     * This method creates a database or opens an existing one. If you use a
     * <code>http://domain.com/dbname</code> then PouchDB will work as a client
     * to an online CouchDB instance, otherwise it will create a local database
     * using a backend that is present.
     * </p>
     * 
     * <p>
     * <strong>Notes:</strong>
     * </p>
     * 
     * <ol>
     * <li>If you are also using indexedDB directly, PouchDB will use
     * <code>_pouch_</code> to prefix the internal database names, dont manually
     * create databases with the same prefix.</li>
     * <li>When acting as a client on Node any other options given will be
     * passed to <a href="https://github.com/mikeal/request">request</a>.</li>
     * </ol>
     * 
     * <ul>
     * <li><code>options.name</code>: You can omit the name argument and specify
     * it via options.</li>
     * <li><code>options.auto_compaction</code>: This turns on auto compaction
     * (experimental).</li>
     * </ul>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="kd">var</span> <span class="nx">db</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">PouchDB</span><span class="p">(</span><span class="s1">&#39;dbname&#39;</span><span class="p">);</span>
     * <span class="c1">// or</span>
     * <span class="kd">var</span> <span class="nx">db</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">PouchDB</span><span class="p">(</span><span class="s1">&#39;http://localhost:5984/dbname&#39;</span><span class="p">);</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href=
     *      'http://pouchdb.com/api.html#create_database'>http://pouchdb.com/api.html#create_databas
     *      e < / a >
     */
    public static <T extends PouchDocument> PouchDB<T> newPouchDB(Class<T> documentClass, CouchDroidRuntime runtime) {
        return newPouchDB(documentClass, runtime, null, false);
    }

    /**
     * <p>
     * This method creates a database or opens an existing one. If you use a
     * <code>http://domain.com/dbname</code> then PouchDB will work as a client
     * to an online CouchDB instance, otherwise it will create a local database
     * using a backend that is present.
     * </p>
     * 
     * <p>
     * <strong>Notes:</strong>
     * </p>
     * 
     * <ol>
     * <li>If you are also using indexedDB directly, PouchDB will use
     * <code>_pouch_</code> to prefix the internal database names, dont manually
     * create databases with the same prefix.</li>
     * <li>When acting as a client on Node any other options given will be
     * passed to <a href="https://github.com/mikeal/request">request</a>.</li>
     * </ol>
     * 
     * <ul>
     * <li><code>options.name</code>: You can omit the name argument and specify
     * it via options.</li>
     * <li><code>options.auto_compaction</code>: This turns on auto compaction
     * (experimental).</li>
     * </ul>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="kd">var</span> <span class="nx">db</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">PouchDB</span><span class="p">(</span><span class="s1">&#39;dbname&#39;</span><span class="p">);</span>
     * <span class="c1">// or</span>
     * <span class="kd">var</span> <span class="nx">db</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">PouchDB</span><span class="p">(</span><span class="s1">&#39;http://localhost:5984/dbname&#39;</span><span class="p">);</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href=
     *      'http://pouchdb.com/api.html#create_database'>http://pouchdb.com/api.html#create_databas
     *      e < / a >
     */
    public static <T extends PouchDocument> PouchDB<T> newPouchDB(Class<T> documentClass, CouchDroidRuntime runtime, String name) {
        return newPouchDB(documentClass, runtime, name, false);
    }

    /**
     * <p>
     * This method creates a database or opens an existing one. If you use a
     * <code>http://domain.com/dbname</code> then PouchDB will work as a client
     * to an online CouchDB instance, otherwise it will create a local database
     * using a backend that is present.
     * </p>
     * 
     * <p>
     * <strong>Notes:</strong>
     * </p>
     * 
     * <ol>
     * <li>If you are also using indexedDB directly, PouchDB will use
     * <code>_pouch_</code> to prefix the internal database names, dont manually
     * create databases with the same prefix.</li>
     * <li>When acting as a client on Node any other options given will be
     * passed to <a href="https://github.com/mikeal/request">request</a>.</li>
     * </ol>
     * 
     * <ul>
     * <li><code>options.name</code>: You can omit the name argument and specify
     * it via options.</li>
     * <li><code>options.auto_compaction</code>: This turns on auto compaction
     * (experimental).</li>
     * </ul>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="kd">var</span> <span class="nx">db</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">PouchDB</span><span class="p">(</span><span class="s1">&#39;dbname&#39;</span><span class="p">);</span>
     * <span class="c1">// or</span>
     * <span class="kd">var</span> <span class="nx">db</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">PouchDB</span><span class="p">(</span><span class="s1">&#39;http://localhost:5984/dbname&#39;</span><span class="p">);</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href=
     *      'http://pouchdb.com/api.html#create_database'>http://pouchdb.com/api.html#create_databas
     *      e < / a >
     */
    public static <T extends PouchDocument> PouchDB<T> newPouchDB(Class<T> documentClass, CouchDroidRuntime runtime, 
            String name, boolean autoCompaction) {
        return new PouchDB<T>(documentClass, runtime, name, autoCompaction);
    }
    
    private PouchDB(Class<T> documentClass, CouchDroidRuntime runtime, String name, boolean autoCompaction) {
        this.id = POUCH_IDS.incrementAndGet();
        this.documentClass = documentClass;
        this.runtime = runtime;

        runtime.loadJavascript(new StringBuilder("CouchDroid.pouchDBs[").append(id).append("] = new PouchDB(")
                .append(JsonUtil.simpleMap("name", name, "autoCompaction", autoCompaction)).append(");"));
    }

    /**
     * <p>
     * Delete database with given name
     * </p>
     * 
     * <p>
     * <strong>Notes:</strong> With a remote couch on node options are passed to
     * <a href="https://github.com/mikeal/request">request</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">PouchDB</span><span class="p">.</span><span class="nx">destroy</span><span class="p">(</span><span class="s1">&#39;dbname&#39;</span><span class="p">,</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">info</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * // * </div>
     * 
     * @see <a href=
     *      'http://pouchdb.com/api.html#delete_database'>http://pouchdb.com/api.html#delete_database
     *      < / a >
     */
    public void destroy(StandardCallback callback) {
        destroy(null, callback);
    }

    /**
     * <p>
     * Delete database with given name
     * </p>
     * 
     * <p>
     * <strong>Notes:</strong> With a remote couch on node options are passed to
     * <a href="https://github.com/mikeal/request">request</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">PouchDB</span><span class="p">.</span><span class="nx">destroy</span><span class="p">(</span><span class="s1">&#39;dbname&#39;</span><span class="p">,</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">info</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * // * </div>
     * 
     * @see <a href=
     *      'http://pouchdb.com/api.html#delete_database'>http://pouchdb.com/api.html#delete_database
     *      < / a >
     */
    public void destroy(Map<String, Object> options) {
        destroy(options, null);
    }

    /**
     * <p>
     * Delete database with given name
     * </p>
     * 
     * <p>
     * <strong>Notes:</strong> With a remote couch on node options are passed to
     * <a href="https://github.com/mikeal/request">request</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">PouchDB</span><span class="p">.</span><span class="nx">destroy</span><span class="p">(</span><span class="s1">&#39;dbname&#39;</span><span class="p">,</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">info</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * // * </div>
     * 
     * @see <a href=
     *      'http://pouchdb.com/api.html#delete_database'>http://pouchdb.com/api.html#delete_database
     *      < / a >
     */
    public void destroy() {
        destroy(null, null);
    }

    /**
     * <p>
     * Delete database with given name
     * </p>
     * 
     * <p>
     * <strong>Notes:</strong> With a remote couch on node options are passed to
     * <a href="https://github.com/mikeal/request">request</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">PouchDB</span><span class="p">.</span><span class="nx">destroy</span><span class="p">(</span><span class="s1">&#39;dbname&#39;</span><span class="p">,</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">info</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * // * </div>
     * 
     * @see <a href=
     *      'http://pouchdb.com/api.html#delete_database'>http://pouchdb.com/api.html#delete_database
     *      < / a >
     */
    public void destroy(Map<String, Object> options, StandardCallback callback) {
        loadAction("destroy", options, callback);
        
        // won't need this database anymore
        runtime.loadJavascript(new StringBuilder("delete CouchDroid.pouchDBs[").append(id).append("];"));
    }


    /**
     * 
     <h2>Create / Update a document<a id="create_document"></a></h2>
     * 
     * <h3>Using db.put()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document or update an existing document. If the document
     * already exists you must specify its revision <code>_rev</code>, otherwise
     * a conflict will occur.
     * </p>
     * 
     * <p>
     * There are some restrictions on valid property names of the documents,
     * these are explained <a
     * href="http://wiki.apache.org/couchdb/HTTP_Document_API#Special_Fields"
     * >here</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * 
     * <span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">_rev</span><span class="o">:</span> <span class="s1">&#39;1-A6157A5EA545C99B00FF904EEF05FD9F&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Lets Dance&#39;</span><span class="p">,</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">})</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h4>Example Response:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="p">{</span>
     *   <span class="s2">&quot;ok&quot;</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
     *   <span class="s2">&quot;id&quot;</span><span class="o">:</span> <span class="s2">&quot;mydoc&quot;</span><span class="p">,</span>
     *   <span class="s2">&quot;rev&quot;</span><span class="o">:</span> <span class="s2">&quot;1-A6157A5EA545C99B00FF904EEF05FD9F&quot;</span>
     * <span class="p">}</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h3>Using db.post()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document and let PouchDB generate an <code>_id</code> for
     * it.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">({</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href =
     *      'http://pouchdb.com/api.html#create_document'>http://pouchdb.com/api.html#create_document</
     *      a >
     * @param doc
     * @param callback
     */
    public void put(T doc, Map<String, Object> options, StandardCallback callback) {
        loadAction("put", PouchDocumentMapper.toJson(doc), options, callback);
    }
    
    /**
     * 
     <h2>Create / Update a document<a id="create_document"></a></h2>
     * 
     * <h3>Using db.put()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document or update an existing document. If the document
     * already exists you must specify its revision <code>_rev</code>, otherwise
     * a conflict will occur.
     * </p>
     * 
     * <p>
     * There are some restrictions on valid property names of the documents,
     * these are explained <a
     * href="http://wiki.apache.org/couchdb/HTTP_Document_API#Special_Fields"
     * >here</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * 
     * <span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">_rev</span><span class="o">:</span> <span class="s1">&#39;1-A6157A5EA545C99B00FF904EEF05FD9F&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Lets Dance&#39;</span><span class="p">,</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">})</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h4>Example Response:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="p">{</span>
     *   <span class="s2">&quot;ok&quot;</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
     *   <span class="s2">&quot;id&quot;</span><span class="o">:</span> <span class="s2">&quot;mydoc&quot;</span><span class="p">,</span>
     *   <span class="s2">&quot;rev&quot;</span><span class="o">:</span> <span class="s2">&quot;1-A6157A5EA545C99B00FF904EEF05FD9F&quot;</span>
     * <span class="p">}</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h3>Using db.post()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document and let PouchDB generate an <code>_id</code> for
     * it.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">({</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href =
     *      'http://pouchdb.com/api.html#create_document'>http://pouchdb.com/api.html#create_document</
     *      a >
     * @param doc
     * @param callback
     */
    public void put(T doc, Map<String, Object> options) {
        put(doc, options, null);
    }
    
    /**
     * 
     <h2>Create / Update a document<a id="create_document"></a></h2>
     * 
     * <h3>Using db.put()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document or update an existing document. If the document
     * already exists you must specify its revision <code>_rev</code>, otherwise
     * a conflict will occur.
     * </p>
     * 
     * <p>
     * There are some restrictions on valid property names of the documents,
     * these are explained <a
     * href="http://wiki.apache.org/couchdb/HTTP_Document_API#Special_Fields"
     * >here</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * 
     * <span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">_rev</span><span class="o">:</span> <span class="s1">&#39;1-A6157A5EA545C99B00FF904EEF05FD9F&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Lets Dance&#39;</span><span class="p">,</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">})</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h4>Example Response:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="p">{</span>
     *   <span class="s2">&quot;ok&quot;</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
     *   <span class="s2">&quot;id&quot;</span><span class="o">:</span> <span class="s2">&quot;mydoc&quot;</span><span class="p">,</span>
     *   <span class="s2">&quot;rev&quot;</span><span class="o">:</span> <span class="s2">&quot;1-A6157A5EA545C99B00FF904EEF05FD9F&quot;</span>
     * <span class="p">}</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h3>Using db.post()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document and let PouchDB generate an <code>_id</code> for
     * it.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">({</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href =
     *      'http://pouchdb.com/api.html#create_document'>http://pouchdb.com/api.html#create_document</
     *      a >
     * @param doc
     * @param callback
     */
    public void put(T doc, StandardCallback callback) {
        put(doc, null, callback);
    }
    /**
     * 
     <h2>Create / Update a document<a id="create_document"></a></h2>
     * 
     * <h3>Using db.put()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document or update an existing document. If the document
     * already exists you must specify its revision <code>_rev</code>, otherwise
     * a conflict will occur.
     * </p>
     * 
     * <p>
     * There are some restrictions on valid property names of the documents,
     * these are explained <a
     * href="http://wiki.apache.org/couchdb/HTTP_Document_API#Special_Fields"
     * >here</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * 
     * <span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">_rev</span><span class="o">:</span> <span class="s1">&#39;1-A6157A5EA545C99B00FF904EEF05FD9F&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Lets Dance&#39;</span><span class="p">,</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">})</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h4>Example Response:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="p">{</span>
     *   <span class="s2">&quot;ok&quot;</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
     *   <span class="s2">&quot;id&quot;</span><span class="o">:</span> <span class="s2">&quot;mydoc&quot;</span><span class="p">,</span>
     *   <span class="s2">&quot;rev&quot;</span><span class="o">:</span> <span class="s2">&quot;1-A6157A5EA545C99B00FF904EEF05FD9F&quot;</span>
     * <span class="p">}</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h3>Using db.post()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document and let PouchDB generate an <code>_id</code> for
     * it.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">({</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href =
     *      'http://pouchdb.com/api.html#create_document'>http://pouchdb.com/api.html#create_document</
     *      a >
     * @param doc
     * @param callback
     */
    
    public void put(T doc) {
        put(doc, null, null);
    }
    /**
     * 
     <h2>Create / Update a document<a id="create_document"></a></h2>
     * 
     * <h3>Using db.put()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document or update an existing document. If the document
     * already exists you must specify its revision <code>_rev</code>, otherwise
     * a conflict will occur.
     * </p>
     * 
     * <p>
     * There are some restrictions on valid property names of the documents,
     * these are explained <a
     * href="http://wiki.apache.org/couchdb/HTTP_Document_API#Special_Fields"
     * >here</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * 
     * <span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">_rev</span><span class="o">:</span> <span class="s1">&#39;1-A6157A5EA545C99B00FF904EEF05FD9F&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Lets Dance&#39;</span><span class="p">,</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">})</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h4>Example Response:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="p">{</span>
     *   <span class="s2">&quot;ok&quot;</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
     *   <span class="s2">&quot;id&quot;</span><span class="o">:</span> <span class="s2">&quot;mydoc&quot;</span><span class="p">,</span>
     *   <span class="s2">&quot;rev&quot;</span><span class="o">:</span> <span class="s2">&quot;1-A6157A5EA545C99B00FF904EEF05FD9F&quot;</span>
     * <span class="p">}</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h3>Using db.post()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document and let PouchDB generate an <code>_id</code> for
     * it.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">({</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href =
     *      'http://pouchdb.com/api.html#create_document'>http://pouchdb.com/api.html#create_document</
     *      a >
     * @param doc
     * @param callback
     */
   
    public void post(T doc, Map<String, Object> options, StandardCallback callback) {
        loadAction("post", PouchDocumentMapper.toJson(doc), options, callback);
    }
    /**
     * 
     <h2>Create / Update a document<a id="create_document"></a></h2>
     * 
     * <h3>Using db.put()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document or update an existing document. If the document
     * already exists you must specify its revision <code>_rev</code>, otherwise
     * a conflict will occur.
     * </p>
     * 
     * <p>
     * There are some restrictions on valid property names of the documents,
     * these are explained <a
     * href="http://wiki.apache.org/couchdb/HTTP_Document_API#Special_Fields"
     * >here</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * 
     * <span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">_rev</span><span class="o">:</span> <span class="s1">&#39;1-A6157A5EA545C99B00FF904EEF05FD9F&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Lets Dance&#39;</span><span class="p">,</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">})</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h4>Example Response:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="p">{</span>
     *   <span class="s2">&quot;ok&quot;</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
     *   <span class="s2">&quot;id&quot;</span><span class="o">:</span> <span class="s2">&quot;mydoc&quot;</span><span class="p">,</span>
     *   <span class="s2">&quot;rev&quot;</span><span class="o">:</span> <span class="s2">&quot;1-A6157A5EA545C99B00FF904EEF05FD9F&quot;</span>
     * <span class="p">}</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h3>Using db.post()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document and let PouchDB generate an <code>_id</code> for
     * it.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">({</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href =
     *      'http://pouchdb.com/api.html#create_document'>http://pouchdb.com/api.html#create_document</
     *      a >
     * @param doc
     * @param callback
     */
   
    public void post(T doc, Map<String, Object> options) {
        post(doc, options, null);
    }
    /**
     * 
     <h2>Create / Update a document<a id="create_document"></a></h2>
     * 
     * <h3>Using db.put()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document or update an existing document. If the document
     * already exists you must specify its revision <code>_rev</code>, otherwise
     * a conflict will occur.
     * </p>
     * 
     * <p>
     * There are some restrictions on valid property names of the documents,
     * these are explained <a
     * href="http://wiki.apache.org/couchdb/HTTP_Document_API#Special_Fields"
     * >here</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * 
     * <span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">_rev</span><span class="o">:</span> <span class="s1">&#39;1-A6157A5EA545C99B00FF904EEF05FD9F&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Lets Dance&#39;</span><span class="p">,</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">})</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h4>Example Response:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="p">{</span>
     *   <span class="s2">&quot;ok&quot;</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
     *   <span class="s2">&quot;id&quot;</span><span class="o">:</span> <span class="s2">&quot;mydoc&quot;</span><span class="p">,</span>
     *   <span class="s2">&quot;rev&quot;</span><span class="o">:</span> <span class="s2">&quot;1-A6157A5EA545C99B00FF904EEF05FD9F&quot;</span>
     * <span class="p">}</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h3>Using db.post()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document and let PouchDB generate an <code>_id</code> for
     * it.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">({</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href =
     *      'http://pouchdb.com/api.html#create_document'>http://pouchdb.com/api.html#create_document</
     *      a >
     * @param doc
     * @param callback
     */
    
    public void post(T doc, StandardCallback callback) {
        post(doc, null, callback);
    }
    
     /**
     * 
     <h2>Create / Update a document<a id="create_document"></a></h2>
     * 
     * <h3>Using db.put()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document or update an existing document. If the document
     * already exists you must specify its revision <code>_rev</code>, otherwise
     * a conflict will occur.
     * </p>
     * 
     * <p>
     * There are some restrictions on valid property names of the documents,
     * these are explained <a
     * href="http://wiki.apache.org/couchdb/HTTP_Document_API#Special_Fields"
     * >here</a>.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * 
     * <span class="nx">db</span><span class="p">.</span><span class="nx">put</span><span class="p">({</span>
     *   <span class="nx">_id</span><span class="o">:</span> <span class="s1">&#39;mydoc&#39;</span><span class="p">,</span>
     *   <span class="nx">_rev</span><span class="o">:</span> <span class="s1">&#39;1-A6157A5EA545C99B00FF904EEF05FD9F&#39;</span><span class="p">,</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Lets Dance&#39;</span><span class="p">,</span>
     * <span class="p">},</span> <span class="kd">function</span><span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">})</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h4>Example Response:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="p">{</span>
     *   <span class="s2">&quot;ok&quot;</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
     *   <span class="s2">&quot;id&quot;</span><span class="o">:</span> <span class="s2">&quot;mydoc&quot;</span><span class="p">,</span>
     *   <span class="s2">&quot;rev&quot;</span><span class="o">:</span> <span class="s2">&quot;1-A6157A5EA545C99B00FF904EEF05FD9F&quot;</span>
     * <span class="p">}</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <h3>Using db.post()</h3>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">(</span><span class="nx">doc</span><span class="p">,</span> <span class="p">[</span><span class="nx">options</span><span class="p">],</span> <span class="p">[</span><span class="nx">callback</span><span class="p">])</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * <p>
     * Create a new document and let PouchDB generate an <code>_id</code> for
     * it.
     * </p>
     * 
     * <h4>Example Usage:</h4>
     * 
     * <div class="highlight">
     * 
     * <pre>
     * <code class="js"><span class="nx">db</span><span class="p">.</span><span class="nx">post</span><span class="p">({</span>
     *   <span class="nx">title</span><span class="o">:</span> <span class="s1">&#39;Heroes&#39;</span>
     * <span class="p">},</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">err</span><span class="p">,</span> <span class="nx">response</span><span class="p">)</span> <span class="p">{</span> <span class="p">});</span>
     * </code>
     * </pre>
     * 
     * </div>
     * 
     * @see <a href =
     *      'http://pouchdb.com/api.html#create_document'>http://pouchdb.com/api.html#create_document</
     *      a >
     * @param doc
     * @param callback
     */
    public void post(T doc) {
        post(doc, null, null);
    }    
    
    public void get(String docid, Map<String, Object> options, final GetCallback<T> callback) {
        loadAction("get", JsonUtil.simpleString(docid), options, callback == null ? null : 
            new InnerGetCallback<T>(documentClass) {

            @Override
            public void onCallback(PouchError err, T info) {
                callback.onCallback(err, info);
            }

            @Override
            public Class<?> getPrimaryClass() {
                return documentClass;
            }
        });
    }
    
    public void get(String docid, Map<String, Object> options) {
        get(docid, options, null);
    }

    public void get(String docid, GetCallback<T> callback) {
        get(docid, null, callback);
    }
    
    public void get(String docid) {
        get(docid, null, null);
    }    

    public void remove(T doc, Map<String, Object> options, StandardCallback callback) {
        loadAction("remove", PouchDocumentMapper.toJson(doc), options, callback);
    }
    
    public void remove(T doc, Map<String, Object> options) {
        remove(doc, options, null);
    }
    
    public void remove(T doc, StandardCallback callback) {
        remove(doc, null, callback);
    }
    
    public void remove(T doc) {
        remove(doc, null, null);
    }    
    
    public void bulkDocs(List<T> docs, Map<String, Object> options, BulkCallback callback) {
        
        Map<String, List<T>> map = new LinkedHashMap<String, List<T>>();
        map.put("docs", docs);
        String mapAsJson = JsonUtil.simpleMap(options);
        
        loadAction("bulkDocs", mapAsJson, options, callback);
    }
    
    public void bulkDocs(List<T> docs, Map<String, Object> options) {
        bulkDocs(docs, options, null);
    }
    
    public void bulkDocs(List<T> docs, BulkCallback callback) {
        bulkDocs(docs, null, callback);
    }
    
    public void bulkDocs(List<T> docs) {
        bulkDocs(docs, null, null);
    }
    
    public void allDocs(Map<String, Object> options, final AllDocsCallback<T> callback) {
        loadAction("allDocs", options, callback == null ? null : new AllDocsCallback<T>() {
            
            @Override
            public void onCallback(PouchError err, AllDocsInfo<T> info) {
                callback.onCallback(err, info);
            }

            @Override
            public Class<?> getGenericClass() {
                return documentClass;
            }
        });
    }
    
    public void allDocs(Map<String, Object> options) {
        allDocs(options, null);
    }
    
    public void allDocs(AllDocsCallback<T> callback) {
        allDocs(null, callback);
    }
    
    private void loadAction(String action, Map<String, Object> options, Callback<?> callback) {
        loadAction(action, null, options, callback);
    }
    
    private void loadAction(String action, String arg1, Map<String, Object> options, Callback<?> callback) {
        log.d("loadAction(%s, %s, %s, %s", action, arg1, options, callback);
        
        StringBuilder js = new StringBuilder("CouchDroid.pouchDBs[").append(id).append("].")
                .append(action).append("(");
        
        if (!TextUtils.isEmpty(arg1)) {
            js.append(arg1).append(",");
        }
        if (options != null && !options.isEmpty()) {
            js.append(JsonUtil.simpleMap(options)).append(",");
        }
        
        js.append(getFunctionForCallback(callback)).append(");");
        
        runtime.loadJavascript(js);
    }
    
    @SuppressWarnings("rawtypes")
    private CharSequence getFunctionForCallback(final Callback innerCallback) {

        if (innerCallback == null) {
            // user doesn't give a shit
            return "function(){}";
        }

        // begin cone of death
        int callbackId = PouchJavascriptInterface.INSTANCE.addCallback(new Callback<Object>() {

            @Override
            public void onCallback(final PouchError err, final Object info) {
                Activity activity = runtime.getActivity();
                if (activity != null) {
                    // ensure it runs on the ui thread
                    activity.runOnUiThread(new Runnable() {

                        @SuppressWarnings("unchecked")
                        @Override
                        public void run() {
                            try {
                                innerCallback.onCallback(err, info);
                            } catch (Exception e) {
                                log.e(e, "User-created callback threw an exception");
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }

            @Override
            public Object getPrimaryClass() {
                return innerCallback.getPrimaryClass();
            }
            
            @Override
            public Class<?> getGenericClass() {
                return innerCallback.getGenericClass();
            }
        });

        return new StringBuilder("function(err, info){PouchJavascriptInterface.callback(").append(callbackId).append(
                ", err ? JSON.stringify(err) : null, info ? JSON.stringify(info) : null);}");
    }

    /**
     * A generic callback for interacting with PouchDB.
     * 
     * @author nolan
     * 
     */
    public static interface Callback<E> {

        /**
         * Callback method, which runs on the UI thread.
         * 
         * @param err
         *            if null, there was no error
         * @param info
         *            contains additional information given by PouchDB
         */
        public void onCallback(PouchError err, E info);
        
        public Object getPrimaryClass();
        
        public Class<?> getGenericClass();
    }
    
    public static abstract class BulkCallback implements Callback<List<PouchInfo>> {
        public Object getPrimaryClass() {

            return new TypeReference<List<PouchInfo>>(){};
        }
        public Class<?> getGenericClass() {
            return null;
        }
    }
    
    public static abstract class AllDocsCallback<T extends PouchDocument> implements Callback<AllDocsInfo<T>> {

        @Override
        public Object getPrimaryClass() {
            return AllDocsInfo.class;
        }
        
        public Class<?> getGenericClass() {
            return null; // overridden
        }
        
    }
    
    public static abstract class StandardCallback implements Callback<PouchInfo> {
        public Object getPrimaryClass() {

            return PouchInfo.class;
        }
        public Class<?> getGenericClass() {
            return null;
        }
    }
    public static abstract class GetCallback<T> implements Callback<T> {
        

        public GetCallback() {
        }
        
        public Object getPrimaryClass() {
            return null; // overridden
        }
        
        public Class<?> getGenericClass() {
            return null;
        }
    }
    public static abstract class InnerGetCallback<T> implements Callback<T> {
        
        private Class<T> documentClass;

        public InnerGetCallback(Class<T> documentClass) {
            this.documentClass = documentClass;
        }
        
        public Object getPrimaryClass() {
            return documentClass;
        }
        
        public Class<?> getGenericClass() {
            return null;
        }
    }
}
