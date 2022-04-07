package co.za.wedwise.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import co.za.wedwise.Constants.BaseApp;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Constants.VersionChecker;
import co.za.wedwise.Fragment.FavouriteFragment;
import co.za.wedwise.Fragment.HomeFragment;
import co.za.wedwise.Fragment.MessageFragment;
import co.za.wedwise.Fragment.MyCategoryFragment;
import co.za.wedwise.Fragment.ProfileFragment;
import co.za.wedwise.Models.AboutModels;
import co.za.wedwise.Models.PropertyModels;
import co.za.wedwise.R;
import co.za.wedwise.checklist.checklist.Topics;
import co.za.wedwise.checklist.guestlist.Guests;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager fragmentManager;
    LinearLayout llsearch;
    public static DrawerActivity drawerActivity;
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
    DatabaseReference rootref;
    AboutModels modelAbout;
    public static String title = "none";
    PropertyModels items;
    DrawerLayout drawer;
    Toolbar toolbar;
    de.hdodenhof.circleimageview.CircleImageView imageView_User;
    TextView user_name_tv;
    Context context =this;
    NavigationView mNavigationView;
    View mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLACK));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLACK));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
        drawerActivity = this;
        baseApp = BaseApp.getInstance();
        modelAbout = new AboutModels();

        sharedPreferences = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        user_id = sharedPreferences.getString(Constants.uid, "null");
        user_name = sharedPreferences.getString(Constants.f_name, "") + " " + sharedPreferences.getString(Constants.l_name, "");
        image = sharedPreferences.getString(Constants.u_pic, "null");
        image1 = sharedPreferences.getString("image1", "null");
        token = sharedPreferences.getString(Constants.device_token, FirebaseInstanceId.getInstance().getToken());

        rootref = FirebaseDatabase.getInstance().getReference();

        // NavigationView
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // NavigationView Header
        mHeaderView = mNavigationView.getHeaderView(0);

        // View
        user_name_tv = mHeaderView.findViewById(R.id.user_name_tv);
        // Set username & email
        try {
            user_name_tv.setText(user_name);
        } catch (Exception e) {
        }

        // xyz
        user_name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context,"Click on Username",Toast.LENGTH_SHORT).show();
//Compromised due to removal bottom navigation, we should revert back or
                //have to change all screens Chat,Profile, Guest List etc
                drawer.closeDrawers();
                hideShowAction(true);
                //TODO: Change fragment to Activity
                ProfileFragment profileFragment = new ProfileFragment();
                loadFrag(profileFragment, getString(R.string.menu_profile), fragmentManager);
            }
        });

        imageView_User = mHeaderView.findViewById(R.id.imageView_User);
        imageView_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Change fragment to Activity
                drawer.closeDrawers();
                hideShowAction(true);
                ProfileFragment profileFragment = new ProfileFragment();
                loadFrag(profileFragment, getString(R.string.menu_profile), fragmentManager);
            }
        });
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(3)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        try {
            Picasso.with(getApplicationContext())
                    .load(image)
//                .resize(100,100)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.image_placeholder)
                    .transform(transformation)
                    .into(imageView_User);

//                    .into(imageView_User, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            Bitmap imageBitmap = ((BitmapDrawable) imageView_User.getDrawable()).getBitmap();
//                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
//                            imageDrawable.setCircular(true);
//                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
//                            imageView_User.setImageDrawable(imageDrawable);
//                        }
//                        @Override
//                        public void onError() {
//                            imageView_User.setImageResource(R.drawable.image_placeholder);
//                        }
//                    });
        } catch (Exception e) {
        }


        //mNavigationView.setNavigationItemSelectedListener(this);


//        imageView_cancel = findViewById(R.id.imageView_cancel);
//        imageView_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                drawer.closeDrawers();
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (count == 0) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                }
                return;
            } else {
                clickDone();
            }
        } else {
            super.onBackPressed();
        }
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_maps) {
            String geoUri = "http://maps.google.com/maps";
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            Intent intent = new Intent(context, PicklocationActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_filter) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("");
            String[] types = {"Find By Category", "Change Location"};
            b.setItems(types, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    switch (which) {
                        case 0:
                            MyCategoryFragment propertyFragment = new MyCategoryFragment();
                            loadFrag(propertyFragment, getString(R.string.menu_property), fragmentManager);
                            break;
                        case 1:
                            String geoUri = "http://maps.google.com/maps";
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            Intent intent = new Intent(context, PicklocationActivity.class);
                            startActivity(intent);
                            break;
                    }
                }

            });

            b.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideShowAction(boolean ishide) {
        if (ishide) {
            MenuItem item = toolbar.getMenu().findItem(R.id.action_filter);
            item.setVisible(false);
        } else {
            MenuItem item = toolbar.getMenu().findItem(R.id.action_filter);
            item.setVisible(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            hideShowAction(false);
            HomeFragment homeFragment = new HomeFragment();
            loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
        } else if (id == R.id.nav_category) {
            hideShowAction(true);
            //PropertyFragment propertyFragment = new PropertyFragment();
            MyCategoryFragment propertyFragment = new MyCategoryFragment();
            loadFrag(propertyFragment, getString(R.string.menu_property), fragmentManager);
        } /*else if (id == R.id.nav_my_category) {
            MyCategoryFragment propertyFragment = new MyCategoryFragment();
            loadFrag(propertyFragment, "Category", fragmentManager);
        } */ else if (id == R.id.nav_favourite) {
            hideShowAction(true);
            FavouriteFragment matchFragment = new FavouriteFragment();
            loadFrag(matchFragment, getString(R.string.menu_favourite), fragmentManager);
        } else if (id == R.id.nav_chat) {
            hideShowAction(true);
            MessageFragment messageFragment = new MessageFragment();
            loadFrag(messageFragment, getString(R.string.menu_chat), fragmentManager);
        } else if (id == R.id.nav_profile) {
            hideShowAction(true);
            ProfileFragment profileFragment = new ProfileFragment();
            loadFrag(profileFragment, getString(R.string.menu_profile), fragmentManager);
        } else if (id == R.id.nav_setting) {
            hideShowAction(true);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_checklist) {
            hideShowAction(true);
            Intent intent = new Intent(context, Topics.class);
            startActivity(intent);
        } else if (id == R.id.nav_guestList) {
            hideShowAction(false);
            Intent intent = new Intent(context, Guests.class);
            startActivity(intent);
        } else if (id == R.id.nav_menu_Registerasvendor) {
            hideShowAction(false);
            Intent intent = new Intent(context, RegisterAsVendorActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
