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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import co.za.wedwise.R;
import co.za.wedwise.checklist.Common;
import co.za.wedwise.checklist.Config;

import java.util.HashMap;

public class Topics extends Activity {
    public static final Uri CONTENT_URI_BUDGETS = Config.CONTENT_URI_BUDGETS;
    public static final Uri CONTENT_URI_ITEMS = Config.CONTENT_URI_ITEMS;
    public static final Uri CONTENT_URI_SCHEDULES = Config.CONTENT_URI_SCHEDULES;
    public static final Uri CONTENT_URI_TASKS = Config.CONTENT_URI_TASKS;
    public static final Uri CONTENT_URI_TOPICS = Config.CONTENT_URI_TOPICS;
    public static final Uri CONTENT_URI_VENDORS = Config.CONTENT_URI_VENDORS;
    public static int current_topic_id = 0;
    public static String current_topic_title;
    private static String orderbyTitle = "title asc, status desc";
    private static String orderby = orderbyTitle;
    private ListView ListViewTopics;
    Activity act = this;
    ContentResolver cr;
    private int positionToMove = 0;
    private int scrollto = 0;
    ImageView imgBack;

    class TopicListAdapter extends BaseAdapter {
        HashMap<String, String> hm;
        LayoutInflater inflater;
        public int listCount = 0;
        Context mContext;
        int mListLayout;

        public TopicListAdapter(Context tContext, int listLayout, HashMap<String, String> tmpHm) {
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
            ((TextView) convertView.findViewById(R.id.title)).setText((String) this.hm.get("title[" + position + "]"));
            return convertView;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics);
        this.cr = getContentResolver();
        this.imgBack = findViewById(R.id.back_btn);
        imgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadList();
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

    public void refreshList() {
        loadList();
    }

