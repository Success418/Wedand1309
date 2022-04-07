package co.za.wedwise.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.za.wedwise.Constants.BaseApp;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Fragment.ChatFragment;
import co.za.wedwise.Item.AmenitiesItem;
import co.za.wedwise.Item.GalleryItem;
import co.za.wedwise.Item.ReviewItem;
import co.za.wedwise.Models.PropertyModels;
import co.za.wedwise.Models.ReviewModel;
import co.za.wedwise.R;
import co.za.wedwise.Utils.CustomDateTimePicker;
import co.za.wedwise.Utils.DatabaseHelper;
import co.za.wedwise.Utils.NetworkUtils;
import co.za.wedwise.adapter.ReviewAdapter;

//import co.za.wedwise.Utils.BannerAds;

/**
 * Created by za on 3/26/2019.
 */

public class PropertyDetailActivity extends AppCompatActivity {

    TextView propName, address, nameuser, category, type, city, price, bed, bath, area, ratenow, fulladdress;
    String Id;
    ImageView imageuser, images, backButton, likeButton;
    PropertyModels item;
    RelativeLayout progress;
    LinearLayout llprofile, delete, chat, phone, direction, send_message, appointment;
    RatingView ratingView;
    DatabaseHelper databaseHelper;
    ArrayList<PropertyModels> mPropertyList;
    ArrayList<String> mAmenities, addgallery;
    WebView description;
    RecyclerView gallery, amenities;
    FloatingActionButton fab;
    GalleryItem galleryItem;
    BaseApp baseApp;
    CardView rledit;
    ProgressDialog pDialog;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View rating_sheet;
    AmenitiesItem amenitiesItem;
    SharedPreferences sharedPreferences;
    ImageView Imgshare;
    CustomDateTimePicker custom;
    Context context = this;
    String mainImage = "";
    ImageView imgFav;
    TextView tv_like_count, tv_operating_hours;
    ArrayList<ReviewItem> reviewItemsArrayList = new ArrayList<>();
    ReviewAdapter reviewAdapter;
    RecyclerView review_recyclerview;
    String userReview = "";
    Float userRating = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propertydetail);

        sharedPreferences = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);

        LinearLayout mAdViewLayout = findViewById(R.id.adView);
//        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);
        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        rating_sheet = findViewById(R.id.rating_sheet);
        mBehavior = BottomSheetBehavior.from(rating_sheet);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        item = new PropertyModels();
        baseApp = BaseApp.getInstance();
        mPropertyList = new ArrayList<>();
        mAmenities = new ArrayList<>();
        addgallery = new ArrayList<>();
        phone = findViewById(R.id.phone);
        direction = findViewById(R.id.direction);
        send_message = findViewById(R.id.send_message);
        appointment = findViewById(R.id.appointment);
        propName = findViewById(R.id.propertyname);
//        fab = findViewById(R.id.fab);
        progress = findViewById(R.id.progress);
        address = findViewById(R.id.address);
        fulladdress = findViewById(R.id.fulladdress);
        nameuser = findViewById(R.id.name);
        imageuser = findViewById(R.id.imageuser);
        images = findViewById(R.id.image);
        ratingView = findViewById(R.id.ratingView);
        backButton = findViewById(R.id.back_btn);
        likeButton = findViewById(R.id.like_btn);

        category = findViewById(R.id.category);
        type = findViewById(R.id.type);
        city = findViewById(R.id.city);
        price = findViewById(R.id.propertyprice);
        tv_like_count = findViewById(R.id.tv_like_count);
        tv_operating_hours = findViewById(R.id.tv_operating_hours);
        bed = findViewById(R.id.bed);
        bath = findViewById(R.id.bath);
        ratenow = findViewById(R.id.rate);
        area = findViewById(R.id.square);
        description = findViewById(R.id.description);
        gallery = findViewById(R.id.galleryre);
        amenities = findViewById(R.id.amenities);
        chat = findViewById(R.id.chat);
        llprofile = findViewById(R.id.llprofile);
        rledit = findViewById(R.id.rledit);
        delete = findViewById(R.id.lldelete);
        Imgshare = findViewById(R.id.imgShare);
        imgFav = findViewById(R.id.fav_btn);

