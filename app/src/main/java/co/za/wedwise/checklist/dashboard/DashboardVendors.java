package co.za.wedwise.checklist.dashboard;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import co.za.wedwise.checklist.checklist.Vendors;
import co.za.wedwise.checklist.database.WPSQLiteOpenHelper;
import java.util.HashMap;

public class DashboardVendors extends Activity {
    public static final Uri CONTENT_URI_VENDORS = Config.CONTENT_URI_VENDORS;
    private ListView ListViewVendors;
    Activity act = this;
    ContentResolver cr;
    SQLiteDatabase db;
    WPSQLiteOpenHelper helper;
    private int positionToMove = 0;
    private int scrollto = 0;

    class VendorListAdapter extends BaseAdapter {
        HashMap<String, String> hm;
        LayoutInflater inflater;
        public int listCount = 0;
        Context mContext;
        int mListLayout;

        public VendorListAdapter(Context tContext, int listLayout, HashMap<String, String> tmpHm) {
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
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView note = (TextView) convertView.findViewById(R.id.note);
            TextView contact = (TextView) convertView.findViewById(R.id.contact);
            TextView phone = (TextView) convertView.findViewById(R.id.phone);
            TextView email = (TextView) convertView.findViewById(R.id.email);
            TextView website = (TextView) convertView.findViewById(R.id.website);
            TextView address = (TextView) convertView.findViewById(R.id.address);
            String titlestr = (String) this.hm.get("title[" + position + "]");
            String notestr = (String) this.hm.get("note[" + position + "]");
            String contactstr = (String) this.hm.get("contact[" + position + "]");
            String phonestr = (String) this.hm.get("phone[" + position + "]");
            String emailstr = (String) this.hm.get("email[" + position + "]");
            String websitestr = (String) this.hm.get("website[" + position + "]");
            String addressstr = (String) this.hm.get("address[" + position + "]");
            ((TextView) convertView.findViewById(R.id.topictitle)).setText((String) this.hm.get("topic_title[" + position + "]"));
            title.setText(titlestr);
            if (Common.checkValue(notestr)) {
                note.setVisibility(0);
                note.setText(notestr);
            } else {
                note.setVisibility(8);
            }
            if (Common.checkValue(contactstr)) {
                contact.setVisibility(0);
                contact.setText("(" + contactstr + ")");
            } else {
                contact.setVisibility(8);
            }
            if (Common.checkValue(phonestr)) {
                phone.setVisibility(0);
                phone.setText(phonestr);
            } else {
                phone.setVisibility(8);
            }
            if (Common.checkValue(emailstr)) {
                email.setVisibility(0);
                email.setText(emailstr);
            } else {
                email.setVisibility(8);
            }
            if (Common.checkValue(websitestr)) {
                website.setVisibility(0);
                website.setText(websitestr);
            } else {
                website.setVisibility(8);
            }
            if (Common.checkValue(addressstr)) {
                address.setVisibility(0);
                address.setText(addressstr);
            } else {
                address.setVisibility(8);
            }
            return convertView;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_vendors);
        this.cr = getContentResolver();
        refreshList();
    }

    public void refreshList() {
        loadList();
    }

    public void refreshList(View v) {
        loadList();
    }

    public void loadList() {
        this.helper = new WPSQLiteOpenHelper(getBaseContext());
        this.db = this.helper.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("select vendors.title as title, vendors.note as note, vendors.contact as contact, vendors.phone as phone, vendors.email as email, vendors.website as website, vendors.address as address, vendors.topic_id as topic_id, topics.title as topic_title from vendors left outer join topics on vendors.topic_id = topics.topic_id order by title asc", null);
        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        VendorListAdapter listAdapter = new VendorListAdapter(this.act, R.layout.dashboard_vendor, hm);
        this.ListViewVendors = (ListView) this.act.findViewById(R.id.listVendors);
        this.ListViewVendors.setAdapter(listAdapter);
        this.ListViewVendors.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DashboardVendors.this.gotoVendors(Integer.parseInt((String) hm.get("topic_id[" + position + "]")), (String) hm.get("topic_title[" + position + "]"));
            }
        });
        this.db.close();
    }

    public void scrollList() {
        this.ListViewVendors.setSelectionFromTop(this.positionToMove, this.scrollto);
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

    public void gotoVendors(int topic_id, String topic_title) {
        Intent intent = new Intent(this.act, Vendors.class);
        intent.putExtra("topic_id", topic_id);
        intent.putExtra("topic_title", topic_title);
        this.act.startActivity(intent);
    }
}
