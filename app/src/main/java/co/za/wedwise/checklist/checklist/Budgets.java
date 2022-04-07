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

public class Budgets extends Activity {
    public static final Uri CONTENT_URI_BUDGETS = Config.CONTENT_URI_BUDGETS;
    public static final Uri CONTENT_URI_TOPICS = Config.CONTENT_URI_TOPICS;
    private static String orderby;
    private static String orderbyStatus = "status desc, title asc";
    private static String orderbyTitle = "title asc, status desc";
    private ListView ListViewBudgets;
    Activity act = this;
    ContentResolver cr;
    private int positionToMove = 0;
    private int scrollto = 0;
    private int topic_id = 0;
    private String topic_title;

    class BudgetListAdapter extends BaseAdapter {
        HashMap<String, String> hm;
        LayoutInflater inflater;
        public int listCount = 0;
        Context mContext;
        int mListLayout;

        public BudgetListAdapter(Context tContext, int listLayout, HashMap<String, String> tmpHm) {
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
            TextView budgeted = (TextView) convertView.findViewById(R.id.budgeted);
            TextView vendor = (TextView) convertView.findViewById(R.id.vendor);
            TextView spent = (TextView) convertView.findViewById(R.id.spent);
            String titlestr = (String) this.hm.get("title[" + position + "]");
            String notestr = (String) this.hm.get("note[" + position + "]");
            String budgetedstr = Common.numberFormat((String) this.hm.get("budgeted[" + position + "]"));
            String vendorstr = Common.numberFormat((String) this.hm.get("vendor[" + position + "]"));
            String spentstr = Common.numberFormat((String) this.hm.get("spent[" + position + "]"));
            title.setText(titlestr);
            title.setTextColor(status.equals("T") ? -14540254 : -6710887);
            budgeted.setText(budgetedstr);
            vendor.setText(vendorstr);
            spent.setText(spentstr);
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
        setContentView(R.layout.budgets);
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

    public void setTitle() {
        ((TextView) this.act.findViewById(R.id.listTitle)).setText(this.topic_title);
    }

    public void refreshList() {
        displaySumBudget();
        loadList();
    }

    public void loadList() {
        Cursor cursor = this.cr.query(CONTENT_URI_BUDGETS, null, "topic_id = " + this.topic_id, null, orderby);
        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        BudgetListAdapter listAdapter = new BudgetListAdapter(this.act, R.layout.budget, hm);
        this.ListViewBudgets = (ListView) this.act.findViewById(R.id.listBudgets);
        this.ListViewBudgets.setAdapter(listAdapter);
        this.ListViewBudgets.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int budget_id = Integer.parseInt((String) hm.get("budget_id[" + position + "]"));
                Budgets.this.scrollto = view.getTop();
                Budgets.this.positionToMove = position;
                Budgets.this.toggleStatus(budget_id);
                Budgets.this.loadList();
                Budgets.this.scrollList();
            }
        });
        this.ListViewBudgets.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int budget_id = Integer.parseInt((String) hm.get("budget_id[" + position + "]"));
                final String title = (String) hm.get("title[" + position + "]");
                final String note = (String) hm.get("note[" + position + "]");
                final String budgeted = (String) hm.get("budgeted[" + position + "]");
                final String vendor = (String) hm.get("vendor[" + position + "]");
                final String spent = (String) hm.get("spent[" + position + "]");
                Budgets.this.scrollto = view.getTop();
                Budgets.this.positionToMove = position;
                final Dialog dialog = new Dialog(Budgets.this.act);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.list_manage);
                TextView editBtn = (TextView) dialog.findViewById(R.id.editBtn);
                TextView deleteBtn = (TextView) dialog.findViewById(R.id.deleteBtn);
                ((TextView) dialog.findViewById(R.id.selctedItem)).setText(title);
                editBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Budgets.this.openUpdateBudgetDialog(budget_id, title, note, budgeted, vendor, spent);
                        dialog.dismiss();
                    }
                });
                deleteBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Budgets.this.deleteBudget(budget_id);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    public void scrollList() {
        this.ListViewBudgets.setSelectionFromTop(this.positionToMove, this.scrollto);
    }

    public void toggleStatus(int budget_id) {
        String selection = "budget_id=" + budget_id;
        Cursor cursor = this.cr.query(CONTENT_URI_BUDGETS, null, selection, null, null);
        HashMap<String, String> tmphm = Common.cursor2HashMap(cursor);
        cursor.close();
        String newStatus = ((String) tmphm.get("status[0]")).equals("T") ? "D" : "T";
        ContentValues contVal = new ContentValues();
        contVal.put("status", newStatus);
        this.cr.update(CONTENT_URI_BUDGETS, contVal, selection, null);
    }

    public void displaySumBudget() {
        Cursor cursor = this.cr.query(CONTENT_URI_TOPICS, new String[]{"budgeted", "vendor", "spent"}, "topic_id = " + this.topic_id, null, null);
        cursor.moveToFirst();
        String sum_budgeted = cursor.getString(0);
        String sum_vendor = cursor.getString(1);
        String sum_spent = cursor.getString(2);
        cursor.close();
        TextView sumBudgeted = (TextView) this.act.findViewById(R.id.sumBudgeted);
        TextView sumVendor = (TextView) this.act.findViewById(R.id.sumVendor);
        TextView sumSpent = (TextView) this.act.findViewById(R.id.sumSpent);
        String sum_budgeted_str = Common.numberFormat(sum_budgeted);
        String sum_vendor_str = Common.numberFormat(sum_vendor);
        String sum_spent_str = Common.numberFormat(sum_spent);
        sumBudgeted.setText(sum_budgeted_str);
        sumVendor.setText(sum_vendor_str);
        sumSpent.setText(sum_spent_str);
    }

    public void updateSumBudget() {
        long sum_budgeted = 0;
        long sum_vendor = 0;
        long sum_spent = 0;
        Cursor cursor = this.cr.query(CONTENT_URI_BUDGETS, new String[]{"budgeted", "vendor", "spent"}, "topic_id = " + this.topic_id, null, null);
        HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        int cursorcnt = cursor.getCount();
        if (cursorcnt == 0) {
            sum_budgeted = 0;
            sum_vendor = 0;
            sum_spent = 0;
        } else {
            for (int i = 0; i < cursorcnt; i++) {
                long budgeted = Long.parseLong((String) hm.get("budgeted[" + i + "]"));
                sum_budgeted += budgeted;
                sum_vendor += Long.parseLong((String) hm.get("vendor[" + i + "]"));
                sum_spent += Long.parseLong((String) hm.get("spent[" + i + "]"));
            }
        }
        cursor.close();
        String sum_budgeted_str = Long.toString(sum_budgeted);
        String sum_vendor_str = Long.toString(sum_vendor);
        String sum_spent_str = Long.toString(sum_spent);
        ContentValues contVal = new ContentValues();
        contVal.put("budgeted", sum_budgeted_str);
        contVal.put("vendor", sum_vendor_str);
        contVal.put("spent", sum_spent_str);
        this.cr.update(CONTENT_URI_TOPICS, contVal, "topic_id=" + this.topic_id, null);
    }

    public void openNewBudgetDialog(View view) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.budget_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        final TextView ntv = (TextView) dialog.findViewById(R.id.noteEditText);
        final TextView bbtv = (TextView) dialog.findViewById(R.id.budgetedEditText);
        final TextView bvtv = (TextView) dialog.findViewById(R.id.vendorEditText);
        final TextView bstv = (TextView) dialog.findViewById(R.id.spentEditText);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                String note = ntv.getText().toString();
                String budgeted = Common.checkValue(bbtv.getText().toString()) ? bbtv.getText().toString() : "0";
                String vendor = Common.checkValue(bvtv.getText().toString()) ? bvtv.getText().toString() : "0";
                String spent = Common.checkValue(bstv.getText().toString()) ? bstv.getText().toString() : "0";
                if (Common.checkValue(title)) {
                    Budgets.this.insertBudget(title, note, budgeted, vendor, spent);
                    Budgets.this.showMSG(10);
                    Budgets.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Budgets.this.refreshList();
                    return;
                }
                Budgets.this.showErrMSG(11);
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Budgets.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openUpdateBudgetDialog(int t_id, String title, String note, String budgeted, String vendor, String spent) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.budget_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        final TextView ntv = (TextView) dialog.findViewById(R.id.noteEditText);
        final TextView bbtv = (TextView) dialog.findViewById(R.id.budgetedEditText);
        final TextView bvtv = (TextView) dialog.findViewById(R.id.vendorEditText);
        final TextView bstv = (TextView) dialog.findViewById(R.id.spentEditText);
        tv.setText(title);
        ntv.setText(note);
        bbtv.setText(Common.checkZero(budgeted));
        bvtv.setText(Common.checkZero(vendor));
        bstv.setText(Common.checkZero(spent));
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        final int budget_id = t_id;
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                String note = ntv.getText().toString();
                String budgeted = Common.checkValue(bbtv.getText().toString()) ? bbtv.getText().toString() : "0";
                String vendor = Common.checkValue(bvtv.getText().toString()) ? bvtv.getText().toString() : "0";
                String spent = Common.checkValue(bstv.getText().toString()) ? bstv.getText().toString() : "0";
                if (Common.checkValue(title)) {
                    Budgets.this.updateBudget(budget_id, title, note, budgeted, vendor, spent);
                    Budgets.this.showMSG(20);
                    Budgets.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Budgets.this.refreshList();
                    Budgets.this.scrollList();
                    return;
                }
                Budgets.this.showErrMSG(11);
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Budgets.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void insertBudget(String title, String note, String budgeted, String vendor, String spent) {
        ContentValues contVal = new ContentValues();
        contVal.put("topic_id", Integer.valueOf(this.topic_id));
        contVal.put(Config.TITLE, title);
        contVal.put("note", note);
        contVal.put("budgeted", budgeted);
        contVal.put("vendor", vendor);
        contVal.put("spent", spent);
        contVal.put("status", "T");
        this.cr.insert(CONTENT_URI_BUDGETS, contVal);
        updateSumBudget();
    }

    public void updateBudget(int budget_id, String title, String note, String budgeted, String vendor, String spent) {
        ContentValues contVal = new ContentValues();
        contVal.put(Config.TITLE, title);
        contVal.put("note", note);
        contVal.put("budgeted", budgeted);
        contVal.put("vendor", vendor);
        contVal.put("spent", spent);
        this.cr.update(CONTENT_URI_BUDGETS, contVal, "budget_id=" + budget_id, null);
        updateSumBudget();
    }

    public void deleteBudget(int budget_id) {
        this.cr.delete(CONTENT_URI_BUDGETS, "budget_id=" + budget_id, null);
        updateSumBudget();
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
