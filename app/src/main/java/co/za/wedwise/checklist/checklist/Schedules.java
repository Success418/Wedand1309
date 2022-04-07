package co.za.wedwise.checklist.checklist;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import co.za.wedwise.R;
import co.za.wedwise.checklist.Common;
import co.za.wedwise.checklist.Config;
import java.util.HashMap;

public class Schedules extends Activity {
    public static final Uri CONTENT_URI_INFO = Config.CONTENT_URI_INFO;
    public static final Uri CONTENT_URI_SCHEDULES = Config.CONTENT_URI_SCHEDULES;
    private static String orderbyDate = "ddate asc, title asc, status desc";
    private static String orderby = orderbyDate;
    private static String orderbyStatus = "status desc, ddate asc, title asc";
    private static String orderbyTitle = "title asc, ddate asc, status desc";
    private ListView ListViewSchedules;
    Activity act = this;
    ContentResolver cr;
    private int positionToMove = 0;
    private int scrollto = 0;
    private int topic_id = 0;
    private String topic_title;

    class ScheduleListAdapter extends BaseAdapter {
        HashMap<String, String> hm;
        LayoutInflater inflater;
        public int listCount = 0;
        Context mContext;
        int mListLayout;

        public ScheduleListAdapter(Context tContext, int listLayout, HashMap<String, String> tmpHm) {
            this.mContext = tContext;
            this.mListLayout = listLayout;
            this.hm = tmpHm;
            this.inflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
            this.listCount = Integer.parseInt(((String) this.hm.get("count")).toString());
        }

        public int getCount() {
            return this.listCount;
        }

