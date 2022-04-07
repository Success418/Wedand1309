package co.za.wedwise.checklist.guestlist;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import android.provider.Contacts;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import co.za.wedwise.R;
import co.za.wedwise.checklist.Config;

import co.za.wedwise.checklist.Common;
import co.za.wedwise.checklist.database.WPSQLiteOpenHelper;

import java.util.HashMap;

public class Guests extends Activity {

    public static final Uri CONTENT_URI_GUESTS = Config.CONTENT_URI_GUESTS;
    private ListView ListViewGuests;
    Activity act = this;
    ContentResolver cr;
    SQLiteDatabase db;
    WPSQLiteOpenHelper helper;
    private TextView labelAttending;
    private TextView labelGuestsName;
    private TextView labelInvitessent;
    private int positionToMove = 0;
    private int scrollto = 0;
    private TextView statGuests;
    private ImageView imgBackBtn;
    private Boolean showAll = true, rsvp = false, attending = false;

    class GuestListAdapter extends BaseAdapter {
        HashMap<String, String> hm;
        LayoutInflater inflater;
        public int listCount = 0;
        Context mContext;
        int mListLayout;

        public GuestListAdapter(Context tContext, int listLayout, HashMap<String, String> tmpHm) {
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
            TextView side = (TextView) convertView.findViewById(R.id.side);
            TextView invites = (TextView) convertView.findViewById(R.id.invites);
            ImageView ivinvites = (ImageView) convertView.findViewById(R.id.ivinvites);
            TextView attending = (TextView) convertView.findViewById(R.id.attending);
            ImageView ivattending = (ImageView) convertView.findViewById(R.id.ivattending);


            String lastnamestr = (String) this.hm.get("lastname[" + position + "]");
            String sidestr = (String) this.hm.get("side[" + position + "]");
            String invitesstr = (String) this.hm.get("invitessent[" + position + "]");
            String attendingstr = (String) this.hm.get("attending[" + position + "]");


            ((TextView) convertView.findViewById(R.id.name)).setText(new StringBuilder(String.valueOf((String) this.hm.get("firstname[" + position + "]"))).append(" ").append(lastnamestr).toString());
            side.setText(sidestr);
            if (invitesstr.equals("Yes")) {
                ivinvites.setImageResource(R.drawable.ic_done_tick);
                ivinvites.setColorFilter(mContext.getResources().getColor(R.color.green));
            } else {
                invites.setText("-");
                ivinvites.setImageResource(R.drawable.ic_cross_remove_sign);
                ivinvites.setColorFilter(mContext.getResources().getColor(R.color.red));
            }
            if (attendingstr.equals("Yes")) {

                ivattending.setImageResource(R.drawable.ic_done_tick);
                ivattending.setColorFilter(mContext.getResources().getColor(R.color.green));
//                attending.setText("O");
//                attending.setTextColor(-6710785);
            } else if (attendingstr.equals("No")) {
                ivattending.setImageResource(R.drawable.ic_cross_remove_sign);
                ivattending.setColorFilter(mContext.getResources().getColor(R.color.red));
//                attending.setText("X");
//                attending.setTextColor(-39322);
            } else {
                ivattending.setImageResource(R.drawable.ic_cross_remove_sign);
                ivattending.setColorFilter(mContext.getResources().getColor(R.color.red));
//                attending.setText("-");
//                attending.setTextColor(-8947849);
            }
            return convertView;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guests);
        this.cr = getContentResolver();
        this.labelGuestsName = (TextView) findViewById(R.id.labelGuestsName);
        this.labelInvitessent = (TextView) findViewById(R.id.labelInvitessent);
        this.labelAttending = (TextView) findViewById(R.id.labelAttending);
        this.statGuests = (TextView) findViewById(R.id.statGuests);
        this.imgBackBtn = findViewById(R.id.back_btn);
        ImageView ivFilter = (ImageView) findViewById(R.id.ivFilter);
        ivFilter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        imgBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshList();
    }

