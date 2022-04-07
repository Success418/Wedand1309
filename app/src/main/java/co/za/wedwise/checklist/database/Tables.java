package co.za.wedwise.checklist.database;

import android.database.sqlite.SQLiteDatabase;

public class Tables {
    private static final String CREATE_TABLE_BUDGETS = "create table if not exists budgets(budget_id integer primary key autoincrement, topic_id, title, note, budgeted, vendor, spent, tags, status);";
    private static final String CREATE_TABLE_GUESTS = "create table if not exists guests(guest_id integer primary key autoincrement, family_id, firstname, middlename, lastname, phone, email, side, invitessent, attending, tags, status);";
    private static final String CREATE_TABLE_INFO = "create table info(info_id integer primary key autoincrement, bridename, groomname, date);";
    private static final String CREATE_TABLE_ITEMS = "create table if not exists items(item_id integer primary key autoincrement, topic_id, title, note, tags, status);";
    private static final String CREATE_TABLE_SCHEDULES = "create table if not exists schedules(schedule_id integer primary key autoincrement, topic_id, title, note, ddate, dtime, tags, status);";
    private static final String CREATE_TABLE_TASKS = "create table if not exists tasks(task_id integer primary key autoincrement, topic_id, title, note, tags, status);";
    private static final String CREATE_TABLE_TOPICS = "create table if not exists topics(topic_id integer primary key autoincrement, title, note, budgeted, vendor, spent, tags, status);";
    private static final String CREATE_TABLE_VENDORS = "create table if not exists vendors(vendor_id integer primary key autoincrement, topic_id, title, note, contact, phone, email, website, address, tags, status);";
    private static final String PRAGMA_SETTING = "PRAGMA auto_vacuum = 1;";
    public static final String TABLE_BUDGETS = "budgets";
    public static final String TABLE_GUESTS = "guests";
    public static final String TABLE_INFO = "info";
    public static final String TABLE_ITEMS = "items";
    public static final String TABLE_SCHEDULES = "schedules";
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_TOPICS = "topics";
    public static final String TABLE_VENDORS = "vendors";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(PRAGMA_SETTING);
        db.execSQL(CREATE_TABLE_INFO);
        db.execSQL(CREATE_TABLE_TOPICS);
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_ITEMS);
        db.execSQL(CREATE_TABLE_BUDGETS);
        db.execSQL(CREATE_TABLE_SCHEDULES);
        db.execSQL(CREATE_TABLE_VENDORS);
        db.execSQL(CREATE_TABLE_GUESTS);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