//        fab.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDone();
            }
        });

        imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null) {
                    if (item.getLike()) {
                        likeUnlike(true);
                    } else {
                        likeUnlike(false);
                    }

                } else {
                    Toast.makeText(baseApp, "Something went wrong while like the venue", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ratenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null) {
                    rateNow(userReview, userRating);
                }

            }
        });

        custom = new CustomDateTimePicker(this,
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int day,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
//                        showpopupForMessage(dateSelected.toString());
                        //                        ((TextInputEditText) findViewById(R.id.edtEventDateTime))
//                        edtEventDateTime.setText("");
//                        edtEventDateTime.setText(year
//                                + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH)
//                                + " " + hour24 + ":" + min
//                                + ":" + sec);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        /**
         * Pass Directly current time format it will return AM and PM if you set
         * false
         */
        custom.set24HourFormat(true);
        /**
         * Pass Directly current data and time to show when it pop up
         */
        custom.setDate(Calendar.getInstance());
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseApp.getIsLogin()) {
                    chatFragment(DrawerActivity.user_id, item.getUserId(), item.getNameUser(), item.getImageUser());
                } else {
                    Intent intent = new Intent(context, LoginFormActivity.class);
                    startActivity(intent);
                }
            }
        });
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null && item.getEmail() != null) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + item.getEmail()));
                    startActivity(intent);
                } else {
                    Toast.makeText(baseApp, "Unable to send email right now.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Book Apppointment", "");
//                custom.showDialog();
                selectDate();

            }
        });
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + item.getLatitude() + "," + item.getLongitude() + " (" + item.getName() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(intent);
            }
        });
        gallery.setHasFixedSize(true);

        gallery.setNestedScrollingEnabled(false);
        gallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        amenities.setHasFixedSize(true);
        amenities.setNestedScrollingEnabled(false);
        amenities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        Imgshare.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (check_ReadStoragepermission() && check_writeStoragepermission()) {
                    if (mainImage != null && !mainImage.equals("")) {
                        Picasso.with(context).load(item.getImage())
                                //.resize(100, 100)
                                //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .placeholder(R.drawable.image_placeholder)
                                .into(shareNormal);
                        //Picasso.with(context).load(mainImage).into(shareNormal);
                    } else {
                        Toast.makeText(context, "Unable to load image for sharing", Toast.LENGTH_SHORT).show();

                    }
                } else {

                }
            }
        });


        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(item.getPropid())) {
                    databaseHelper.removeFavouriteById(item.getPropid());
                    likeButton.setColorFilter(getResources().getColor(R.color.gray));
                    Toast.makeText(context, "Removed From Bookmarks", Toast.LENGTH_SHORT).show();
                    sendFavouriteServer(DrawerActivity.user_id, item.getPropid(), item.getUserId());
                } else {
                    fav.put(DatabaseHelper.KEY_ID, item.getPropid());
                    fav.put(DatabaseHelper.KEY_TITLE, item.getName());
                    fav.put(DatabaseHelper.KEY_IMAGE, item.getImage());
                    fav.put(DatabaseHelper.KEY_RATE, item.getRateAvg());
                    fav.put(DatabaseHelper.KEY_BED, item.getBed());
                    fav.put(DatabaseHelper.KEY_BATH, item.getBath());
                    fav.put(DatabaseHelper.KEY_ADDRESS, item.getAddress());
                    fav.put(DatabaseHelper.KEY_AREA, item.getArea());
                    fav.put(DatabaseHelper.KEY_CITY, item.getCityName());
                    fav.put(DatabaseHelper.KEY_PRICE, item.getPrice());
                    fav.put(DatabaseHelper.KEY_PURPOSE, item.getPurpose());
                    fav.put(DatabaseHelper.KEY_LAT, item.getLatitude());
                    fav.put(DatabaseHelper.KEY_LONG, item.getLongitude());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    likeButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    Toast.makeText(context, "Added To Bookmarks", Toast.LENGTH_SHORT).show();
                    sendFavouriteServer(DrawerActivity.user_id, item.getPropid(), item.getUserId());
                }
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + item.getPhone()));
                startActivity(callIntent);
            }
        });
        isFavourite();
        getData();

        review_recyclerview = findViewById(R.id.review_recyclerview);

        review_recyclerview.setLayoutManager(new LinearLayoutManager(this));


    }

    public void showpopupForMessage(final String date, final String time) {

        final EditText edittext = new EditText(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialog);
        alert.setMessage("Enter Your Message");
        alert.setTitle("");
        edittext.setTextColor(getResources().getColor(R.color.black));
        edittext.setHint("Enter Your Message Here");
        alert.setView(edittext);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
//            Editable YouEditTextValue = edittext.getText();
                //OR
                String emailDesc = edittext.getText().toString();
                if (TextUtils.isEmpty(emailDesc)) {
                    Toast.makeText(baseApp, "Please enter your message", Toast.LENGTH_SHORT).show();
                } else {
                    if (date != null && !date.equals("")) {
                        emailDesc = "Requested appointment date:- " + date + "\n \n" + "Requested appointment time:- " + time + "\n \n " + emailDesc;
                    }
                    if (item != null && item.getEmail() != null) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + item.getEmail() + "?subject=" + Uri.encode("Appointment Request") + "&body=" + Uri.encode(emailDesc)));
                        startActivity(intent);
                    } else {
                        Toast.makeText(baseApp, "Unable to send email right now.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

    private void rateNow(String review, Float rating) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View mDialog = getLayoutInflater().inflate(R.layout.sheet_rating, null);
        ImageView btnclose = mDialog.findViewById(R.id.bt_close);
        final RatingView ratingView = mDialog.findViewById(R.id.ratingView);
        final customfonts.EditTextSFProDisplayMedium etReview = mDialog.findViewById(R.id.et_review);
        etReview.setText(review);
        ratingView.setRating(rating);
        Button submit = mDialog.findViewById(R.id.submit);
        final String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etReview.getText())) {
                    Toast.makeText(PropertyDetailActivity.this, "Please write a review", Toast.LENGTH_SHORT).show();
                } else {


                    pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Loading...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    if (NetworkUtils.isConnected(context)) {
                        JSONObject parameters = new JSONObject();
                        RequestQueue rq = Volley.newRequestQueue(context);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, Constants.RATING + Id + "&rate=" + ratingView.getRating() + "&device_id=" + deviceId + "&user_id=" + DrawerActivity.user_id + "&review=" + etReview.getText().toString(), parameters, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String respo = response.toString();
                                        Log.d("responce", respo);
                                        pDialog.dismiss();
                                        Toast.makeText(context, "Thanks For Review", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error
                                        Log.d("respo", error.toString());
                                        Toast.makeText(context, "Problem", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        rq.getCache().clear();
                        rq.add(jsonObjectRequest);
                        mBottomSheetDialog.hide();
                    } else {
                        Toast.makeText(context, "No connection Internet", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(mDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    private void getData() {
        Map<String, String> parameters = new HashMap<>();
//        parameters.put("user_id", DrawerActivity.user_id);
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.PROPPERTYDETAIL + Id + "&user_id=" + DrawerActivity.user_id, new JSONObject(parameters), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        getDataProperty(respo);
                        getDataImage(respo);
                        progress.setVisibility(View.GONE);
//                        fab.setVisibility(View.VISIBLE);
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
    }

    public void getDataProperty(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    item.setPropid(userdata.getString("propid"));
                    item.setEmail(userdata.getString("email"));
                    item.setName(userdata.getString("name"));
                    item.setAddress(userdata.getString("address"));
                    item.setUserId(userdata.getString("userid"));
                    item.setNameUser(userdata.getString("fullname"));
                    item.setImageUser(userdata.getString("imageprofile"));
                    item.setCid(userdata.getString("cid"));
                    item.setCname(userdata.getString("cname"));
                    item.setCityName(userdata.getString("cityname"));
                    item.setPurpose(userdata.getString("purpose"));
                    item.setAmenities(userdata.getString("amenities"));
                    item.setRateAvg(userdata.getString("rate"));
                    item.setImage(userdata.getString("image"));
                    item.setBed(userdata.getString("bed"));
                    item.setBath(userdata.getString("bath"));
                    item.setArea(userdata.getString("area"));
                    item.setPrice(userdata.getString("price"));
                    item.setLatitude(userdata.getString("latitude"));
                    item.setLongitude(userdata.getString("longitude"));
                    item.setDescription(userdata.getString("description"));
                    item.setLike(userdata.getBoolean("isLike"));
                    item.setLikeCount(userdata.getInt("likeCount"));
                    item.setOperatingHours(userdata.getString("operating_hour"));
                    item.setUserId(userdata.getString("owner_id"));


                    if (item.getUserId().equals(DrawerActivity.user_id)) {
                        llprofile.setVisibility(View.GONE);
                        rledit.setVisibility(View.VISIBLE);
                    } else {
                        llprofile.setVisibility(View.VISIBLE);
                        rledit.setVisibility(View.GONE);
                    }

                    String latitude = sharedPreferences.getString(Constants.Lat, "");
                    String longitude = sharedPreferences.getString(Constants.Lon, "");

                    Location startPoint = new Location("locationA");
                    startPoint.setLatitude(Double.parseDouble(latitude));
                    startPoint.setLongitude(Double.parseDouble(longitude));

                    Location endPoint = new Location("locationA");
                    endPoint.setLatitude(Double.parseDouble(item.getLatitude()));
                    endPoint.setLongitude(Double.parseDouble(item.getLongitude()));

                    double distance = startPoint.distanceTo(endPoint);
                    Log.e(">>>>", "onBindViewHolder: " + distance);

                    distance = distance / 1000;
                    //address.setText(distance + " km");
                    address.setText(new DecimalFormat("##.#").format(distance) + " km");

                    propName.setText(item.getName());
                    fulladdress.setText(item.getAddress());
                    ratingView.setRating(Float.parseFloat(item.getRateAvg()));
                    nameuser.setText(item.getNameUser());
                    price.setText(item.getCityName());
                    category.setText(item.getCname());
                    type.setText(item.getPurpose());
                    city.setText(item.getCityName());
                    bed.setText(item.getBed() + " " + "bed");
                    bath.setText(item.getBath() + " " + "bath");
                    area.setText(item.getArea() + " " + "Sq");
                    area.setText(item.getArea() + " " + "Sq");

                    if (item.getLike()) {
                        imgFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                    } else {
                        imgFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike));
                    }

                    tv_like_count.setText(String.valueOf(item.getLikeCount()));

                    if (item.getOperatingHours() != null && !item.getOperatingHours().equals("")) {
                        tv_operating_hours.setText(String.valueOf(item.getOperatingHours()));
                    } else {
                        tv_operating_hours.setText("Not Available");
                    }


                    if (!item.getImageUser().equalsIgnoreCase("")) {
                        Picasso.with(this)
                                .load(item.getImageUser())
                                //.resize(100, 100)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .placeholder(R.drawable.image_placeholder)
                                .into(imageuser);
                        Picasso.with(this)
                                .load(item.getImage())
                                //.resize(100, 100)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .placeholder(R.drawable.image_placeholder)
                                .into(images);
                        mainImage = item.getImage();
                    }


                    String mimeType = "text/html";
                    String encoding = "utf-8";
                    String htmlText = item.getDescription();

                    String text = "<html dir=" + "><head>"
                            + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/NeoSans_Pro_Regular.ttf\")}body{font-family: MyFont;color: #000000;text-align: left;line-height:1}"
                            + "</style></head>"
                            + "<body>"
                            + htmlText
                            + "</body></html>";

                    if (!item.getAmenities().isEmpty())
                        mAmenities = new ArrayList<>(Arrays.asList(item.getAmenities().split(",")));

                    description.loadDataWithBaseURL(null, text, mimeType, encoding, null);

//                        fab.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String geoUri = "http://maps.google.com/maps?q=loc:" + item.getLatitude() + "," + item.getLongitude() + " (" + item.getName() + ")";
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//                                startActivity(intent);
//                            }
//                        });
                    getReviewList(item.getPropid());
                    getRating(item.getPropid());

                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        amenitiesItem = new AmenitiesItem(this, mAmenities);
        amenities.setAdapter(amenitiesItem);
    }

    public void getDataImage(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    addgallery.add(userdata.getString("image"));
                    addgallery.add(userdata.getString("floorplan"));
                    JSONArray username_obj = userdata.getJSONArray("galleryimage");
                    for (int j = 0; i < msg.length(); j++) {
                        JSONObject userdata1 = username_obj.getJSONObject(j);
                        addgallery.add(userdata1.optString("gallery"));


                    }
                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
        galleryItem = new GalleryItem(this, addgallery, R.layout.item_square);
        gallery.setAdapter(galleryItem);
        galleryItem.setOnItemClickListener(new GalleryItem.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String viewModel, int pos) {
                Intent i = new Intent(context, FullImageActivity.class);
                i.putExtra(FullImageActivity.EXTRA_POS, pos);
                i.putStringArrayListExtra(FullImageActivity.EXTRA_IMGS, addgallery);
                startActivity(i);
            }
        });

    }

    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id)) {
            likeButton.setColorFilter(getResources().getColor(R.color.colorPrimary));
        } else {
            likeButton.setColorFilter(getResources().getColor(R.color.gray));
        }
    }

    public void chatFragment(String senderid, String receiverid, String name, String picture) {
        ChatFragment chat_fragment = new ChatFragment();
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("Sender_Id", senderid);
        args.putString("Receiver_Id", receiverid);
        args.putString("picture", picture);
        args.putString("name", name);
        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainFragment, chat_fragment).commit();

    }

    private void delete() {
        Intent intent = new Intent(this.getApplicationContext(), DrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.DELETEPROPERTY + item.getPropid() + "&userid=" + DrawerActivity.user_id, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
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
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to Delete?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        delete();
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

    private Target shareNormal = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // Get access to the URI for the bitmap
            Uri bmpUri = getLocalBitmapUri(bitmap);
            if (bmpUri != null) {
                // Construct a ShareIntent with link to image
                try {
                    Intent sendIntent = new Intent();
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendIntent.setAction(Intent.ACTION_SEND);
                    if (bmpUri != null) {
                        sendIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    }
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Unable to load image for sharing", Toast.LENGTH_SHORT).show();
                }
            } else {
                // ...sharing failed, handle error
                Toast.makeText(context, "Unable to load image for sharing", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Toast.makeText(context, "Unable to load image for sharing", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public Uri getLocalBitmapUri(Bitmap bmp) {
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".jpeg");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.permission_Read_data);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean check_writeStoragepermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.permission_write_data);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    private void selectDate() {
        int mYear, mMonth, mDay;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(context, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
//                tvDobGeneralDetail.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                selectTime(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            }
        }, mYear, mMonth, mDay);
        Calendar calendar = Calendar.getInstance();
        mDatePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
        mDatePicker.show();
    }

    private void selectTime(final String date) {
        //Open Time Picker for choose BirthTime
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String AM_PM;
                if (selectedHour < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }
//                tvBirthTime.setText(pad(selectedHour) + ":" + pad(selectedMinute) + " " + AM_PM);
                showpopupForMessage(date, pad(selectedHour) + ":" + pad(selectedMinute) + " " + AM_PM);
            }
        }, hour, minute, true);
        mTimePicker.show();


    }

    public String pad(int input) {
        // for converting time value for time picker
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    private void likeUnlike(final Boolean likeStatus) {
        Map<String, String> parameters = new HashMap<>();
//        parameters.put("user_id", DrawerActivity.user_id);
//        parameters.put("venue_id", Id);

        RequestQueue rq = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, Constants.LIKE_UNLIKE + "&user_id=" + DrawerActivity.user_id + "&venue_id=" + Id, new JSONObject(parameters), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        try {
                            if (response.getString("code").equals("200")) {
                                Toast.makeText(baseApp, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                if (likeStatus) {
                                    item.setLike(false);
                                    imgFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike));
                                    int count = Integer.parseInt(tv_like_count.getText().toString()) - 1;
                                    tv_like_count.setText(String.valueOf(count));
                                } else {
                                    item.setLike(true);
                                    imgFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                                    int count = Integer.parseInt(tv_like_count.getText().toString()) + 1;
                                    tv_like_count.setText(String.valueOf(count));
                                }
                            } else {
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo", error.toString());
                        Toast.makeText(context, "Problem occured while like/unlike", Toast.LENGTH_SHORT).show();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
    }

    private void sendFavouriteServer(String user_id, String venue_id, String owner_id) {
        JSONObject parameters = new JSONObject();

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.POST, Constants.FAVOURITE + "&user_id="+user_id+"&venue_id"+venue_id+"&owner_id"+owner_id, parameters, new Response.Listener<JSONObject>() {
                (Request.Method.GET, Constants.FAVOURITE + "&user_id=" + user_id + "&venue_id=" + venue_id + "&owner_id=" + owner_id, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        try {
                            JSONObject jsonObject = new JSONObject(respo);
                            String code = jsonObject.optString("code");
                            if (code.equals("200")) {

                            }
                        } catch (Exception e) {

                        }
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
    }

    private void getReviewList(String venue_id) {
        JSONObject parameters = new JSONObject();

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.POST, Constants.FAVOURITE + "&user_id="+user_id+"&venue_id"+venue_id+"&owner_id"+owner_id, parameters, new Response.Listener<JSONObject>() {
                (Request.Method.GET, Constants.RATING_LIST + "&venue_id=" + venue_id, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        try {
                            JSONObject jsonObject = new JSONObject(respo);
                            String code = jsonObject.optString("code");
                            if (code.equals("200")) {
                                JSONArray mArray = jsonObject.getJSONArray("msg");
                                if (mArray != null && mArray.length() > 0) {
                                    reviewItemsArrayList = new Gson().fromJson(mArray.toString(), new TypeToken<List<ReviewItem>>() {
                                    }.getType());

                                    if (reviewItemsArrayList != null && reviewItemsArrayList.size() > 0) {
                                        reviewAdapter = new ReviewAdapter((Activity) context, reviewItemsArrayList);
                                        review_recyclerview.setAdapter(reviewAdapter);
                                    }
                                }

                            }
                        } catch (Exception e) {

                        }
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
    }

    private void getRating(String venueId) {
        if (NetworkUtils.isConnected(context)) {
            JSONObject parameters = new JSONObject();
            RequestQueue rq = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, Constants.GET_USER_RATING + "&venue_id=" + venueId + "&user_id=" + DrawerActivity.user_id, parameters, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("code").equals("200")) {

                                    JSONObject msg = response.getJSONObject("msg");
                                    userRating = Float.parseFloat(msg.getString("rate"));
                                    userReview = msg.getString("review");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.d("respo", error.toString());
                            Toast.makeText(context, "Problem", Toast.LENGTH_SHORT).show();
                        }
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rq.getCache().clear();
            rq.add(jsonObjectRequest);

        }
    }
}