    private void showDialog() {
//        AlertDialog.Builder b = new AlertDialog.Builder(this);
//        b.setTitle("");
//        String[] types = {"RSVP (yes,no)", "Attending (yes,no,undecided)","Show all"};
//        b.setItems(types, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//                switch (which) {
//                    case 0:
//
//                        break;
//                    case 1:
////                            Toast.makeText(baseApp, "B", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//        });
//
//        b.show();
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.guest_filter);
        ImageView imgBackDialog = dialog.findViewById(R.id.ivFilter);
        final Button btnRsvpYes = dialog.findViewById(R.id.btn_rsvp_yes);
        final Button btnRsvpNo = dialog.findViewById(R.id.btn_rsvp_no);
        final Button btnAttendingYes = dialog.findViewById(R.id.btn_attending_yes);
        final Button btnAttendingNo = dialog.findViewById(R.id.btn_attending_no);
        final Button btnApplyFilter = dialog.findViewById(R.id.btnApplyFilter);
        final Button btnShowAll = dialog.findViewById(R.id.formOKBtn);

        imgBackDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (showAll) {
            btnAttendingYes.setBackgroundColor(getResources().getColor(R.color.grey));
            btnAttendingNo.setBackgroundColor(getResources().getColor(R.color.grey));
            btnRsvpNo.setBackgroundColor(getResources().getColor(R.color.grey));
            btnRsvpYes.setBackgroundColor(getResources().getColor(R.color.grey));
            btnShowAll.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
        } else {
            if (rsvp) {
                btnRsvpNo.setBackgroundColor(getResources().getColor(R.color.grey));
                btnRsvpYes.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                btnShowAll.setBackgroundColor(getResources().getColor(R.color.grey));
            } else {
                btnRsvpYes.setBackgroundColor(getResources().getColor(R.color.grey));
                btnRsvpNo.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                btnShowAll.setBackgroundColor(getResources().getColor(R.color.grey));
            }

            if (attending) {
                btnAttendingNo.setBackgroundColor(getResources().getColor(R.color.grey));
                btnAttendingYes.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                btnShowAll.setBackgroundColor(getResources().getColor(R.color.grey));
            } else {
                btnAttendingYes.setBackgroundColor(getResources().getColor(R.color.grey));
                btnAttendingNo.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                btnShowAll.setBackgroundColor(getResources().getColor(R.color.grey));
            }

        }


