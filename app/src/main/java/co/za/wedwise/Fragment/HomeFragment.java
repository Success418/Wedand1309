package co.za.wedwise.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import co.za.wedwise.Activity.AddPropertyActivity;
import co.za.wedwise.Activity.LoginFormActivity;
import co.za.wedwise.Constants.BaseApp;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Item.CategoryItem;
import co.za.wedwise.Item.GridItem;
import co.za.wedwise.Item.SliderItem;
import co.za.wedwise.Models.CategoryModels;
import co.za.wedwise.Models.CityModels;
import co.za.wedwise.Models.PropertyModels;
import co.za.wedwise.R;
import co.za.wedwise.Utils.NetworkUtils;
import me.relex.circleindicator.CircleIndicator;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<PropertyModels> mSliderList, mLatestList, mPopularList, mAllList;
    ArrayList<CategoryModels> mCategoryList;
    ArrayList<CityModels> mCityList;
    SliderItem sliderItem;
    NestedScrollView mScrollView;
    ProgressBar mProgressBar, progresslatest;
    ViewPager mViewPager;
    CircleIndicator circleIndicator;
    RecyclerView rvLatest;
    Button addProperty;
    CategoryItem categoryItem;
    BaseApp baseApp;
    GridItem popularItem, latestItem;
    RelativeLayout nofound;
    LinearLayout rlslider;
    SwipeRefreshLayout swipe_refresh_latest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mSliderList = new ArrayList<>();
        mLatestList = new ArrayList<>();
        mPopularList = new ArrayList<>();
//        mAllList = new ArrayList<>();
        mCategoryList = new ArrayList<>();
        mCityList = new ArrayList<>();
        baseApp = BaseApp.getInstance();
        mScrollView = rootView.findViewById(R.id.scrollView);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        progresslatest = rootView.findViewById(R.id.progresslatest);
        mViewPager = rootView.findViewById(R.id.viewPager);
        rlslider = rootView.findViewById(R.id.rlslider);
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);
        rvLatest = rootView.findViewById(R.id.latest);
        swipe_refresh_latest = rootView.findViewById(R.id.swipe_refresh_latest);
        swipe_refresh_latest.setOnRefreshListener(this);
        addProperty = rootView.findViewById(R.id.addproperty);
        nofound = rootView.findViewById(R.id.nofound);

        progresslatest.setVisibility(View.VISIBLE);

        rvLatest.setHasFixedSize(true);
        rvLatest.setNestedScrollingEnabled(false);
        rvLatest.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        swipe_refresh_latest.setRefreshing(true);

        if (NetworkUtils.isConnected(getActivity())) {
            getFeaturedItem();
            getCategory();
            getPopularItem();
            getLatestItem();
            getCity();
        } else {
            nofound.setVisibility(View.VISIBLE);
        }
//        morePopular.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AllPopularActivity.class);
//                getActivity().startActivity(intent);
//            }
//        });
//        moreLatest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), AllPropActivity.class);
//                    getActivity().startActivity(intent);
//            }
//        });

        addProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseApp.getIsLogin()) {
                    Intent intent = new Intent(getActivity(), AddPropertyActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginFormActivity.class);
                    getActivity().startActivity(intent);
                }

            }
        });


        return rootView;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();
        }
    }

    private void getFeaturedItem() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.FEATURED, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        getData(respo);
                        mScrollView.setVisibility(View.VISIBLE);
