package co.za.wedwise.checklist;

import android.net.Uri;

public class Config {
    public static final String AUTHORITY = "co.za.wedwise.checklist.ContentProvider";
    public static final Uri CONTENT_URI_BUDGETS = Uri.parse("content://co.za.wedwise.checklist.ContentProvider/budgets");
    public static final Uri CONTENT_URI_GUESTS = Uri.parse("content://co.za.wedwise.checklist.ContentProvider/guests");
    public static final Uri CONTENT_URI_INFO = Uri.parse("content://co.za.wedwise.checklist.ContentProvider/info");
    public static final Uri CONTENT_URI_ITEMS = Uri.parse("content://co.za.wedwise.checklist.ContentProvider/items");
    public static final Uri CONTENT_URI_SCHEDULES = Uri.parse("content://co.za.wedwise.checklist.ContentProvider/schedules");
    public static final Uri CONTENT_URI_TASKS = Uri.parse("content://co.za.wedwise.checklist.ContentProvider/tasks");
    public static final Uri CONTENT_URI_TOPICS = Uri.parse("content://co.za.wedwise.checklist.ContentProvider/topics");
    public static final Uri CONTENT_URI_VENDORS = Uri.parse("content://co.za.wedwise.checklist.ContentProvider/vendors");
    public static final String cachePath = "/data/co.za.wedwise.checklist/cache/.nomedia";
    public static final String TITLE = "title";
}
