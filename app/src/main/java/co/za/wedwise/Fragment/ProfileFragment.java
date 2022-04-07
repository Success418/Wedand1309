package co.za.wedwise.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.za.wedwise.Activity.AboutActivity;
import co.za.wedwise.Activity.DrawerActivity;
import co.za.wedwise.Activity.LoginFormActivity;
import co.za.wedwise.Activity.MyPropertyActivity;
import co.za.wedwise.Activity.PrivacyActivity;
import co.za.wedwise.BuildConfig;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.R;
import co.za.wedwise.Utils.Prefs;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    View setView;
    Context context;
    co.za.wedwise.Constants.BaseApp BaseApp;

    Button logout, login, edit;
    CircleImageView imageProfile;
    DatabaseReference rootref;

    TextView fullName;

    LinearLayout llshare, llrate, llabout, llprivacy, llproperty;
    private Prefs prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        prefs = new Prefs(context);
        BaseApp = co.za.wedwise.Constants.BaseApp.getInstance();
        llabout = setView.findViewById(R.id.llabout);
        llrate = setView.findViewById(R.id.llrate);
        llshare = setView.findViewById(R.id.llshare);
        llproperty = setView.findViewById(R.id.llproperty);
        login = setView.findViewById(R.id.login);
        logout = setView.findViewById(R.id.logout);
        edit = setView.findViewById(R.id.editprofile);
        imageProfile = setView.findViewById(R.id.profileimage);

        llproperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyPropertyActivity.class);
                startActivity(intent);

            }
        });
        llshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });

        llrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editprofile();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginFormActivity.class);
                startActivity(intent);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDone();
            }
        });
        rootref = FirebaseDatabase.getInstance().getReference();

        fullName = setView.findViewById(R.id.fullname);
        llprivacy = setView.findViewById(R.id.llpirvacy);

        llabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AboutActivity.class);
                startActivity(intent);
            }
        });

        llprivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PrivacyActivity.class);
                startActivity(intent);
            }
        });



        return setView;
    }

    @Override
    public void onStart() {
        super.onStart();
        GetUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetUser();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(ProfileFragment.this).attach(ProfileFragment.this).commit();
        }
    }

    private void GetUser() {
        if (BaseApp.getIsLogin()) {
            login.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("userid", DrawerActivity.user_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestQueue rq = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, Constants.USERDATA, parameters, new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(JSONObject response) {
                            String respo = response.toString();
                            Log.d("responce", respo);
                            userinfo(respo);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.d("respo", error.toString());
                        }
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rq.getCache().clear();
            rq.add(jsonObjectRequest);
        } else {
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            llproperty.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }

    }

    public void userinfo(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                JSONObject userdata = msg.getJSONObject(0);

                fullName.setText(userdata.optString("fullname"));
                if (!userdata.optString("imageprofile").equals("") && userdata.optString("imageprofile") != null)
                    Picasso.with(context)
                            .load(userdata.optString("imageprofile"))
                            .placeholder(R.mipmap.ic_launcher)
                            .resize(100, 100)
                            .centerCrop()
                            .into(imageProfile);

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    public void Editprofile() {
        EditProfileFragment editProfile_fragment = new EditProfileFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
         transaction.replace(R.id.drawer_layout, editProfile_fragment).commit();
    }

    public void Logout() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();
        rootref.child("Users").child(DrawerActivity.user_id).child("token").setValue("null");
        BaseApp.saveIsLogin(false);
        prefs.setPrefBoolean("login",false);
        Intent intent = new Intent(context.getApplicationContext(), DrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getFragmentManager().beginTransaction().detach(ProfileFragment.this).attach(ProfileFragment.this).commit();
    }

    public void clickDone() {
        new AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Logout();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