    public void loadList() {
        Cursor cursor = this.cr.query(CONTENT_URI_TOPICS, null, null, null, orderby);
        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        TopicListAdapter listAdapter = new TopicListAdapter(this.act, R.layout.topic, hm);
        this.ListViewTopics = (ListView) this.act.findViewById(R.id.listTopics);
        this.ListViewTopics.setAdapter(listAdapter);
        this.ListViewTopics.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Topics.current_topic_id = Integer.parseInt((String) hm.get("topic_id[" + position + "]"));
                Topics.current_topic_title = (String) hm.get("title[" + position + "]");
                Topics.this.gotoItems();
            }
        });
        this.ListViewTopics.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int topic_id = Integer.parseInt((String) hm.get("topic_id[" + position + "]"));
                final String title = (String) hm.get("title[" + position + "]");
                final String note = (String) hm.get("note[" + position + "]");
                final String startdate = (String) hm.get("sdate[" + position + "]");
                final String enddate = (String) hm.get("edate[" + position + "]");
                Topics.this.scrollto = view.getTop();
                Topics.this.positionToMove = position;
                final Dialog dialog = new Dialog(Topics.this.act);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.list_manage);
                TextView editBtn = (TextView) dialog.findViewById(R.id.editBtn);
                TextView deleteBtn = (TextView) dialog.findViewById(R.id.deleteBtn);
                ((TextView) dialog.findViewById(R.id.selctedItem)).setText(title);
                editBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Topics.this.openUpdateTopicDialog(topic_id, title, note, startdate, enddate);
                        dialog.dismiss();
                    }
                });
                deleteBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Topics.this.openDeleteConfirmDialog(topic_id, title);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    public void scrollList() {
        this.ListViewTopics.setSelectionFromTop(this.positionToMove, this.scrollto);
    }

    public void toggleStatus(int topic_id) {
        String selection = "topic_id=" + topic_id;
        Cursor cursor = this.cr.query(CONTENT_URI_TOPICS, null, selection, null, null);
        HashMap<String, String> tmphm = Common.cursor2HashMap(cursor);
        cursor.close();
        String newStatus = ((String) tmphm.get("status[0]")).equals("T") ? "D" : "T";
        ContentValues contVal = new ContentValues();
        contVal.put("status", newStatus);
        this.cr.update(CONTENT_URI_TOPICS, contVal, selection, null);
    }

    public void openNewTopicDialog(View view) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.topic_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                if (Common.checkValue(title)) {
                    Topics.this.insertTopic(title);
                    Topics.this.showMSG(10);
                    Topics.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Topics.this.refreshList();
                    return;
                }
                Topics.this.showErrMSG(11);
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Topics.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openUpdateTopicDialog(int t_id, String title, String note, String startdate, String enddate) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.topic_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        tv.setText(title);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        final int topic_id = t_id;
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                if (Common.checkValue(title)) {
                    Topics.this.updateTopic(topic_id, title);
                    Topics.this.showMSG(20);
                    Topics.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Topics.this.refreshList();
                    Topics.this.scrollList();
                    return;
                }
                Topics.this.showErrMSG(11);
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Topics.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openDeleteConfirmDialog(int t_id, String title) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.list_manage_confirm);
        TextView ctv = (TextView) dialog.findViewById(R.id.confirmMsg);
        ((TextView) dialog.findViewById(R.id.selctedItem)).setText(title);
        ctv.setText(getResources().getString(R.string.confirm_delete_topic));
        TextView formCancelBtn = (TextView) dialog.findViewById(R.id.formCancelBtn);
        final int topic_id = t_id;
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Topics.this.deleteTopic(topic_id);
                Topics.this.hideSoftKeyboard(v);
                dialog.dismiss();
                Topics.this.refreshList();
                Topics.this.scrollList();
            }
        });
        formCancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Topics.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void insertTopic(String title) {
        ContentValues contVal = new ContentValues();
        contVal.put(Config.TITLE, title);
        contVal.put("budgeted", "0");
        contVal.put("vendor", "0");
        contVal.put("spent", "0");
        contVal.put("status", "T");
        this.cr.insert(CONTENT_URI_TOPICS, contVal);
    }

    public void updateTopic(int topic_id, String title) {
        ContentValues contVal = new ContentValues();
        contVal.put(Config.TITLE, title);
        this.cr.update(CONTENT_URI_TOPICS, contVal, "topic_id=" + topic_id, null);
    }

    public void deleteTopic(int topic_id) {
        String selection = "topic_id=" + topic_id;
        this.cr.delete(CONTENT_URI_TASKS, selection, null);
        this.cr.delete(CONTENT_URI_ITEMS, selection, null);
        this.cr.delete(CONTENT_URI_VENDORS, selection, null);
        this.cr.delete(CONTENT_URI_BUDGETS, selection, null);
        this.cr.delete(CONTENT_URI_SCHEDULES, selection, null);
        this.cr.delete(CONTENT_URI_TOPICS, selection, null);
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

    public void gotoTasks() {
        Intent intent = new Intent(this.act, Tasks.class);
        intent.putExtra("topic_id", current_topic_id);
        intent.putExtra("topic_title", current_topic_title);
        this.act.startActivity(intent);
    }

    public void gotoItems() {
        Intent intent = new Intent(this.act, Items.class);
        intent.putExtra("topic_id", current_topic_id);
        intent.putExtra("topic_title", current_topic_title);
        this.act.startActivity(intent);
    }

    public void gotoBudgets() {
        Intent intent = new Intent(this.act, Budgets.class);
        intent.putExtra("topic_id", current_topic_id);
        intent.putExtra("topic_title", current_topic_title);
        this.act.startActivity(intent);
    }

    public void gotoSchedules() {
        Intent intent = new Intent(this.act, Schedules.class);
        intent.putExtra("topic_id", current_topic_id);
        intent.putExtra("topic_title", current_topic_title);
        this.act.startActivity(intent);
    }

    public void gotoVendors() {
        Intent intent = new Intent(this.act, Vendors.class);
        intent.putExtra("topic_id", current_topic_id);
        intent.putExtra("topic_title", current_topic_title);
        this.act.startActivity(intent);
    }
}
