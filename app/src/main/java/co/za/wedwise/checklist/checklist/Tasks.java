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

public class Tasks extends Activity {
    public static final Uri CONTENT_URI_TASKS = Config.CONTENT_URI_TASKS;
    private static String orderbyStatus = "status desc, title asc";
    private static String orderbyTitle = "title asc, status desc";
    private static String orderby = orderbyTitle;
    private ListView ListViewTasks;
    Activity act = this;
    ContentResolver cr;
    private int positionToMove = 0;
    private int scrollto = 0;
    private int topic_id = 0;
    private String topic_title;

    class TaskListAdapter extends BaseAdapter {
        HashMap<String, String> hm;
        LayoutInflater inflater;
        public int listCount = 0;
        Context mContext;
        int mListLayout;

        public TaskListAdapter(Context tContext, int listLayout, HashMap<String, String> tmpHm) {
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
            int i = -6710887;
            if (convertView == null) {
                convertView = this.inflater.inflate(this.mListLayout, parent, false);
            }
            CheckBox checkBox = (CheckBox) convertView.findViewById( R.id.checkBox);
            checkBox.setButtonDrawable(R.drawable.custom_checkbox);
            String status = (String) this.hm.get("status[" + position + "]");
            checkBox.setChecked(status.equals("D"));
            checkBox.setClickable(false);
            checkBox.setFocusable(false);
            checkBox.setEnabled(status.equals("T"));
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView note = (TextView) convertView.findViewById(R.id.note);
            String notestr = (String) this.hm.get("note[" + position + "]");
            title.setText((String) this.hm.get("title[" + position + "]"));
            title.setTextColor(status.equals("T") ? -14540254 : -6710887);
            if (Common.checkValue(notestr)) {
                note.setVisibility(0);
                note.setText(notestr);
                if (status.equals("T")) {
                    i = -12303292;
                }
                note.setTextColor(i);
            } else {
                note.setVisibility(8);
            }
            return convertView;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks);
        Intent intent = getIntent();
        this.topic_id = intent.getIntExtra("topic_id", 0);
        this.topic_title = intent.getStringExtra("topic_title");
        this.cr = getContentResolver();
        setTitle();
        loadList();
    }


    public void orderbyTitle(View v) {
        orderby = orderbyTitle;
        refreshList();
    }

    public void orderbyStatus(View v) {
        orderby = orderbyStatus;
        refreshList();
    }

    public void refreshList() {
        loadList();
    }

    public void setTitle() {
        ((TextView) this.act.findViewById(R.id.listTitle)).setText(this.topic_title);
    }

    public void loadList() {
        Cursor cursor = this.cr.query(CONTENT_URI_TASKS, null, "topic_id = " + this.topic_id, null, orderby);
        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        TaskListAdapter listAdapter = new TaskListAdapter(this.act, R.layout.task, hm);
        this.ListViewTasks = (ListView) this.act.findViewById(R.id.listTasks);
        this.ListViewTasks.setAdapter(listAdapter);
        this.ListViewTasks.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int task_id = Integer.parseInt((String) hm.get("task_id[" + position + "]"));
                Tasks.this.scrollto = view.getTop();
                Tasks.this.positionToMove = position;
                Tasks.this.toggleStatus(task_id);
                Tasks.this.loadList();
                Tasks.this.scrollList();
            }
        });
        this.ListViewTasks.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int task_id = Integer.parseInt((String) hm.get("task_id[" + position + "]"));
                final String title = (String) hm.get("title[" + position + "]");
                final String note = (String) hm.get("note[" + position + "]");
                Tasks.this.scrollto = view.getTop();
                Tasks.this.positionToMove = position;
                final Dialog dialog = new Dialog(Tasks.this.act);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.list_manage);
                TextView editBtn = (TextView) dialog.findViewById(R.id.editBtn);
                TextView deleteBtn = (TextView) dialog.findViewById(R.id.deleteBtn);
                ((TextView) dialog.findViewById(R.id.selctedItem)).setText(title);
                editBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Tasks.this.openUpdateTaskDialog(task_id, title, note);
                        dialog.dismiss();
                    }
                });
                deleteBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Tasks.this.deleteTask(task_id);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    public void scrollList() {
        this.ListViewTasks.setSelectionFromTop(this.positionToMove, this.scrollto);
    }

    public void toggleStatus(int task_id) {
        String selection = "task_id=" + task_id;
        Cursor cursor = this.cr.query(CONTENT_URI_TASKS, null, selection, null, null);
        HashMap<String, String> tmphm = Common.cursor2HashMap(cursor);
        cursor.close();
        String newStatus = ((String) tmphm.get("status[0]")).equals("T") ? "D" : "T";
        ContentValues contVal = new ContentValues();
        contVal.put("status", newStatus);
        this.cr.update(CONTENT_URI_TASKS, contVal, selection, null);
    }

    public void openNewTaskDialog(View view) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.task_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        final TextView ntv = (TextView) dialog.findViewById(R.id.noteEditText);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                String note = ntv.getText().toString();
                if (Common.checkValue(title)) {
                    Tasks.this.insertTask(title, note);
                    Tasks.this.showMSG(10);
                    Tasks.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Tasks.this.loadList();
                    return;
                }
                Tasks.this.showErrMSG(11);
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Tasks.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openUpdateTaskDialog(int t_id, String title, String note) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.task_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        final TextView ntv = (TextView) dialog.findViewById(R.id.noteEditText);
        tv.setText(title);
        ntv.setText(note);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        final int task_id = t_id;
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                String note = ntv.getText().toString();
                if (Common.checkValue(title)) {
                    Tasks.this.updateTask(task_id, title, note);
                    Tasks.this.showMSG(20);
                    Tasks.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Tasks.this.loadList();
                    Tasks.this.scrollList();
                    return;
                }
                Tasks.this.showErrMSG(11);
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Tasks.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void insertTask(String title, String note) {
        ContentValues contVal = new ContentValues();
        contVal.put("topic_id", Integer.valueOf(this.topic_id));
        contVal.put(Config.TITLE, title);
        contVal.put("note", note);
        contVal.put("status", "T");
        this.cr.insert(CONTENT_URI_TASKS, contVal);
    }

    public void updateTask(int task_id, String title, String note) {
        ContentValues contVal = new ContentValues();
        contVal.put(Config.TITLE, title);
        contVal.put("note", note);
        this.cr.update(CONTENT_URI_TASKS, contVal, "task_id=" + task_id, null);
    }

    public void deleteTask(int task_id) {
        this.cr.delete(CONTENT_URI_TASKS, "task_id=" + task_id, null);
        loadList();
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
