package co.za.wedwise.Item;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ornolfr.ratingview.RatingView;

import co.za.wedwise.Activity.DrawerActivity;
import co.za.wedwise.Activity.LoginFormActivity;
import co.za.wedwise.Activity.PropertyDetailActivity;
import co.za.wedwise.Constants.BaseApp;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Fragment.ChatFragment;
import co.za.wedwise.Fragment.HomeFragment;
import co.za.wedwise.Models.PropertyModels;
import co.za.wedwise.R;
import co.za.wedwise.Utils.DatabaseHelper;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by za on 3/24/2019.
 */

public class GridItem extends RecyclerView.Adapter<GridItem.ItemRowHolder> {

    private ArrayList<PropertyModels> dataList;
    private Activity mContext;
    private DatabaseHelper databaseHelper;
    BaseApp baseApp;
    SharedPreferences sharedPreferences;
    private Fragment mFragment;
    Boolean isFavEnable;

    public GridItem(Activity context, ArrayList<PropertyModels> dataList, Fragment fragment, Boolean isFavouritable) {
        this.dataList = dataList;
        this.mContext = context;
        databaseHelper = new DatabaseHelper(mContext);
        baseApp = BaseApp.getInstance();
        mFragment = fragment;
        isFavEnable = isFavouritable;
        sharedPreferences = mContext.getSharedPreferences(Constants.pref_name, MODE_PRIVATE);

    }

    public GridItem(Activity context, ArrayList<PropertyModels> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        databaseHelper = new DatabaseHelper(mContext);
        baseApp = BaseApp.getInstance();
        sharedPreferences = mContext.getSharedPreferences(Constants.pref_name, MODE_PRIVATE);

    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {

        String latitude = sharedPreferences.getString(Constants.Lat, "");
        String longitude = sharedPreferences.getString(Constants.Lon, "");

        final PropertyModels singleItem = dataList.get(position);
        holder.text.setText(singleItem.getName());
//        holder.price.setText(singleItem.getCityName());
        holder.price.setText(singleItem.getCityName());
//        holder.price.setText("$"+singleItem.getPrice());
        holder.address.setText(singleItem.getAddress());
        holder.ratingView.setRating(Float.parseFloat(singleItem.getRateAvg()));
//        holder.purpose.setText(singleItem.getPurpose());
        Picasso.with(mContext)
                .load(singleItem.getImage())
                .resize(100, 100)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.images);

        if (databaseHelper.getFavouriteById(singleItem.getPropid())) {
            holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.gray));
        }

        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(singleItem.getPropid())) {
                    databaseHelper.removeFavouriteById(singleItem.getPropid());
                    holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.gray));
                    sendFavouriteServer(mContext, DrawerActivity.user_id, singleItem.getPropid(), singleItem.getUserId());
                    Toast.makeText(mContext, "Remove To Favourite", Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, singleItem.getPropid());
                    fav.put(DatabaseHelper.KEY_TITLE, singleItem.getName());
                    fav.put(DatabaseHelper.KEY_IMAGE, singleItem.getImage());
                    fav.put(DatabaseHelper.KEY_RATE, singleItem.getRateAvg());
                    fav.put(DatabaseHelper.KEY_BED, singleItem.getBed());
                    fav.put(DatabaseHelper.KEY_BATH, singleItem.getBath());
                    fav.put(DatabaseHelper.KEY_ADDRESS, singleItem.getAddress());
                    fav.put(DatabaseHelper.KEY_AREA, singleItem.getArea());
                    fav.put(DatabaseHelper.KEY_CITY, singleItem.getCityName());
                    fav.put(DatabaseHelper.KEY_PRICE, singleItem.getPrice());
                    fav.put(DatabaseHelper.KEY_LAT, singleItem.getLatitude());
                    fav.put(DatabaseHelper.KEY_LONG, singleItem.getLongitude());

