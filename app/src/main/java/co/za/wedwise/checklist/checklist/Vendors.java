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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import co.za.wedwise.R;
import co.za.wedwise.checklist.Common;
import co.za.wedwise.checklist.Config;
import java.util.HashMap;

public class Vendors extends Activity {
    public static final Uri CONTENT_URI_VENDORS = Config.CONTENT_URI_VENDORS;
    private static String orderby;
    private static String orderbyStatus = "status desc, title asc";
    private static String orderbyTitle = "title asc, status desc";
    private ListView ListViewVendors;
    Activity act = this;
    ContentResolver cr;
    private int positionToMove = 0;
    private int scrollto = 0;
    private int topic_id = 0;
    private String topic_title;

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
            TextView note = (TextView) convertView.findViewById(R.id.note);
            TextView contact = (TextView) convertView.findViewById(R.id.contact);
            TextView phone = (TextView) convertView.findViewById(R.id.phone);
            TextView email = (TextView) convertView.findViewById(R.id.email);
            TextView website = (TextView) convertView.findViewById(R.id.website);
            TextView address = (TextView) convertView.findViewById(R.id.address);
            String notestr = (String) this.hm.get("note[" + position + "]");
            String contactstr = (String) this.hm.get("contact[" + position + "]");
            String phonestr = (String) this.hm.get("phone[" + position + "]");
            String emailstr = (String) this.hm.get("email[" + position + "]");
            String websitestr = (String) this.hm.get("website[" + position + "]");
            String addressstr = (String) this.hm.get("address[" + position + "]");
            ((TextView) convertView.findViewById(R.id.title)).setText((String) this.hm.get("title[" + position + "]"));
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
        setContentView(R.layout.vendors);
        Intent intent = getIntent();
        this.topic_id = intent.getIntExtra("topic_id", 0);
        this.topic_title = intent.getStringExtra("topic_title");
        this.cr = getContentResolver();
        setTitle();
        refreshList();
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
        loadList();
    }

    public void loadList() {
        Cursor cursor = this.cr.query(CONTENT_URI_VENDORS, null, "topic_id = " + this.topic_id, null, orderby);
        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        VendorListAdapter listAdapter = new VendorListAdapter(this.act, R.layout.vendor, hm);
        this.ListViewVendors = (ListView) this.act.findViewById(R.id.listVendors);
        this.ListViewVendors.setAdapter(listAdapter);
        this.ListViewVendors.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String title = (String) hm.get("title[" + position + "]");
                final String phone = Common.checkValue((String) hm.get(new StringBuilder("phone[").append(position).append("]").toString())) ? (String) hm.get("phone[" + position + "]") : null;
                final String email = Common.checkValue((String) hm.get(new StringBuilder("email[").append(position).append("]").toString())) ? (String) hm.get("email[" + position + "]") : null;
                final String website = Common.checkValue((String) hm.get(new StringBuilder("website[").append(position).append("]").toString())) ? (String) hm.get("website[" + position + "]") : null;
                final String address = Common.checkValue((String) hm.get(new StringBuilder("address[").append(position).append("]").toString())) ? (String) hm.get("address[" + position + "]") : null;
                Dialog dialog = new Dialog(Vendors.this.act);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.list_contact_actions);
                TextView itv = (TextView) dialog.findViewById(R.id.selctedItem);
                TextView telBtn = (TextView) dialog.findViewById(R.id.telBtn);
                TextView emailBtn = (TextView) dialog.findViewById(R.id.emailBtn);
                TextView websiteBtn = (TextView) dialog.findViewById(R.id.websiteBtn);
                TextView mapBtn = (TextView) dialog.findViewById(R.id.mapBtn);
                if (phone == null) {
                    telBtn.setVisibility(8);
                }
                if (email == null) {
                    emailBtn.setVisibility(8);
                }
                if (website == null) {
                    websiteBtn.setVisibility(8);
                }
                if (address == null) {
                    mapBtn.setVisibility(8);
                }
                itv.setText(title);
                telBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Vendors.this.act.startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phone)));
                    }
                });
                emailBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Vendors.this.act.startActivity(new Intent("android.intent.action.SENDTO", Uri.parse("mailto:" + email)));
                    }
                });
                websiteBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Vendors.this.act.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(website)));
                    }
                });
                mapBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Vendors.this.act.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:37.5,127?q=" + address)));
                    }
                });
                dialog.show();
            }
        });
        this.ListViewVendors.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int vendor_id = Integer.parseInt((String) hm.get("vendor_id[" + position + "]"));
                final String title = (String) hm.get("title[" + position + "]");
                final String note = (String) hm.get("note[" + position + "]");
                final String contact = (String) hm.get("contact[" + position + "]");
                final String phone = (String) hm.get("phone[" + position + "]");
                final String email = (String) hm.get("email[" + position + "]");
                final String website = (String) hm.get("website[" + position + "]");
                final String address = (String) hm.get("address[" + position + "]");
                Vendors.this.scrollto = view.getTop();
                Vendors.this.positionToMove = position;
                final Dialog dialog = new Dialog(Vendors.this.act);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.list_manage);
                TextView editBtn = (TextView) dialog.findViewById(R.id.editBtn);
                TextView deleteBtn = (TextView) dialog.findViewById(R.id.deleteBtn);
                ((TextView) dialog.findViewById(R.id.selctedItem)).setText(title);
                editBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Vendors.this.openUpdateVendorDialog(vendor_id, title, note, contact, phone, email, website, address);
                        dialog.dismiss();
                    }
                });
                deleteBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Vendors.this.deleteVendor(vendor_id);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    public void scrollList() {
        this.ListViewVendors.setSelectionFromTop(this.positionToMove, this.scrollto);
    }

    public void openNewVendorDialog(View view) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.vendor_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        final TextView ntv = (TextView) dialog.findViewById(R.id.noteEditText);
        final TextView ctv = (TextView) dialog.findViewById(R.id.contactEditText);
        final TextView ptv = (TextView) dialog.findViewById(R.id.phoneEditText);
        final TextView etv = (TextView) dialog.findViewById(R.id.emailEditText);
        final TextView wtv = (TextView) dialog.findViewById(R.id.websiteEditText);
        final TextView atv = (TextView) dialog.findViewById(R.id.addressEditText);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                String note = ntv.getText().toString();
                String contact = ctv.getText().toString();
                String phone = ptv.getText().toString();
                String email = etv.getText().toString();
                String website = wtv.getText().toString();
                String address = atv.getText().toString();
                boolean urlcheck = Common.checkValue(website) ? Common.checkValidUrl(website) : true;
                if (!Common.checkValue(title)) {
                    Vendors.this.showErrMSG(11);
                } else if (urlcheck) {
                    Vendors.this.insertVendor(title, note, contact, phone, email, website, address);
                    Vendors.this.showMSG(10);
                    Vendors.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Vendors.this.refreshList();
                } else {
                    Vendors.this.showErrMSG(13);
                }
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Vendors.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openUpdateVendorDialog(int t_id, String title, String note, String contact, String phone, String email, String website, String address) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.vendor_form);
        final TextView tv = (TextView) dialog.findViewById(R.id.titleEditText);
        final TextView ntv = (TextView) dialog.findViewById(R.id.noteEditText);
        final TextView ctv = (TextView) dialog.findViewById(R.id.contactEditText);
        final TextView ptv = (TextView) dialog.findViewById(R.id.phoneEditText);
        final TextView etv = (TextView) dialog.findViewById(R.id.emailEditText);
        final TextView wtv = (TextView) dialog.findViewById(R.id.websiteEditText);
        final TextView atv = (TextView) dialog.findViewById(R.id.addressEditText);
        tv.setText(title);
        ntv.setText(note);
        ctv.setText(contact);
        ptv.setText(phone);
        etv.setText(email);
        wtv.setText(website);
        atv.setText(address);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        final int vendor_id = t_id;
        ((TextView) dialog.findViewById(R.id.formOKBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String title = tv.getText().toString();
                String note = ntv.getText().toString();
                String contact = ctv.getText().toString();
                String phone = ptv.getText().toString();
                String email = etv.getText().toString();
                String website = wtv.getText().toString();
                String address = atv.getText().toString();
                boolean urlcheck = Common.checkValue(website) ? Common.checkValidUrl(website) : true;
                if (!Common.checkValue(title)) {
                    Vendors.this.showErrMSG(11);
                } else if (urlcheck) {
                    Vendors.this.updateVendor(vendor_id, title, note, contact, phone, email, website, address);
                    Vendors.this.showMSG(20);
                    Vendors.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Vendors.this.refreshList();
                    Vendors.this.scrollList();
                } else {
                    Vendors.this.showErrMSG(13);
                }
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Vendors.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void insertVendor(String title, String note, String contact, String phone, String email, String website, String address) {
        ContentValues contVal = new ContentValues();
        contVal.put("topic_id", Integer.valueOf(this.topic_id));
        contVal.put(Config.TITLE, title);
        contVal.put("note", note);
        contVal.put("contact", contact);
        contVal.put("phone", phone);
        contVal.put("email", email);
        contVal.put("website", website);
        contVal.put("address", address);
        contVal.put("status", "T");
        this.cr.insert(CONTENT_URI_VENDORS, contVal);
    }

    public void updateVendor(int vendor_id, String title, String note, String contact, String phone, String email, String website, String address) {
        ContentValues contVal = new ContentValues();
        contVal.put(Config.TITLE, title);
        contVal.put("note", note);
        contVal.put("contact", contact);
        contVal.put("phone", phone);
        contVal.put("email", email);
        contVal.put("website", website);
        contVal.put("address", address);
        this.cr.update(CONTENT_URI_VENDORS, contVal, "vendor_id=" + vendor_id, null);
    }

    public void deleteVendor(int vendor_id) {
        this.cr.delete(CONTENT_URI_VENDORS, "vendor_id=" + vendor_id, null);
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
            msg = getResources().getString(R.string.err_vendorname);
        } else if (errcode == 13) {
            msg = getResources().getString(R.string.err_urlformat);
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
