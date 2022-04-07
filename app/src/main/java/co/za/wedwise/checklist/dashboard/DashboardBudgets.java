package co.za.wedwise.checklist.dashboard;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import co.za.wedwise.R;
import co.za.wedwise.checklist.Common;
import co.za.wedwise.checklist.Config;
import co.za.wedwise.checklist.checklist.Budgets;
import java.util.HashMap;

public class DashboardBudgets extends Activity {
    public static final Uri CONTENT_URI_TOPICS = Config.CONTENT_URI_TOPICS;
    private ListView ListViewBudgets;
    Activity act = this;
    ContentResolver cr;
    private int positionToMove = 0;
    private int scrollto = 0;

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
            TextView topictitle = (TextView) convertView.findViewById(R.id.topictitle);
            TextView budgeted = (TextView) convertView.findViewById(R.id.budgeted);
            TextView vendor = (TextView) convertView.findViewById(R.id.vendor);
            TextView spent = (TextView) convertView.findViewById(R.id.spent);
            String topictitlestr = (String) this.hm.get("title[" + position + "]");
            String budgetedstr = Common.numberFormat((String) this.hm.get("budgeted[" + position + "]"));
            String vendorstr = Common.numberFormat((String) this.hm.get("vendor[" + position + "]"));
            String spentstr = Common.numberFormat((String) this.hm.get("spent[" + position + "]"));
            topictitle.setText(topictitlestr);
            budgeted.setText(budgetedstr);
            vendor.setText(vendorstr);
            spent.setText(spentstr);
            return convertView;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_budgets);
        this.cr = getContentResolver();
        refreshList();
    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();

    }

    public void refreshList() {
        displaySumBudget();
        loadList();
    }

    public void refreshList(View v) {
        displaySumBudget();
        loadList();
    }

    public void loadList() {
        Cursor cursor = this.cr.query(CONTENT_URI_TOPICS, null, null, null, "title asc");
        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        BudgetListAdapter listAdapter = new BudgetListAdapter(this.act, R.layout.dashboard_budget, hm);
        this.ListViewBudgets = (ListView) this.act.findViewById(R.id.listBudgets);
        this.ListViewBudgets.setAdapter(listAdapter);
        this.ListViewBudgets.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DashboardBudgets.this.gotoBudgets(Integer.parseInt((String) hm.get("topic_id[" + position + "]")), (String) hm.get("title[" + position + "]"));
            }
        });
    }

    public void scrollList() {
        this.ListViewBudgets.setSelectionFromTop(this.positionToMove, this.scrollto);
    }

    public void displaySumBudget() {
        long sum_budgeted = 0;
        long sum_vendor = 0;
        long sum_spent = 0;
        Cursor cursor = this.cr.query(CONTENT_URI_TOPICS, new String[]{"budgeted", "vendor", "spent"}, null, null, null);
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
        TextView sumBudgeted = (TextView) this.act.findViewById(R.id.sumBudgeted);
        TextView sumVendor = (TextView) this.act.findViewById(R.id.sumVendor);
        TextView sumSpent = (TextView) this.act.findViewById(R.id.sumSpent);
        String sum_budgeted_str = Common.numberFormat(Long.toString(sum_budgeted));
        String sum_vendor_str = Common.numberFormat(Long.toString(sum_vendor));
        String sum_spent_str = Common.numberFormat(Long.toString(sum_spent));
        sumBudgeted.setText(sum_budgeted_str);
        sumVendor.setText(sum_vendor_str);
        sumSpent.setText(sum_spent_str);
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

    public void gotoBudgets(int topic_id, String topic_title) {
        Intent intent = new Intent(this.act, Budgets.class);
        intent.putExtra("topic_id", topic_id);
        intent.putExtra("topic_title", topic_title);
        this.act.startActivity(intent);
    }
}
