package co.za.wedwise.checklist.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import co.za.wedwise.R;
import co.za.wedwise.checklist.Common;
import co.za.wedwise.checklist.Config;
import co.za.wedwise.checklist.checklist.Schedules;
import co.za.wedwise.checklist.database.WPSQLiteOpenHelper;
import java.util.HashMap;

public class DashboardSchedules extends Activity {
    public static final Uri CONTENT_URI_INFO = Config.CONTENT_URI_INFO;
    public static final Uri CONTENT_URI_SCHEDULES = Config.CONTENT_URI_SCHEDULES;
    private static String filter;
    private static String filterAll = " ";
    static String todaystr = Common.getTodayStr();
    private static String filterBeforetoday = (" where schedules.ddate < '" + todaystr + "' ");
    private static String filterFromthisday = (" where schedules.ddate >= '" + todaystr + "' ");
    private ListView ListViewSchedules;
    Activity act = this;
    ContentResolver cr;
    SQLiteDatabase db;
    WPSQLiteOpenHelper helper;
    private int positionToMove = 0;
    private int scrollto = 0;

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
            TextView topictitle = (TextView) convertView.findViewById(R.id.topictitle);
            TextView ddate = (TextView) convertView.findViewById(R.id.ddate);
            TextView dday = (TextView) convertView.findViewById(R.id.dday);
            String topictitlestr = (String) this.hm.get("topic_title[" + position + "]");
            String titlestr = (String) this.hm.get("title[" + position + "]");
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
            topictitle.setText(topictitlestr);
            title.setText(titlestr);
            title.setTextColor(status.equals("T") ? -14540254 : -6710887);
            ddate.setText(disDdate);
            dday.setText(disDday);
            dday.setTextColor(disDdayColor);
            return convertView;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_schedules);
        this.cr = getContentResolver();
        showInfo();
        refreshList();
    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }

    public void filterFromthisday(View v) {
        filter = filterFromthisday;
        refreshList();
    }

    public void filterBeforetoday(View v) {
        filter = filterBeforetoday;
        refreshList();
    }

    public void filterAll(View v) {
        filter = filterAll;
        refreshList();
    }

    public void refreshList() {
        loadList();
    }

    public void refreshList(View v) {
        loadList();
    }

    public void showInfo() {
        TextView dday = (TextView) this.act.findViewById(R.id.dday);
        TextView info = (TextView) this.act.findViewById(R.id.info);
        TextView noinfo = (TextView) this.act.findViewById(R.id.noInfo);
        TextView btnnewinfo = (TextView) this.act.findViewById(R.id.btnNewInfo);
        String ddaystr = null;
        Cursor cursor = this.cr.query(CONTENT_URI_INFO, null, null, null, null);
        if (cursor.getCount() == 0) {
            dday.setVisibility(8);
            info.setVisibility(8);
            noinfo.setVisibility(0);
            btnnewinfo.setVisibility(0);
            noinfo.setText("Set up your wedding date.");
        } else {
            String infostr;
            dday.setVisibility(0);
            info.setVisibility(0);
            noinfo.setVisibility(8);
            btnnewinfo.setVisibility(8);
            cursor.moveToFirst();
            final String bridename = cursor.getString(1);
            final String groomname = cursor.getString(2);
            String weddingdate = cursor.getString(3);
            int ddayColor = 0;
            if (Common.checkValue(weddingdate)) {
                int diffdday = Common.getDday(weddingdate);
                if (diffdday < 0) {
                    ddayColor = -10066177;
                    ddaystr = String.format("D%d", new Object[]{Integer.valueOf(diffdday)});
                    //infostr = Common.convUSDate(weddingdate);
                    infostr = "test";
                } else if (diffdday > 0) {
                    ddayColor = -39322;
                    ddaystr = String.format("D+%d", new Object[]{Integer.valueOf(diffdday)});
                    //infostr = Common.convUSDate(weddingdate);
                    infostr = "test";
                } else {
                    ddaystr = "D-day";
                    ddayColor = -13382605;
                    infostr = String.format("The Wedding Day.", new Object[0]);
                }
            } else {
                infostr = String.format("Set up your wedding date.", new Object[0]);
            }
            dday.setText(ddaystr);
            dday.setTextColor(ddayColor);
            info.setText(infostr);
            final String str = weddingdate;
            //str = weddingdate;
            dday.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    DashboardSchedules.this.openUpdateInfoDialog(groomname, bridename, str);
                }
            });
            info.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    DashboardSchedules.this.openUpdateInfoDialog(groomname, bridename, str);
                }
            });
        }
        cursor.close();
    }

    public void openNewInfoDialog(View view) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.schedule_info_form);
        final TextView gtv = (TextView) dialog.findViewById(R.id.groomEditText);
        final TextView btv = (TextView) dialog.findViewById(R.id.brideEditText);
        final TextView dytv = (TextView) dialog.findViewById(R.id.yearEditText);
        final TextView dmtv = (TextView) dialog.findViewById(R.id.monthEditText);
        final TextView ddtv = (TextView) dialog.findViewById(R.id.dateEditText);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String groomname = gtv.getText().toString();
                String bridename = btv.getText().toString();
                String year = Common.genYear(dytv.getText().toString());
                String month = Common.twoNumber(dmtv.getText().toString());
                String date = Common.twoNumber(ddtv.getText().toString());
                String weddingdate = String.format("%s-%s-%s", new Object[]{year, month, date});
                if (!Common.checkValue(groomname) || !Common.checkValue(bridename)) {
                    DashboardSchedules.this.showErrMSG(91);
                } else if (!Common.checkValue(weddingdate)) {
                    DashboardSchedules.this.insertInfo(groomname, bridename, null);
                    DashboardSchedules.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    DashboardSchedules.this.showInfo();
                } else if (Common.checkValidDate(weddingdate)) {
                    DashboardSchedules.this.insertInfo(groomname, bridename, weddingdate);
                    DashboardSchedules.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    DashboardSchedules.this.showInfo();
                } else {
                    DashboardSchedules.this.showErrMSG(22);
                }
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DashboardSchedules.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openUpdateInfoDialog(String groomname, String bridename, String weddingdate) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.schedule_info_form);
        final TextView gtv = (TextView) dialog.findViewById(R.id.groomEditText);
        final TextView btv = (TextView) dialog.findViewById(R.id.brideEditText);
        final TextView dytv = (TextView) dialog.findViewById(R.id.yearEditText);
        final TextView dmtv = (TextView) dialog.findViewById(R.id.monthEditText);
        final TextView ddtv = (TextView) dialog.findViewById(R.id.dateEditText);
        TextView formOKBtn = (TextView) dialog.findViewById(R.id.formOKBtn);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        String year = weddingdate.substring(0, 4);
        String month = weddingdate.substring(5, 7);
        String date = weddingdate.substring(8, 10);
        gtv.setText(groomname);
        btv.setText(bridename);
        dytv.setText(year);
        dmtv.setText(month);
        ddtv.setText(date);
        formOKBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String groomname = gtv.getText().toString();
                String bridename = btv.getText().toString();
                String year = Common.genYear(dytv.getText().toString());
                String month = Common.twoNumber(dmtv.getText().toString());
                String date = Common.twoNumber(ddtv.getText().toString());
                String weddingdate = String.format("%s-%s-%s", new Object[]{year, month, date});
                if (!Common.checkValue(groomname) || !Common.checkValue(bridename)) {
                    DashboardSchedules.this.showErrMSG(91);
                } else if (!Common.checkValue(weddingdate)) {
                    DashboardSchedules.this.updateInfo(groomname, bridename, null);
                    DashboardSchedules.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    DashboardSchedules.this.showInfo();
                } else if (Common.checkValidDate(weddingdate)) {
                    DashboardSchedules.this.updateInfo(groomname, bridename, weddingdate);
                    DashboardSchedules.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    DashboardSchedules.this.showInfo();
                } else {
                    DashboardSchedules.this.showErrMSG(22);
                }
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DashboardSchedules.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void insertInfo(String groomname, String bridename, String weddingdate) {
        ContentValues contVal = new ContentValues();
        contVal.put("bridename", bridename);
        contVal.put("groomname", groomname);
        contVal.put("date", weddingdate);
        this.cr.insert(CONTENT_URI_INFO, contVal);
    }

    public void updateInfo(String groomname, String bridename, String weddingdate) {
        ContentValues contVal = new ContentValues();
        contVal.put("bridename", bridename);
        contVal.put("groomname", groomname);
        contVal.put("date", weddingdate);
        this.cr.update(CONTENT_URI_INFO, contVal, null, null);
    }

    public void loadList() {
        this.helper = new WPSQLiteOpenHelper(getBaseContext());
        this.db = this.helper.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("select schedules.title as title, schedules.note as note, schedules.ddate as ddate, schedules.status as status, schedules.topic_id as topic_id, topics.title as topic_title from schedules left outer join topics on schedules.topic_id = topics.topic_id " + filter + "order by status desc, ddate asc, title asc", null);
        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        ScheduleListAdapter listAdapter = new ScheduleListAdapter(this.act, R.layout.dashboard_schedule, hm);
        this.ListViewSchedules = (ListView) this.act.findViewById(R.id.listSchedules);
        this.ListViewSchedules.setAdapter(listAdapter);
        this.ListViewSchedules.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DashboardSchedules.this.gotoSchedules(Integer.parseInt((String) hm.get("topic_id[" + position + "]")), (String) hm.get("topic_title[" + position + "]"));
            }
        });
        this.db.close();
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
        if (errcode == 22) {
            msg = getResources().getString(R.string.err_dateformat);
        } else if (errcode == 91) {
            msg = getResources().getString(R.string.err_names);
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

    public void gotoSchedules(int topic_id, String topic_title) {
        Intent intent = new Intent(this.act, Schedules.class);
        intent.putExtra("topic_id", topic_id);
        intent.putExtra("topic_title", topic_title);
        this.act.startActivity(intent);
    }
}