//                    fav.put(DatabaseHelper.KEY_PURPOSE, singleItem.getPurpose());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
                    Toast.makeText(mContext, "Add To Favourite", Toast.LENGTH_SHORT).show();
                    sendFavouriteServer(mContext, DrawerActivity.user_id, singleItem.getPropid(), singleItem.getUserId());
                }
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + singleItem.getPhone()));
                mContext.startActivity(callIntent);
            }
        });

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataList != null && dataList.get(position).getEmail() != null) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + dataList.get(position).getEmail()));
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(baseApp, "Unable to send email right now.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                BannerAds.ShowInterstitialAds(mContext);
                Intent intent = new Intent(mContext, PropertyDetailActivity.class);
                intent.putExtra("Id", singleItem.getPropid());
                if (mFragment != null) {
                    mFragment.startActivityForResult(intent, 101);
                } else {
                    mContext.startActivityForResult(intent, 101);
                }


            }
        });
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (baseApp.getIsLogin()) {
                    getData(mContext, singleItem.getPropid());
                    //chatFragment(DrawerActivity.user_id, singleItem.getUserId(), singleItem.getNameUser(), singleItem.getImageUser());
                } else {
                    Intent intent = new Intent(mContext, LoginFormActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });
//
//        if (latitude != null && !latitude.isEmpty()) {
//
//            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
//
//
////            Location startPoint = new Location("locationA");
////            startPoint.setLatitude(Double.parseDouble(latitude));
////            startPoint.setLongitude(Double.parseDouble(longitude));
//
//            Location startPoint = new Location("locationA");
//            startPoint.setLatitude(sharedPreferences.getFloat(Constants.latitude, 0));
//            startPoint.setLongitude(sharedPreferences.getFloat(Constants.longitude, 0));
//
//            Location endPoint = new Location("locationA");
//            endPoint.setLatitude(Double.parseDouble(singleItem.getLatitude()));
//            endPoint.setLongitude(Double.parseDouble(singleItem.getLongitude()));
//
//            double distance = startPoint.distanceTo(endPoint);
//            Log.e(">>>>", "onBindViewHolder: " + distance);
//
//            distance = distance / 1000;
//            holder.address.setText(new DecimalFormat("##.#").format(distance) + " km");
//            //holder.address.setText(distance + " km");
//        }

        //holder.address.setText(singleItem.getLatitude());
        double distance = Double.parseDouble(singleItem.getLatitude());
        holder.address.setText(new DecimalFormat("##.#").format(distance) + " km");

    }

    private void getData(Activity mContext, String propid) {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.PROPPERTYDETAIL + propid, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        try {
                            JSONObject jsonObject = new JSONObject(respo);
                            String code = jsonObject.optString("code");
                            if (code.equals("200")) {
                                JSONArray msg = jsonObject.getJSONArray("msg");
                                String user_id = null, fullname = null, imageprofile = null;
                                for (int i = 0; i < msg.length(); i++) {
                                    JSONObject userdata = msg.getJSONObject(i);
                                    user_id = userdata.getString("userid");
                                    fullname = userdata.getString("fullname");
                                    imageprofile = userdata.getString("imageprofile");
                                }
                                chatFragment(DrawerActivity.user_id, user_id, fullname, imageprofile);
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

    public void chatFragment(String senderid, String receiverid, String name, String picture) {
        ChatFragment chat_fragment = new ChatFragment();
        AppCompatActivity activity = (AppCompatActivity) mContext;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString("Sender_Id", senderid);
        args.putString("Receiver_Id", receiverid);
        args.putString("picture", picture);
        args.putString("name", name);
        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.drawer_layout, chat_fragment).commit();

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text, price, address, purpose;
        ImageView images, favourite, call, chat, message;
        RatingView ratingView;
        LinearLayout lyt_parent;

        ItemRowHolder(View itemView) {
            super(itemView);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            images = itemView.findViewById(R.id.image);
            call = itemView.findViewById(R.id.call);
            chat = itemView.findViewById(R.id.chat);
            message = itemView.findViewById(R.id.message);
            text = itemView.findViewById(R.id.text);
            favourite = itemView.findViewById(R.id.favourite);
            price = itemView.findViewById(R.id.price);
            address = itemView.findViewById(R.id.address);
//            purpose = itemView.findViewById(R.id.textPurpose);
            ratingView = itemView.findViewById(R.id.ratingView);
        }
    }

    public void updateAdapter() {
        notifyDataSetChanged();
    }

    private void sendFavouriteServer(final Context context, String user_id, String venue_id, String owner_id) {
        JSONObject parameters = new JSONObject();

        RequestQueue rq = Volley.newRequestQueue(mContext);
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
}
