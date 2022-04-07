package co.za.wedwise.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Fragment.EnableLlocationFragment;
import co.za.wedwise.R;
import co.za.wedwise.Utils.Prefs;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private boolean mIsBackButtonPressed = false;
    co.za.wedwise.Constants.BaseApp BaseApp;
    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }
        Prefs prefs = new Prefs(SplashActivity.this);
        isLogin = prefs.getPrefBoolean("login");

        BaseApp = co.za.wedwise.Constants.BaseApp.getInstance();

        sharedPreferences = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            public void run() {

                if (!mIsBackButtonPressed) {
                    if (getIntent().hasExtra("action_type")) {
                        //Intent intent = new Intent(SplashActivity.this, DrawerActivity.class);
                        Intent intent = new Intent(SplashActivity.this, LoginFormActivity.class);
                        String action_type = getIntent().getExtras().getString("action_type");
                        String receiverid = getIntent().getExtras().getString("senderid");
                        String title = getIntent().getExtras().getString("title");
                        String icon = getIntent().getExtras().getString("icon");

                        intent.putExtra("icon", icon);
                        intent.putExtra("action_type", action_type);
                        intent.putExtra("receiverid", receiverid);
                        intent.putExtra("title", title);

                        startActivity(intent);
                        finish();
                    } else
                        GPSStatus();
                }
            }
        }, 2000);


    }

    public void onBackPressed() {
        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();

    }


    public void GPSStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {
            Toast.makeText(this, "On Location in High Accuracy", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
        } else {
            GetCurrentlocation();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            GPSStatus();
        }
    }


    private FusedLocationProviderClient mFusedLocationClient;

    private void GetCurrentlocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enable_location();
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            // if we successfully get the location of the user then we will save the locatio into
                            //locally and go to the Main view
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.Lat, "" + location.getLatitude());
                            editor.putString(Constants.Lon, "" + location.getLongitude());
                            editor.commit();
                            if (isLogin){
                                startActivity(new Intent(SplashActivity.this, DrawerActivity.class));
                            }else {
                                startActivity(new Intent(SplashActivity.this, LoginFormActivity.class));
                            }

//                            startActivity(new Intent(SplashActivity.this, DrawerActivity.class));
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            finish();
                        } else {

                            if (sharedPreferences.getString(Constants.Lat, "").equals("") || sharedPreferences.getString(Constants.Lon, "").equals("")) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.Lat, "33.738045");
                                editor.putString(Constants.Lon, "73.084488");
                                editor.commit();
                            }
                            if (isLogin){
                                startActivity(new Intent(SplashActivity.this, DrawerActivity.class));
                            }else {
                                startActivity(new Intent(SplashActivity.this, LoginFormActivity.class));
                            }
//                            startActivity(new Intent(SplashActivity.this, DrawerActivity.class));
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            finish();
                        }
                    }
                });
    }


    private void enable_location() {
        EnableLlocationFragment enable_llocationFragment = new EnableLlocationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        getSupportFragmentManager().popBackStackImmediate();
        transaction.replace(R.id.splash, enable_llocationFragment).addToBackStack(null).commit();

    }
}
