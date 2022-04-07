package co.za.wedwise.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import co.za.wedwise.Constants.BaseApp;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Constants.VersionChecker;
import co.za.wedwise.Fragment.HomeFragment;
import co.za.wedwise.Fragment.MessageFragment;
import co.za.wedwise.Fragment.PropertyFragment;
import co.za.wedwise.Models.AboutModels;
import co.za.wedwise.R;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.za.wedwise.Fragment.FavouriteFragment;
import co.za.wedwise.Fragment.ProfileFragment;
//import co.za.wedwise.Utils.BannerAds;
import co.za.wedwise.Utils.BottomNavigationViewHelper;


public class MainActivity extends AppCompatActivity {
    long mBackPressed;
    public static SharedPreferences sharedPreferences;
    public static String user_id;
    public static String user_name;
    public static String image;
    public static String image1;
    public static String birthday;
    public static String about;
    public static String purchased;
    public static String token;
    BaseApp baseApp;
    LinearLayout llsearch;
    DatabaseReference rootref;
    AboutModels modelAbout;
    public static String title = "none";
    Context context = this;
    EditText search;

    private FragmentManager fragmentManager;
    BottomNavigationView navigation;
    int previousSelect = 0;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    HomeFragment homeFragment = new HomeFragment();
                    navigationItemSelected(0);
                    loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                    llsearch.setVisibility(View.VISIBLE);
                    return true;
                case R.id.property:
                    PropertyFragment propertyFragment = new PropertyFragment();
                    navigationItemSelected(1);
                    loadFrag(propertyFragment, getString(R.string.menu_property), fragmentManager);
                    llsearch.setVisibility(View.GONE);
                    return true;
                case R.id.favourite:
                    FavouriteFragment matchFragment = new FavouriteFragment();
                    navigationItemSelected(2);
                    loadFrag(matchFragment, getString(R.string.menu_favourite), fragmentManager);
                    llsearch.setVisibility(View.GONE);
                    return true;
                case R.id.chat:
                    MessageFragment messageFragment = new MessageFragment();
                    navigationItemSelected(3);
                    loadFrag(messageFragment, getString(R.string.menu_chat), fragmentManager);
                    llsearch.setVisibility(View.GONE);
                    return true;
                case R.id.user:
                    ProfileFragment profileFragment = new ProfileFragment();
                    navigationItemSelected(4);
                    loadFrag(profileFragment, getString(R.string.menu_profile), fragmentManager);
                    llsearch.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout mAdViewLayout = findViewById(R.id.adView);
//        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);
        fragmentManager = getSupportFragmentManager();
        llsearch = findViewById(R.id.llsearch);
        baseApp = BaseApp.getInstance();
        navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        modelAbout = new AboutModels();
        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
        sharedPreferences = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        user_id = sharedPreferences.getString(Constants.uid, "null");
        user_name = sharedPreferences.getString(Constants.f_name, "") + " " + sharedPreferences.getString(Constants.l_name, "");
        image = sharedPreferences.getString(Constants.u_pic, "null");
        image1 = sharedPreferences.getString("image1", "null");
        token = sharedPreferences.getString(Constants.device_token, FirebaseInstanceId.getInstance().getToken());
        rootref = FirebaseDatabase.getInstance().getReference();

        search = findViewById(R.id.search);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String sSearch = search.getText().toString().trim();
                if (TextUtils.isEmpty(sSearch)) {

                    Toast.makeText(context, "Column Can't be Empty", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra("searchtext", sSearch);
                    startActivity(intent);
                    return true;
                }

                return false;
            }

        });

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Constants.versionname = packageInfo.versionName;


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (baseApp.getIsLogin()) {
            rootref.child("Users").child(user_id).child("token").setValue(token);
        } else {
            rootref.child("Users").child(user_id).child("token").setValue("null");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Check_version();

    }

    @Override
    public void onBackPressed() {
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                clickDone();

            }
        } else {
            super.onBackPressed();
        }
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
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


    public void Check_version() {
        VersionChecker versionChecker = new VersionChecker(this);
        versionChecker.execute();
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
    }

    public void navigationItemSelected(int position) {
        previousSelect = position;
    }


}
