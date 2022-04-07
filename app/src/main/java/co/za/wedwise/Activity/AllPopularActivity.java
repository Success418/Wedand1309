package co.za.wedwise.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Item.GridItem;
import co.za.wedwise.Models.CategoryModels;
import co.za.wedwise.Models.CityModels;
import co.za.wedwise.Models.PropertyModels;
import co.za.wedwise.R;
//import co.za.wedwise.Utils.BannerAds;
import co.za.wedwise.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class AllPopularActivity extends AppCompatActivity {

    ArrayList<PropertyModels> listItem;
    public RecyclerView recyclerView;
    GridItem adapter;
    String purpose, bedfilter, bathfilter, valuemin, valuemax, sortdata, valuecity, valuecat;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    ArrayList<String> catNameList, cityNameList;
    ArrayList<CategoryModels> mListCat;
    ArrayList<CityModels> mListCity;
    ImageView search, backbtn;
    LinearLayout llfilter, llsort, noresult;
    RelativeLayout notFound, progress;
    CardView filterandsort;
    int save_sort=1;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycle);
        LinearLayout mAdViewLayout = findViewById(R.id.adView);
//        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        listItem = new ArrayList<>();
        mListCat= new ArrayList<>();
        cityNameList= new ArrayList<>();
        catNameList= new ArrayList<>();
        mListCity= new ArrayList<>();
        recyclerView = findViewById(R.id.recycle);
        filterandsort = findViewById(R.id.rlfilter);
        notFound = findViewById(R.id.notfound);
        progress = findViewById(R.id.progress);
        backbtn = findViewById(R.id.back_btn);
        search = findViewById(R.id.search);
        llfilter = findViewById(R.id.llfilter);
        llsort = findViewById(R.id.llsort);
        noresult = findViewById(R.id.noresult);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progress.setVisibility(View.VISIBLE);
        llsort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort();
            }
        });
        llfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFilter();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        getData();
        getCategory();
        getCity();
    }

    private void sort() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View mDialog = getLayoutInflater().inflate(R.layout.sheet_list, null);
        RadioGroup radioGroupSort = mDialog.findViewById(R.id.myRadioGroup);
        RadioButton filter_dis = mDialog.findViewById(R.id.sort_distance);
        RadioButton filter_low = mDialog.findViewById(R.id.sort_law);
        RadioButton filter_high = mDialog.findViewById(R.id.sort_high);
        RadioButton filter_all=mDialog.findViewById(R.id.sort_all);

        if(save_sort==1) {
            filter_all.setChecked(true);
        }else if(save_sort==2){
            filter_high.setChecked(true);
        }else if(save_sort==3){
            filter_low.setChecked(true);
        }
        else if(save_sort==4){
            filter_dis.setChecked(true);
        }
        sortdata = "DESC";
        radioGroupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int Check) {
                listItem.clear();
                if (Check == R.id.sort_distance) {
                    save_sort=4;
                    if (NetworkUtils.isConnected(context)) {
                        JSONObject parameters = new JSONObject();
                        String lat= DrawerActivity.sharedPreferences.getString(Constants.Lat,"33.738045");
                        String lon= DrawerActivity.sharedPreferences.getString(Constants.Lon,"73.084488");
                        RequestQueue rq = Volley.newRequestQueue(context);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, Constants.DISTANCE+lat+"&user_long="+lon, parameters, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String respo=response.toString();
                                        Log.d("responce",respo);
                                        getDataAll(respo);
                                        progress.setVisibility(View.GONE);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error
                                        Log.d("respo",error.toString());
                                    }
                                });
                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        rq.getCache().clear();
                        rq.add(jsonObjectRequest);
                    }
                } else if (Check == R.id.sort_high) {
                    save_sort=2;
                    if (NetworkUtils.isConnected(context)) {
                        JSONObject parameters = new JSONObject();
                        RequestQueue rq = Volley.newRequestQueue(context);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, Constants.FILTERPRICE+"DESC", parameters, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String respo=response.toString();
                                        Log.d("responce",respo);
                                        getDataAll(respo);
                                        progress.setVisibility(View.GONE);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error
                                        Log.d("respo",error.toString());
                                    }
                                });
                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        rq.getCache().clear();
                        rq.add(jsonObjectRequest);
                    }
                } else if (Check == R.id.sort_law) {
                    save_sort=3;
                    if (NetworkUtils.isConnected(context)) {
                        JSONObject parameters = new JSONObject();
                        RequestQueue rq = Volley.newRequestQueue(context);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, Constants.FILTERPRICE+"ASC", parameters, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String respo=response.toString();
                                        Log.d("responce",respo);
                                        getDataAll(respo);
                                        progress.setVisibility(View.GONE);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error
                                        Log.d("respo",error.toString());
                                    }
                                });
                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        rq.getCache().clear();
                        rq.add(jsonObjectRequest);
                    }
                }
                else if (Check == R.id.sort_all) {
                    save_sort=1;
                    sortdata = ("Sort All");
                    if (NetworkUtils.isConnected(context)) {
                        getData();
                    }}

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

    private void searchFilter() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_filter);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final RangeSeekBar seekbar = dialog.findViewById(R.id.seekbar);
        final TextView min = dialog.findViewById(R.id.textmin);
        final TextView max = dialog.findViewById(R.id.textmax);
        final Button submit = dialog.findViewById(R.id.submit);
        final ImageView close = dialog.findViewById(R.id.bt_close);
        final Spinner category = dialog.findViewById(R.id.category);
        final Spinner city = dialog.findViewById(R.id.city);

        ArrayAdapter<String> citySpinner = new ArrayAdapter<>(context, R.layout.spinner, cityNameList);
        citySpinner.setDropDownViewResource(R.layout.spinner);
        city.setAdapter(citySpinner);
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    valuecity = mListCity.get(city.getSelectedItemPosition()).getCityId();
                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    valuecity = mListCity.get(city.getSelectedItemPosition()).getCityId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        ArrayAdapter<String> catSpinner = new ArrayAdapter<>(context, R.layout.spinner, catNameList);
        catSpinner.setDropDownViewResource(R.layout.spinner);
        category.setAdapter(catSpinner);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    valuecat = mListCat.get(category.getSelectedItemPosition()).getCategoryId();

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    valuecat = mListCat.get(category.getSelectedItemPosition()).getCategoryId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        final Button btn_buy = dialog.findViewById(R.id.bt_buy);
        final Button btn_rent = dialog.findViewById(R.id.bt_rent);
        btn_buy.setSelected(true);
        purpose = "Sale";
        valuemin = "0";
        valuemax = "10000000";
        bathfilter = "";
        bedfilter = "";

        final Button allbed = dialog.findViewById(R.id.allbed);
        final Button bed1 = dialog.findViewById(R.id.bed1);
        final Button bed2 = dialog.findViewById(R.id.bed2);
        final Button bed3 = dialog.findViewById(R.id.bed3);
        final Button bed4 = dialog.findViewById(R.id.bed4);
        final Button bed5 = dialog.findViewById(R.id.bed5);
        allbed.setSelected(true);

        final Button allbath = dialog.findViewById(R.id.allbath);
        final Button bath1 = dialog.findViewById(R.id.bath1);
        final Button bath2 = dialog.findViewById(R.id.bath2);
        final Button bath3 = dialog.findViewById(R.id.bath3);
        final Button bath4 = dialog.findViewById(R.id.bath4);
        final Button bath5 = dialog.findViewById(R.id.bath5);
        allbath.setSelected(true);

        min.setText("$"+"0");
        max.setText("$"+"10000000");
        seekbar.setValue(0,10000000);

        seekbar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                DecimalFormat df = new DecimalFormat("0");
                min.setText("$"+df.format(leftValue));
                max.setText("$"+df.format(rightValue));
                valuemin = String.valueOf(leftValue);
                valuemax =String.valueOf(rightValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //do what you want!!
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //do what you want!!
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purpose = "Sale";
                btn_buy.setSelected(true);
                btn_rent.setSelected(false);
            }
        });

        btn_rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purpose = "Rent";
                btn_rent.setSelected(true);
                btn_buy.setSelected(false);
            }
        });

        allbed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allbed.setSelected(true);
                bed1.setSelected(false);
                bed2.setSelected(false);
                bed3.setSelected(false);
                bed4.setSelected(false);
                bed5.setSelected(false);
            }
        });

        bed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bedfilter = "1";
                allbed.setSelected(false);
                bed1.setSelected(true);
                bed2.setSelected(false);
                bed3.setSelected(false);
                bed4.setSelected(false);
                bed5.setSelected(false);
            }
        });

        bed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bedfilter = "2";
                allbed.setSelected(false);
                bed1.setSelected(false);
                bed2.setSelected(true);
                bed3.setSelected(false);
                bed4.setSelected(false);
                bed5.setSelected(false);
            }
        });

        bed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bedfilter = "3";
                allbed.setSelected(false);
                bed1.setSelected(false);
                bed2.setSelected(false);
                bed3.setSelected(true);
                bed4.setSelected(false);
                bed5.setSelected(false);
            }
        });

        bed4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bedfilter = "4";
                allbed.setSelected(false);
                bed1.setSelected(false);
                bed2.setSelected(false);
                bed3.setSelected(false);
                bed4.setSelected(true);
                bed5.setSelected(false);
            }
        });

        bed5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bedfilter = "5";
                allbed.setSelected(false);
                bed1.setSelected(false);
                bed2.setSelected(false);
                bed3.setSelected(false);
                bed4.setSelected(false);
                bed5.setSelected(true);
            }
        });

        allbath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allbath.setSelected(true);
                bath1.setSelected(false);
                bath2.setSelected(false);
                bath3.setSelected(false);
                bath4.setSelected(false);
                bath5.setSelected(false);
            }
        });

        bath1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   bathfilter = "1";
                allbath.setSelected(false);
                bath1.setSelected(true);
                bath2.setSelected(false);
                bath3.setSelected(false);
                bath4.setSelected(false);
                bath5.setSelected(false);
            }
        });

        bath2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bathfilter = "2";
                allbath.setSelected(false);
                bath1.setSelected(false);
                bath2.setSelected(true);
                bath3.setSelected(false);
                bath4.setSelected(false);
                bath5.setSelected(false);
            }
        });

        bath3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bathfilter = "3";
                allbath.setSelected(false);
                bath1.setSelected(false);
                bath2.setSelected(false);
                bath3.setSelected(true);
                bath4.setSelected(false);
                bath5.setSelected(false);
            }
        });

        bath4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bathfilter = "4";
                allbath.setSelected(false);
                bath1.setSelected(false);
                bath2.setSelected(false);
                bath3.setSelected(false);
                bath4.setSelected(true);
                bath5.setSelected(false);
            }
        });

        bath5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bathfilter = "5";
                allbath.setSelected(false);
                bath1.setSelected(false);
                bath2.setSelected(false);
                bath3.setSelected(false);
                bath4.setSelected(false);
                bath5.setSelected(true);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilterSearchActivity.class);
                intent.putExtra("purpose", purpose);
                intent.putExtra("pricemin", valuemin);
                intent.putExtra("pricemax", valuemax);
                intent.putExtra("bedfilter", bedfilter);
                intent.putExtra("bathfilter", bathfilter);
                intent.putExtra("cid", valuecat);
                intent.putExtra("cityid", valuecity);
                startActivity(intent);
                dialog.dismiss();


            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void getData() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.ALLPOPULAR, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataAll(respo);
                        progress.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataAll(String loginData){

        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
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
                    listItem.add(item);

                }
                if(listItem.isEmpty()) {
                    noresult.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        adapter = new GridItem(this, listItem);
        recyclerView.setAdapter(adapter);
    }

    private void getCategory() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CATEGORY, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataCategory(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataCategory(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    CategoryModels item = new CategoryModels();
                    item.setCategoryId(userdata.getString("cid"));
                    catNameList.add(userdata.getString("cname"));
                    item.setCategoryImage(userdata.getString("cimage"));
                    mListCat.add(item);

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    private void getCity() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CITY, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        getDataCity(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataCity(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    CityModels item = new CityModels();
                    item.setCityId(userdata.getString("cityid"));
                    cityNameList.add(userdata.getString("cityname"));
                    item.setCityImage(userdata.getString("cityimage"));
                    mListCity.add(item);

                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }
}
