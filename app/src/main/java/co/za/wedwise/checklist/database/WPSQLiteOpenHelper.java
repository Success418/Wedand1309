package co.za.wedwise.checklist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import java.io.File;

public class WPSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_FILE = "weddingchecklist.db";
    public static final int DATABASE_VERSION = 1;
    static File EX_STORAGE = Environment.getExternalStorageDirectory();
    static File EX_DATABASE_FILE = new File(EX_STORAGE, "/data/co.za.wedwise.checklist/databases/weddingchecklist.db");
    static File EX_DATABASE_PATH = new File(EX_STORAGE + "/data/co.za.wedwise.checklist/databases", "");
    static String DATABASE_FILENAME = getDatabaseFilename();

    public WPSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILENAME, null, 1);
    }

    public static String getDatabaseFilename() {
        if (!EX_STORAGE.canWrite()) {
            return DATABASE_FILE;
        }
        if (EX_DATABASE_PATH.exists()) {
            return EX_DATABASE_FILE.toString();
        }
        EX_DATABASE_PATH.mkdirs();
        return EX_DATABASE_FILE.toString();
    }

    public void onCreate(SQLiteDatabase db) {
        Tables.onCreate(db);
        Data.insertTopics(db);
        Data.insertTasks(db);
        Data.insertItems(db);
        Data.insertBudgets(db);
//        Data.insertGuests(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Tables.onUpgrade(db, oldVersion, newVersion);
    }
}