//                        addProperty.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        rlslider.setVisibility(View.VISIBLE);
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

    public void getData(String loginData) {

        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    PropertyModels item = new PropertyModels();
                    item.setPropid(userdata.getString("propid"));
                    item.setName(userdata.getString("name"));
                    item.setAddress(userdata.getString("address"));
                    item.setPrice(userdata.getString("price"));
                    item.setImage(userdata.getString("image"));
                    item.setRateAvg(userdata.getString("rate"));
                    mSliderList.add(item);

                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getCategory() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CATEGORY, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        getDataCategory(respo);
                        mScrollView.setVisibility(View.VISIBLE);
//                        addProperty.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
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

    public void getDataCategory(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    CategoryModels item = new CategoryModels();
                    item.setCategoryId(userdata.getString("cid"));
                    item.setCategoryName(userdata.getString("cname"));
                    item.setCategoryImage(userdata.getString("cimage"));
                    mCategoryList.add(item);

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getCity() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CITY, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        getDataCity(respo);
//                        addProperty.setVisibility(View.VISIBLE);
                        mScrollView.setVisibility(View.VISIBLE);
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

    public void getDataCity(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    CityModels item = new CityModels();
                    item.setCityId(userdata.getString("cityid"));
                    item.setCityName(userdata.getString("cityname"));
                    item.setCityImage(userdata.getString("cityimage"));
                    mCityList.add(item);

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getPopularItem() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.POPULAR, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        getDataPopular(respo);
//                        addProperty.setVisibility(View.VISIBLE);
                        mScrollView.setVisibility(View.VISIBLE);

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

    public void getDataPopular(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    PropertyModels item = new PropertyModels();
                    item.setPropid(userdata.getString("propid"));
                    item.setName(userdata.getString("name"));
                    item.setImage(userdata.getString("image"));
                    item.setAddress(userdata.getString("address"));
                    item.setRateAvg(userdata.getString("rate"));
                    item.setPrice(userdata.getString("price"));
                    item.setPurpose(userdata.getString("purpose"));
                    item.setBed(userdata.getString("bed"));
                    item.setBath(userdata.getString("bath"));
                    item.setArea(userdata.getString("area"));
                    item.setCityName(userdata.getString("cityname"));
                    item.setLatitude(userdata.getString("latitude"));
                    item.setLongitude(userdata.getString("longitude"));
                    mPopularList.add(item);

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getLatestItem() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.ALLPROPERTY, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);

                        getDataLatest(respo);
//                        addProperty.setVisibility(View.VISIBLE);
                        mScrollView.setVisibility(View.VISIBLE);
                        progresslatest.setVisibility(View.GONE);
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

    public void getDataLatest(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                mLatestList.clear();
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    PropertyModels item = new PropertyModels();
                    Double d = getDistance(userdata.getString("latitude"), userdata.getString("longitude"));
                    item.setPropid(userdata.getString("propid"));
                    item.setName(userdata.getString("name"));
                    item.setImage(userdata.getString("image"));
                    item.setAddress(userdata.getString("address"));
                    item.setPrice(userdata.getString("price"));
                    item.setRateAvg(userdata.getString("rate"));
                    item.setPurpose(userdata.getString("purpose"));
                    item.setBed(userdata.getString("bed"));
                    item.setBath(userdata.getString("bath"));
                    item.setArea(userdata.getString("area"));
                    item.setCityName(userdata.getString("cityname"));
                    item.setCityName(userdata.getString("cityname"));
                    item.setLatitude(String.valueOf(d));
                    item.setLongitude(String.valueOf(d));
//                    item.setLatitude(userdata.getString("latitude"));
//                    item.setLongitude(userdata.getString("longitude"));
                    item.setEmail(userdata.getString("email"));
                    item.setUserId(userdata.getString("owner_id"));
                    mLatestList.add(item);

                }
            }


            Collections.sort(mLatestList, new Comparator<PropertyModels>() {
                @Override
                public int compare(PropertyModels lhs, PropertyModels rhs) {
//                    return lhs.getLatitude().compareTo(rhs.getLatitude());
//
//                    return lhs.getId() > rhs.getId() ? -1 : (lhs.customInt < rhs.customInt ) ? 1 : 0;


                    if (Double.valueOf(lhs.getLatitude()) == Double.valueOf(rhs.getLatitude())) {
                        return 0;
                    }
                    else if (Double.valueOf(lhs.getLatitude()) < Double.valueOf(rhs.getLatitude())) {
                        return -1;
                    }
                    return 1;
                }

            });


        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private Double getDistance(String latitude, String longitude){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.pref_name, MODE_PRIVATE);

        Location startPoint = new Location("locationA");
        startPoint.setLatitude(sharedPreferences.getFloat(Constants.latitude, 0));
        startPoint.setLongitude(sharedPreferences.getFloat(Constants.longitude, 0));

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(Double.parseDouble(latitude));
        endPoint.setLongitude(Double.parseDouble(longitude));

        double distance = startPoint.distanceTo(endPoint);
        Log.e(">>>>", "onBindViewHolder: " + distance);
        distance = distance / 1000;

        return distance;
        //return new DecimalFormat("##.#").format(distance) + " km";
    }


    private void displayData() {
        sliderItem = new SliderItem(getActivity(), mSliderList);
        mViewPager.setAdapter(sliderItem);
        circleIndicator.setViewPager(mViewPager);
        swipe_refresh_latest.setRefreshing(false);
        latestItem = new GridItem(getActivity(), mLatestList, this, true);
        rvLatest.setAdapter(latestItem);

        if (mSliderList.isEmpty()) {
            rlslider.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (latestItem != null) {
                latestItem.updateAdapter();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkUtils.isConnected(getActivity())) {
            getFeaturedItem();
            getCategory();
            getPopularItem();
            getLatestItem();
            getCity();
        } else {
            nofound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        if (NetworkUtils.isConnected(getActivity())) {
//            getFeaturedItem();
//            getCategory();
//            getPopularItem();
            getLatestItem();
//            getCity();
        } else {
            nofound.setVisibility(View.VISIBLE);
        }
    }
}