        btnRsvpYes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRsvpNo.setBackgroundColor(getResources().getColor(R.color.grey));
                btnRsvpYes.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                btnShowAll.setBackgroundColor(getResources().getColor(R.color.grey));
                showAll = false;
                rsvp = true;
            }
        });

        btnRsvpNo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRsvpYes.setBackgroundColor(getResources().getColor(R.color.grey));
                btnRsvpNo.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                btnShowAll.setBackgroundColor(getResources().getColor(R.color.grey));
                showAll = false;
                rsvp = false;
            }
        });
        btnAttendingYes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAttendingNo.setBackgroundColor(getResources().getColor(R.color.grey));
                btnAttendingYes.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                btnShowAll.setBackgroundColor(getResources().getColor(R.color.grey));
                showAll = false;
                attending = true;
            }
        });

        btnAttendingNo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAttendingYes.setBackgroundColor(getResources().getColor(R.color.grey));
                btnAttendingNo.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                btnShowAll.setBackgroundColor(getResources().getColor(R.color.grey));
                showAll = false;
                attending = false;
            }
        });

        btnShowAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAttendingYes.setBackgroundColor(getResources().getColor(R.color.grey));
                btnAttendingNo.setBackgroundColor(getResources().getColor(R.color.grey));
                btnRsvpNo.setBackgroundColor(getResources().getColor(R.color.grey));
                btnRsvpYes.setBackgroundColor(getResources().getColor(R.color.grey));
                btnShowAll.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
                showAll = true;
            }
        });

        btnApplyFilter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                loadListWithConditions(rsvp ? "Yes" : "No", attending ? "Yes" : "No", showAll);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void refreshList() {
        loadList();
        setStat();
    }

    public void setStat() {
        this.helper = new WPSQLiteOpenHelper(getBaseContext());
        this.db = this.helper.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("select (select count(guest_id) from guests),(select count(guest_id) from guests where invitessent = 'Yes'),(select count(guest_id) from guests where attending = 'Yes'),(select count(guest_id) from guests where side = 'Bride'),(select count(guest_id) from guests where side = 'Groom'),(select count(guest_id) from guests where side = 'Child')", null);
        cursor.moveToFirst();
        int guestscnt = cursor.getInt(0);
        int invitessentcnt = cursor.getInt(1);
        int attendingcnt = cursor.getInt(2);
        int sidebridecnt = cursor.getInt(3);
        int sidegroomcnt = cursor.getInt(4);
        int sidechildcnt = cursor.getInt(5);
        cursor.close();
        this.labelGuestsName.setText("Guests (" + guestscnt + ")");
        this.labelInvitessent.setText("Invites sent\n(" + invitessentcnt + ")");
        this.labelAttending.setText("Attending\n(" + attendingcnt + ")");
        this.statGuests.setText(String.format("Total: %d (Bride: %d, Groom: %d)", new Object[]{Integer.valueOf(guestscnt), Integer.valueOf(sidebridecnt), Integer.valueOf(sidegroomcnt)}));
        this.db.close();
    }

    public void loadList() {
        Cursor cursor = this.cr.query(CONTENT_URI_GUESTS, null, null, null, "firstname asc");
        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        GuestListAdapter listAdapter = new GuestListAdapter(this.act, R.layout.guest, hm);
        this.ListViewGuests = (ListView) this.act.findViewById(R.id.listGuests);
        this.ListViewGuests.setAdapter(listAdapter);
        this.ListViewGuests.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int guest_id = Integer.parseInt((String) hm.get("guest_id[" + position + "]"));
                final String firstname = (String) hm.get("firstname[" + position + "]");
                final String lastname = (String) hm.get("lastname[" + position + "]");
                final String side = (String) hm.get("side[" + position + "]");
                final String invites = (String) hm.get("invitessent[" + position + "]");
                final String attending = (String) hm.get("attending[" + position + "]");
                Guests.this.scrollto = view.getTop();
                Guests.this.positionToMove = position;
                final Dialog dialog = new Dialog(Guests.this.act);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.list_manage);
                TextView editBtn = (TextView) dialog.findViewById(R.id.editBtn);
                TextView deleteBtn = (TextView) dialog.findViewById(R.id.deleteBtn);
                ((TextView) dialog.findViewById(R.id.selctedItem)).setText(new StringBuilder(String.valueOf(firstname)).append(" ").append(lastname).toString());
                editBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Guests.this.openUpdateGuestDialog(guest_id, firstname, lastname, side, invites, attending);
                        dialog.dismiss();
                    }
                });
                deleteBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Guests.this.deleteGuest(guest_id);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    public void loadListWithConditions(String rsvp, String attending, Boolean isAll) {
//        String where = FEED_ID + " IN ("+feedList+") AND " + PLACE_ID + "=\"" +placeId+"\" AND " + PROFILE_ID + "=" + userId ;
        Cursor cursor;
        if (isAll) {
            cursor = this.cr.query(CONTENT_URI_GUESTS, null, null, null, "firstname asc");

        } else {
            String[] projection = new String[]{
                    "invitessent",
                    "attending",

            };
            String[] selectionArgs = new String[]{
                    rsvp,
                    attending,
            };

            String where = "invitessent = ? AND attending = ?";

//            cursor = this.cr.query(CONTENT_URI_GUESTS, null, where, new String[]{rsvp, attending}, "firstname asc");
            cursor = this.cr.query(CONTENT_URI_GUESTS, null, "invitessent = '" + rsvp + "' AND attending = '" + attending + "'", null, null);

        }

        final HashMap<String, String> hm = Common.cursor2HashMap(cursor);
        cursor.close();
        GuestListAdapter listAdapter = new GuestListAdapter(this.act, R.layout.guest, hm);
        this.ListViewGuests = (ListView) this.act.findViewById(R.id.listGuests);
        this.ListViewGuests.setAdapter(listAdapter);
        this.ListViewGuests.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int guest_id = Integer.parseInt((String) hm.get("guest_id[" + position + "]"));
                final String firstname = (String) hm.get("firstname[" + position + "]");
                final String lastname = (String) hm.get("lastname[" + position + "]");
                final String side = (String) hm.get("side[" + position + "]");
                final String invites = (String) hm.get("invitessent[" + position + "]");
                final String attending = (String) hm.get("attending[" + position + "]");
                Guests.this.scrollto = view.getTop();
                Guests.this.positionToMove = position;
                final Dialog dialog = new Dialog(Guests.this.act);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.list_manage);
                TextView editBtn = (TextView) dialog.findViewById(R.id.editBtn);
                TextView deleteBtn = (TextView) dialog.findViewById(R.id.deleteBtn);
                ((TextView) dialog.findViewById(R.id.selctedItem)).setText(new StringBuilder(String.valueOf(firstname)).append(" ").append(lastname).toString());
                editBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Guests.this.openUpdateGuestDialog(guest_id, firstname, lastname, side, invites, attending);
                        dialog.dismiss();
                    }
                });
                deleteBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Guests.this.deleteGuest(guest_id);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    public void scrollList() {
        this.ListViewGuests.setSelectionFromTop(this.positionToMove, this.scrollto);
    }

    public void toggleStatus(int guest_id) {
        String selection = "guest_id=" + guest_id;
        Cursor cursor = this.cr.query(CONTENT_URI_GUESTS, null, selection, null, null);
        HashMap<String, String> tmphm = Common.cursor2HashMap(cursor);
        cursor.close();
        String newStatus = ((String) tmphm.get("status[0]")).equals("T") ? "D" : "T";
        ContentValues contVal = new ContentValues();
        contVal.put("status", newStatus);
        this.cr.update(CONTENT_URI_GUESTS, contVal, selection, null);
    }

    public void openNewGuestDialog(View view) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.guest_form);
        final TextView fntv = (TextView) dialog.findViewById(R.id.firstnameEditText);
        final TextView lntv = (TextView) dialog.findViewById(R.id.lastnameEditText);
        final TextView stv = (TextView) dialog.findViewById(R.id.sideValue);
        final TextView itv = (TextView) dialog.findViewById(R.id.invitesValue);
        final TextView atv = (TextView) dialog.findViewById(R.id.attendingValue);
        RadioButton radioSideBride = (RadioButton) dialog.findViewById(R.id.radioSideBride);
        RadioButton radioSideGroom = (RadioButton) dialog.findViewById(R.id.radioSideGroom);
        RadioButton radioSideChild = (RadioButton) dialog.findViewById(R.id.radioSideChild);
        RadioButton radioInvitesYes = (RadioButton) dialog.findViewById(R.id.radioInvitesYes);
        RadioButton radioInvitesNo = (RadioButton) dialog.findViewById(R.id.radioInvitesNo);
        RadioButton radioAttendingYes = (RadioButton) dialog.findViewById(R.id.radioAttendingYes);
        RadioButton radioAttendingNo = (RadioButton) dialog.findViewById(R.id.radioAttendingNo);
        RadioButton radioAttendingUndecided = (RadioButton) dialog.findViewById(R.id.radioAttendingUndecided);
        radioSideBride.setChecked(true);
        radioInvitesNo.setChecked(true);
        radioAttendingUndecided.setChecked(true);
        TextView formOKBtn = (TextView) dialog.findViewById(R.id.formOKBtn);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        radioSideBride.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                stv.setText("Bride");
            }
        });
        radioSideGroom.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                stv.setText("Groom");
            }
        });
        radioSideChild.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                stv.setText("Child");
            }
        });
        radioInvitesYes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                itv.setText("Yes");
            }
        });
        radioInvitesNo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                itv.setText("No");
            }
        });
        radioAttendingYes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                atv.setText("Yes");
            }
        });
        radioAttendingNo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                atv.setText("No");
            }
        });
        radioAttendingUndecided.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                atv.setText("Undecided");
            }
        });
        formOKBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String firstname = fntv.getText().toString();
                String lastname = lntv.getText().toString();
                String side = stv.getText().toString();
                String invitessent = itv.getText().toString();
                String attending = atv.getText().toString();
                if (!Common.checkValue(firstname)) {
                    Guests.this.showErrMSG(11);
                } else if (Common.checkValue(side)) {
                    Guests.this.insertGuest(firstname, lastname, side, invitessent, attending);
                    Guests.this.showMSG(10);
                    Guests.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Guests.this.refreshList();
                } else {
                    Guests.this.showErrMSG(21);
                }
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Guests.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openUpdateGuestDialog(int t_id, String firstname, String lastname, String side, String invitessent, String attending) {
        final Dialog dialog = new Dialog(this.act);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.guest_form);
        final TextView fntv = (TextView) dialog.findViewById(R.id.firstnameEditText);
        final TextView lntv = (TextView) dialog.findViewById(R.id.lastnameEditText);
        final TextView stv = (TextView) dialog.findViewById(R.id.sideValue);
        final TextView itv = (TextView) dialog.findViewById(R.id.invitesValue);
        final TextView atv = (TextView) dialog.findViewById(R.id.attendingValue);
        fntv.setText(firstname);
        lntv.setText(lastname);
        stv.setText(side);
        itv.setText(invitessent);
        atv.setText(attending);
        RadioButton radioSideBride = (RadioButton) dialog.findViewById(R.id.radioSideBride);
        RadioButton radioSideGroom = (RadioButton) dialog.findViewById(R.id.radioSideGroom);
        RadioButton radioSideChild = (RadioButton) dialog.findViewById(R.id.radioSideChild);
        RadioButton radioInvitesYes = (RadioButton) dialog.findViewById(R.id.radioInvitesYes);
        RadioButton radioInvitesNo = (RadioButton) dialog.findViewById(R.id.radioInvitesNo);
        RadioButton radioAttendingYes = (RadioButton) dialog.findViewById(R.id.radioAttendingYes);
        RadioButton radioAttendingNo = (RadioButton) dialog.findViewById(R.id.radioAttendingNo);
        RadioButton radioAttendingUndecided = (RadioButton) dialog.findViewById(R.id.radioAttendingUndecided);
        if (side.equals("Bride")) {
            radioSideBride.setChecked(true);
        }
        if (side.equals("Groom")) {
            radioSideGroom.setChecked(true);
        }
        if (side.equals("Child")) {
            radioSideChild.setChecked(true);
        }
        if (invitessent.equals("Yes")) {
            radioInvitesYes.setChecked(true);
        }
        if (invitessent.equals("No")) {
            radioInvitesNo.setChecked(true);
        }
        if (attending.equals("Yes")) {
            radioAttendingYes.setChecked(true);
        }
        if (attending.equals("No")) {
            radioAttendingNo.setChecked(true);
        }
        if (attending.equals("Undecided")) {
            radioAttendingUndecided.setChecked(true);
        }
        TextView formOKBtn = (TextView) dialog.findViewById(R.id.formOKBtn);
        TextView formCloseBtn = (TextView) dialog.findViewById(R.id.formCloseBtn);
        final int guest_id = t_id;
        radioSideBride.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                stv.setText("Bride");
            }
        });
        radioSideGroom.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                stv.setText("Groom");
            }
        });
        radioSideChild.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                stv.setText("Child");
            }
        });
        radioInvitesYes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                itv.setText("Yes");
            }
        });
        radioInvitesNo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                itv.setText("No");
            }
        });
        radioAttendingYes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                atv.setText("Yes");
            }
        });
        radioAttendingNo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                atv.setText("No");
            }
        });
        radioAttendingUndecided.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                atv.setText("Undecided");
            }
        });
        formOKBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String firstname = fntv.getText().toString();
                String lastname = lntv.getText().toString();
                String side = stv.getText().toString();
                String invitessent = itv.getText().toString();
                String attending = atv.getText().toString();
                if (!Common.checkValue(firstname)) {
                    Guests.this.showErrMSG(11);
                } else if (Common.checkValue(side)) {
                    Guests.this.updateGuest(guest_id, firstname, lastname, side, invitessent, attending);
                    Guests.this.showMSG(20);
                    Guests.this.hideSoftKeyboard(v);
                    dialog.dismiss();
                    Guests.this.refreshList();
                    Guests.this.scrollList();
                } else {
                    Guests.this.showErrMSG(21);
                }
            }
        });
        formCloseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Guests.this.hideSoftKeyboard(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void insertGuest(String firstname, String lastname, String side, String invitessent, String attending) {
        ContentValues contVal = new ContentValues();
        contVal.put("firstname", firstname);
        contVal.put("lastname", lastname);
        contVal.put("side", side);
        contVal.put("invitessent", invitessent);
        contVal.put("attending", attending);
        contVal.put("status", "T");
        this.cr.insert(CONTENT_URI_GUESTS, contVal);
    }

    public void updateGuest(int guest_id, String firstname, String lastname, String side, String invitessent, String attending) {
        ContentValues contVal = new ContentValues();
        contVal.put("firstname", firstname);
        contVal.put("lastname", lastname);
        contVal.put("side", side);
        contVal.put("invitessent", invitessent);
        contVal.put("attending", attending);
        this.cr.update(CONTENT_URI_GUESTS, contVal, "guest_id=" + guest_id, null);
    }

    public void deleteGuest(int guest_id) {
        this.cr.delete(CONTENT_URI_GUESTS, "guest_id=" + guest_id, null);
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
            msg = getResources().getString(R.string.err_guestname);
        } else if (errcode == 21) {
            msg = getResources().getString(R.string.err_side);
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
}