        public Object getItem(int arg0) {
            return Integer.valueOf(arg0);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.inflater.inflate(this.mListLayout, parent, false);
            }
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            checkBox.setButtonDrawable(R.drawable.custom_checkbox);
            String status = (String) this.hm.get("status[" + position + "]");
            checkBox.setChecked(status.equals("D"));
            checkBox.setClickable(false);
            checkBox.setFocusable(false);
            checkBox.setEnabled(status.equals("T"));
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView note = (TextView) convertView.findViewById(R.id.note);
            TextView ddate = (TextView) convertView.findViewById(R.id.ddate);
            TextView dday = (TextView) convertView.findViewById(R.id.dday);
            String titlestr = (String) this.hm.get("title[" + position + "]");
            String notestr = (String) this.hm.get("note[" + position + "]");
            String ddatestr = (String) this.hm.get("ddate[" + position + "]");
            String disDday = "";
            int disDdayColor = -1;
            String disDdate = "";
            if (Common.checkValue(ddatestr)) {
                int diffDday = Common.getDday(Integer.parseInt(ddatestr.substring(0, 4)), Integer.parseInt(ddatestr.substring(5, 7)), Integer.parseInt(ddatestr.substring(8, 10)));
                if (diffDday < 0) {
                    disDday = String.format("D-%d", new Object[]{Integer.valueOf(-diffDday)});
                    disDdayColor = -10066177;
                } else if (diffDday > 0) {
                    disDday = String.format("D+%d", new Object[]{Integer.valueOf(diffDday)});
                    disDdayColor = -39322;
                } else {
                    disDday = "D-day";
                    disDdayColor = -13382605;
                }
                //disDdate = Common.convUSDate(ddatestr);
                disDdate = "test";
            }
            title.setText(titlestr);
            title.setTextColor(status.equals("T") ? -14540254 : -6710887);
            ddate.setText(disDdate);
            dday.setText(disDday);
            dday.setTextColor(disDdayColor);
            if (Common.checkValue(notestr)) {
                note.setVisibility(0);
                note.setText(notestr);
                note.setTextColor(status.equals("T") ? -12303292 : -6710887);
            } else {
                note.setVisibility(8);
            }
            return convertView;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedules);
        Intent intent = getIntent();
        this.topic_id = intent.getIntExtra("topic_id", 0);
        this.topic_title = intent.getStringExtra("topic_title");
        this.cr = getContentResolver();
        setTitle();
        refreshList();
    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }

    public void orderbyTitle(View v) {
        orderby = orderbyTitle;
        refreshList();
    }

    public void orderbyStatus(View v) {
        orderby = orderbyStatus;
        refreshList();
    }

    public void orderbyDate(View v) {
        orderby = orderbyDate;
        refreshList();
    }

    public void setTitle() {
        ((TextView) this.act.findViewById(R.id.listTitle)).setText(this.topic_title);
    }

    public void refreshList() {
        loadList();
    }

    public void loadList() {
        Cursor cursor = this.cr.query(CONTENT_URI_SCHEDULES, null, "topic_id = " + this.topic_id, null, orderby);
        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        ScheduleListAdapter listAdapter = new ScheduleListAdapter(this.act, R.layout.schedule, hm);
        this.ListViewSchedules = (ListView) this.act.findViewById(R.id.listSchedules);
        this.ListViewSchedules.setAdapter(listAdapter);
        this.ListViewSchedules.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int schedule_id = Integer.parseInt((String) hm.get("schedule_id[" + position + "]"));
                Schedules.this.scrollto = view.getTop();
                Schedules.this.positionToMove = position;
                Schedules.this.toggleStatus(schedule_id);
                Schedules.this.loadList();
                Schedules.this.scrollList();
            }
        });
        this.ListViewSchedules.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int schedule_id = Integer.parseInt((String) hm.get("schedule_id[" + position + "]"));
                final String title = (String) hm.get("title[" + position + "]");
                final String note = (String) hm.get("note[" + position + "]");
                final String ddate = (String) hm.get("ddate[" + position + "]");
                Schedules.this.scrollto = view.getTop();
                Schedules.this.positionToMove = position;
                final Dialog dialog = new Dialog(Schedules.this.act);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.list_manage);
                TextView editBtn = (TextView) dialog.findViewById(R.id.editBtn);
                TextView deleteBtn = (TextView) dialog.findViewById(R.id.deleteBtn);
                ((TextView) dialog.findViewById(R.id.selctedItem)).setText(title);
                editBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Schedules.this.openUpdateScheduleDialog(schedule_id, title, note, ddate);
                        dialog.dismiss();
                    }
                });
                deleteBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Schedules.this.deleteSchedule(schedule_id);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    public void scrollList() {
        this.ListViewSchedules.setSelectionFromTop(this.positionToMove, this.scrollto);
    }

    public void toggleStatus(int schedule_id) {
        String selection = "schedule_id=" + schedule_id;
        Cursor cursor = this.cr.query(CONTENT_URI_SCHEDULES, null, selection, null, null);
        HashMap<String, String> tmphm = Common.cursor2HashMap(cursor);
        cursor.close();
        String newStatus = ((String) tmphm.get("status[0]")).equals("T") ? "D" : "T";
        ContentValues contVal = new ContentValues();
        contVal.put("status", newStatus);
        this.cr.update(CONTENT_URI_SCHEDULES, contVal, selection, null);
    }

    public void openNewScheduleDialog(View view) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.schedule_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        final TextView ntv = (TextView) dialog.findViewById(R.id.noteEditText);
        final TextView dytv = (TextView) dialog.findViewById(R.id.yearEditText);
        final TextView dmtv = (TextView) dialog.findViewById(R.id.monthEditText);
        final TextView ddtv = (TextView) dialog.findViewById(R.id.dateEditText);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                String note = ntv.getText().toString();
                String year = Common.genYear(dytv.getText().toString());
                String month = Common.twoNumber(dmtv.getText().toString());
                String date = Common.twoNumber(ddtv.getText().toString());
                String ddate = String.format("%s-%s-%s", new Object[]{year, month, date});
                if (!Common.checkValidDate(ddate)) {
                    Schedules.this.showErrMSG(12);
                } else if (Common.checkValue(title)) {
                    Schedules.this.insertSchedule(title, note, ddate);
                    Schedules.this.showMSG(10);
                    Schedules.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Schedules.this.refreshList();
                } else {
                    Schedules.this.showErrMSG(11);
                }
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Schedules.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openUpdateScheduleDialog(int t_id, String title, String note, String ddate) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.schedule_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        final TextView ntv = (TextView) dialog.findViewById(R.id.noteEditText);
        final TextView dytv = (TextView) dialog.findViewById(R.id.yearEditText);
        final TextView dmtv = (TextView) dialog.findViewById(R.id.monthEditText);
        final TextView ddtv = (TextView) dialog.findViewById(R.id.dateEditText);
        String year = ddate.substring(0, 4);
        String month = ddate.substring(5, 7);
        String date = ddate.substring(8, 10);
        tv.setText(title);
        ntv.setText(note);
        dytv.setText(year);
        dmtv.setText(month);
        ddtv.setText(date);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        final int schedule_id = t_id;
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                String note = ntv.getText().toString();
                String year = Common.genYear(dytv.getText().toString());
                String month = Common.twoNumber(dmtv.getText().toString());
                String date = Common.twoNumber(ddtv.getText().toString());
                String ddate = String.format("%s-%s-%s", new Object[]{year, month, date});
                if (!Common.checkValidDate(ddate)) {
                    Schedules.this.showErrMSG(12);
                } else if (Common.checkValue(title)) {
                    Schedules.this.updateSchedule(schedule_id, title, note, ddate);
                    Schedules.this.showMSG(20);
                    Schedules.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Schedules.this.refreshList();
                    Schedules.this.scrollList();
                } else {
                    Schedules.this.showErrMSG(11);
                }
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Schedules.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void insertSchedule(String title, String note, String ddate) {
        ContentValues contVal = new ContentValues();
        contVal.put("topic_id", Integer.valueOf(this.topic_id));
        contVal.put(Config.TITLE, title);
        contVal.put("note", note);
        contVal.put("ddate", ddate);
        contVal.put("status", "T");
        this.cr.insert(CONTENT_URI_SCHEDULES, contVal);
    }

    public void updateSchedule(int schedule_id, String title, String note, String ddate) {
        ContentValues contVal = new ContentValues();
        contVal.put(Config.TITLE, title);
        contVal.put("note", note);
        contVal.put("ddate", ddate);
        this.cr.update(CONTENT_URI_SCHEDULES, contVal, "schedule_id=" + schedule_id, null);
    }

    public void deleteSchedule(int schedule_id) {
        this.cr.delete(CONTENT_URI_SCHEDULES, "schedule_id=" + schedule_id, null);
        refreshList();
        scrollList();
    }

    public void showMSG(int msgcode) {
        String msg;
        if (msgcode == 10) {
            msg = getResources().getString(R.string.msg_added);
        } else if (msgcode == 20) {
            msg = getResources().getString(R.string.msg_edited);
        } else {
            msg = "";
        }
        showToast(msg);
    }

    public void showErrMSG(int errcode) {
        String msg;
        if (errcode == 11) {
            msg = getResources().getString(R.string.err_title);
        } else if (errcode == 12) {
            msg = getResources().getString(R.string.err_invailddate);
        } else {
            msg = "";
        }
        showToast(msg);
    }

    public void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, 0);
        toast.setGravity(17, 0, 0);
        toast.show();
    }

    public void hideSoftKeyboard(View view) {
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            gotoBack(null);
        }
        return true;
    }

    public void gotoBack(View view) {
        this.act.onBackPressed();
    }

    public void gotoTasks(View v) {
        this.act.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        Intent intent = new Intent(this.act, Tasks.class);
        intent.putExtra("topic_id", this.topic_id);
        intent.putExtra("topic_title", this.topic_title);
        this.act.startActivity(intent);
    }

    public void gotoItems(View v) {
        this.act.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        Intent intent = new Intent(this.act, Items.class);
        intent.putExtra("topic_id", this.topic_id);
        intent.putExtra("topic_title", this.topic_title);
        this.act.startActivity(intent);
    }

    public void gotoBudgets(View v) {
        this.act.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        Intent intent = new Intent(this.act, Budgets.class);
        intent.putExtra("topic_id", this.topic_id);
        intent.putExtra("topic_title", this.topic_title);
        this.act.startActivity(intent);
    }

    public void gotoSchedules(View v) {
        this.act.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        Intent intent = new Intent(this.act, Schedules.class);
        intent.putExtra("topic_id", this.topic_id);
        intent.putExtra("topic_title", this.topic_title);
        this.act.startActivity(intent);
    }

    public void gotoVendors(View v) {
        this.act.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        Intent intent = new Intent(this.act, Vendors.class);
        intent.putExtra("topic_id", this.topic_id);
        intent.putExtra("topic_title", this.topic_title);
        this.act.startActivity(intent);
    }
}
