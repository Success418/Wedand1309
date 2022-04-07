package co.za.wedwise.checklist.database;

import android.database.sqlite.SQLiteDatabase;

public class Data {
    public static final String TABLE_BUDGETS = "budgets";
    public static final String TABLE_GUESTS = "guests";
    public static final String TABLE_INFO = "info";
    public static final String TABLE_ITEMS = "items";
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_TOPICS = "topics";

    public static void insertTopics(SQLiteDatabase db) {
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(101,'Stationery','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(102,'Attire','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(103,'Rings','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(104,'Ceremony','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(105,'Reception','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(106,'Transport','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(107,'Photos and video','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(108,'Flowers','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(109,'Gifts and favors','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(110,'Rehearsal Dinner','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(111,'Honeymoon','0','0','0','T');");
        db.execSQL("insert into topics(topic_id,title,budgeted,vendor,spent,status) VALUES(999,'Misc','0','0','0','T');");
    }

    public static void insertTasks(SQLiteDatabase db) {
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(101,'Stationery, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(102,'Attire, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(103,'Rings, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(104,'Ceremony, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(105,'Reception, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(106,'Transport, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(107,'Photos and video, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(108,'Flowers, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(109,'Gifts and favors, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(110,'Rehearsal Dinner, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(111,'Honeymoon, things to do','T');");
        db.execSQL("insert into tasks(topic_id,title,status) VALUES(999,'Misc, things to do','T');");
    }

    public static void insertItems(SQLiteDatabase db) {
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Invitations','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Announcements','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Map/direction cards','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Reply cards','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Ceremony cards','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Save the date cards','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Thank you notes','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Rehearsal dinner invitations','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Bridesmaid luncheon invitations','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Bacholor party invitations','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(101,'Wedding programs','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(102,'Bride''s attire','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(102,'Bridesmaids'' attires','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(102,'Groom''s attire','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(102,'Groomsmen''s attires','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(102,'Children''s apparel','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(102,'Honeymoon clothes','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(102,'Going-away outfit','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(103,'Wedding bands','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(103,'Engagement ring','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(103,'Engraving','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Location fee','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Equipments rental','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Decorations','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Guest book/pen','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Ring bearer pillow','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Aisle runner','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Clergy','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Ushers','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Gratuity','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Transportations and Parking','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(104,'Childcare','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Location fee','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Equipments rental','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Decorations','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Food','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Liquor','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Caterer','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Baker','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Musician','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Bartender','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Servers','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Security','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Wedding cake','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Guest book/pen','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Entertainment','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Trnasportations and Parking','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Limousine/Carriage','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(105,'Childcare','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(106,'Main car','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(106,'Guest cars','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(106,'Taxi','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(106,'Parking','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(107,'Bridal portraits','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(107,'Reception','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(107,'Ceremony','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(107,'Photo albums','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(107,'Engagement portraits','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(107,'Event prints','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(107,'Main video','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(108,'Brides bouquets','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(108,'Bridesmaids boutquets','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(108,'Corsages','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(108,'Boutonniere','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(108,'Altarpiece','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(108,'Reception centerpieces','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(108,'Pew/chair bows','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(108,'Throw away bouquet','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(108,'Flower girl''s flowers','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(109,'Attendant gifts','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(109,'Gift for fiance','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(109,'Favors','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Food','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Liquor','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Caterer','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Bartender','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Equipment rental','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Decorations','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Security','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Entertainment','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Guest parking','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(110,'Servers','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(111,'Air fare','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(111,'Accommodations','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(111,'Rental Car','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(111,'Entertainment','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(999,'Marriage license','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(999,'Bridesmaid luncheon','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(999,'Make-up artist','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(999,'Hair stylist','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(999,'Wedding planner/organizer','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(999,'Consultant/coordinator','T');");
        db.execSQL("insert into items(topic_id,title,status) VALUES(999,'Accommodations for guests','T');");
    }

    public static void insertBudgets(SQLiteDatabase db) {
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(101,'Stationery','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(102,'Attire','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(103,'Rings','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(104,'Ceremony','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(105,'Reception','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(106,'Transport','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(107,'Photos','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(107,'Video','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(108,'Flowers','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(109,'Gifts','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(109,'Favors','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(110,'Rehearsal Dinner','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(111,'Honeymoon','0','0','0','T');");
        db.execSQL("insert into budgets(topic_id,title,budgeted,vendor,spent,status) VALUES(999,'Misc','0','0','0','T');");
    }

    public static void insertGuests(SQLiteDatabase db) {
        db.execSQL("insert into guests(guest_id,firstname,lastname,side,invitessent,attending) VALUES(1,'John','Doe','Groom','Yes','Yes');");
        db.execSQL("insert into guests(guest_id,firstname,lastname,side,invitessent,attending) VALUES(2,'Jane','Doe','Bride','Yes','Undecided');");
    }
}
