package co.za.wedwise.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import co.za.wedwise.Activity.DrawerActivity;
import co.za.wedwise.Activity.LoginFormActivity;
import co.za.wedwise.R;
import co.za.wedwise.Constants.Constants;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import static android.content.Context.MODE_PRIVATE;


public class EnableLlocationFragment extends Fragment {


    View getView;
    Context context;
    Button enableLocation;
    SharedPreferences sharedPreferences;

    IOSDialog iosDialog;
    co.za.wedwise.Constants.BaseApp BaseApp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_enablelocation, container, false);
        context = getContext();
        BaseApp = co.za.wedwise.Constants.BaseApp.getInstance();

        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        sharedPreferences = context.getSharedPreferences(Constants.pref_name, MODE_PRIVATE);

        enableLocation = getView.findViewById(R.id.enable_location_btn);
        enableLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();

            }
        });


        return getView;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.LEFT, enter, 300);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    GPSStatus();

                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, 300);
        }
    }



    private void getLocationPermission() {

        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case  123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   GetCurrentlocation();
                } else {
                    Toast.makeText(context, "Please Grant permission", Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }

    private FusedLocationProviderClient mFusedLocationClient;


    public void GPSStatus(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus) {
            Toast.makeText(context, "On Location in High Accuracy", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),2);
        }
        else {
            GetCurrentlocation();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            GPSStatus();
        }
    }
    private void GetCurrentlocation() {
        iosDialog.show();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            iosDialog.cancel();
            getLocationPermission();
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        iosDialog.cancel();
                        if (location != null) {

                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(Constants.Lat,""+location.getLatitude());
                            editor.putString(Constants.Lon,""+location.getLongitude());
                            editor.putFloat(Constants.latitude, (float) location.getLatitude());
                            editor.putFloat(Constants.longitude, (float) location.getLongitude());

                            editor.commit();

                            GoToNext_Activty();

                        }else  {

                            if(sharedPreferences.getString(Constants.Lat,"").equals("") || sharedPreferences.getString(Constants.Lon,"").equals("") ){
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Constants.Lat,"33.738045");
                                editor.putString(Constants.Lon,"73.084488");
                                editor.putFloat(Constants.latitude, (float) 33.738045);
                                editor.putFloat(Constants.longitude, (float) 73.084488);

                                editor.commit();
                            }

                            GoToNext_Activty();
                        }
                    }
                });
    }


    public void GoToNext_Activty(){
        if (BaseApp.getIsLogin()) {
            Intent intent = new Intent(getActivity(), DrawerActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else {
            Intent intent = new Intent(getActivity(), LoginFormActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }



}

