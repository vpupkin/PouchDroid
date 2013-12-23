PouchDroid
===========

Effortlessly sync your data across multiple Android devices, using [PouchDB][] and [CouchDB][].

**Version 0.9.0**

Introduction
-------------

### What is this?

PouchDroid is an Android adapter for [PouchDB][].  It offers a simple key-value store, backed by SQLite, that can automatically sync with a remote [CouchDB][] database via HTTP/HTTPS.

### Why do I care?

Syncing is hard.  You don't want to have to manage revisions, conflicts, and incremental changes yourself.  CouchDB/PouchDB will handle all that junk for you, so you can devote your brain cells to other problems.

Also, ORM is hard.  So instead of forcing you to write SQL or add ```@AnnoyingAnnotations```, PouchDroid lets you persist your plain old Java objects as JSON. And it uses the same API as PouchDB, which is the same API as CouchDB.  Fewer APIs == fewer brain cells wasted.

### Why Couch/Pouch?

CouchDB is awesome.  If you use its built-in user authentication, you can write Ajax apps with barely any server code at all (if any).  PouchDB is awesome too - it runs cross-browser, and it offers automagical two-way sync between the client(s) and server.

Examples
----------

Your activities extend ```PouchDroidActivity```:

```java
public class MyActivity extends PouchDroidActivity {
  public void onPouchDroidReady(PouchDroid pouchDroid) {
    // do stuff
  }
}
```

Your POJOs extend ```PouchDocument```:

```java
public class Meme extends PouchDocument {
  String name;
  String description;
  // getters and setters (must have a bare constructor)
}
```

You create a database and add POJOs:

```java
PouchDB<Meme> pouch = PouchDB.newPouchDB(Meme.class, pouchDroid, "memes.db");
pouch.post(new Meme("Doge", "Much database, very JSON"));
pouch.post(new Meme("AYB", "All your sync are belong to PouchDB"));
```

Then you set up continuous bidirectional sync with CouchDB:

```java
pouchDB.replicateTo("http://mysite.com:5984", true);
pouchDB.replicateFrom("http://mysite.com:5984", true)
```

You'll never have to touch ```SQLiteOpenHelper``` again.  And if you want to
write a companion webapp with PouchDB, your data is already ready to be synced.

More questions
-------------

### How does this work?

Rather than rewrite PouchDB in Java, PouchDroid fires up an invisible WebView, which it basically uses as a JavaScript interpreter to run PouchDB with its WebSQL adapter.  Calls to WebSQL are rerouted to the native Android SQLite API, while calls to XMLHttpRequest are rerouted to the Apache HttpClient API.  [Jackson][] is used for JSON serialization/deserialization.

### Isn't the performance terrible?

Not really.  Since most of the heavy lifting is done in SQLite/HTTP, relatively little code is executed on the UI thread.  It even manages to run on my vintage HTC Magic (2008) rocking Android 2.1 Eclair.  And with Chrome included as the standard WebView in 4.4 KitKat, it's only gonna get faster.

### Why not just use Cordova/PhoneGap?

I thought it would be overkill to include all the Cordova libraries.  Plus, standard Android Cordova apps run the WebSQL requests on the UI thread, meaning that even a spinning progress bar would stutter (PSA: there's [a Cordova plugin for that][1]).  And getting Ajax to work would require tedious configuration of CORS/JSONP to get around web security, whereas PouchDroid works on a freshly-installed CouchDB.

Also PouchDroid doesn't have any external dependencies, other than PouchDB and Jackson.  The APK clocks in at about 700K (400K with ProGuard).

### But I already store user data in SQLite!

PouchDroid includes a small utility called ```PouchDroidMigrationTask```, which can migrate your existing SQLite tables into a reasonable key-value format.  So, if you don't want to dive head-first into Pouch, you can use it purely for one-way sync to CouchDB.

Limitations
-----------

1. PouchDroid needs a WebView in order to run JavaScript.  Hence, you can't use it in a background Service, and it does consume UI thread cycles.  For small databases, though, you probably won't notice.
2. Actually, that's the only limitation.

Android 2.1 (API level 7) and up is supported.

Tutorials
----------

TODO

License
----------

Apache 2.0

Author
--------
Nolan Lawson

[1]: https://github.com/pgsqlite/PG-SQLitePlugin-Android-2013.09
[2]: http://guide.couchdb.org/draft/conflicts.html
[3]: http://tritarget.org/blog/2012/11/28/the-pyramid-of-doom-a-javascript-style-trap/]
[pouchdb]: http://pouchdb.com/
[couchdb]: http://couchdb.apache.org/
[jackson]: http://jackson.codehaus.org/
