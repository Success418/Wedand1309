package co.za.wedwise.checklist.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class WPContentProvider extends ContentProvider {
    SQLiteDatabase db;
    WPSQLiteOpenHelper helper;

    public boolean onCreate() {
        this.helper = new WPSQLiteOpenHelper(getContext());
        this.db = this.helper.getWritableDatabase();
        return true;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = this.helper.getReadableDatabase();
        qb.setTables(uri.getLastPathSegment());
        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    public Uri insert(Uri uri, ContentValues arg1) {
        this.db = this.helper.getWritableDatabase();
        long rowId = this.db.insert(uri.getLastPathSegment(), "", arg1);
        if (rowId > 0) {
            Uri rowUri = ContentUris.appendId(uri.buildUpon(), rowId).build();
            getContext().getContentResolver().notifyChange(rowUri, null);
            return rowUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    public int delete(Uri uri, String arg1, String[] arg2) {
        return this.db.delete(uri.getLastPathSegment(), arg1, arg2);
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return this.db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
    }
}
