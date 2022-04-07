package co.za.wedwise.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.zanjou.http.debug.Logger;
import com.zanjou.http.request.FileUploadListener;
import com.zanjou.http.request.RequestStateListener;
import com.zanjou.http.request.Requesthttp;
import com.zanjou.http.response.JsonResponseListener;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Models.CategoryModels;
import co.za.wedwise.Models.CityModels;
import co.za.wedwise.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class AddPropertyActivity extends AppCompatActivity {
    EditText name, phone, bed, bath, area, amenities, description, price;
    private LatLng destinationLocation;
    TextView address;
    LinearLayout lladdress;
    Button submit;
    ArrayList<String> propertyPurpose, catNameList, cityNameList;
    ArrayList<CategoryModels> categoriList;
    ArrayList<CityModels> cityList;
    boolean isimage = false;
    String[] spinnertype;
    String message;
    boolean isfloorplan = false;
    boolean isgallery = false;
    private ArrayList<Image> imageset, floorplanset, galleryset = new ArrayList<>();
    private final int DESTINATION_ID = 1;
    ImageView image,gallery, floorplan, backbtn;
    Spinner city, category, type;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        dialog = new ProgressDialog(AddPropertyActivity.this);
        propertyPurpose = new ArrayList<>();
        categoriList = new ArrayList<>();
        catNameList = new ArrayList<>();
        cityNameList = new ArrayList<>();
        cityList = new ArrayList<>();
        spinnertype = getResources().getStringArray(R.array.purpose);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        bed = findViewById(R.id.bed);
        bath = findViewById(R.id.bath);
        area = findViewById(R.id.area);
        price = findViewById(R.id.price);
        amenities = findViewById(R.id.amenities);
        description = findViewById(R.id.description);
        image = findViewById(R.id.image);
        gallery = findViewById(R.id.gallery);
        floorplan = findViewById(R.id.floorplan);
        submit = findViewById(R.id.submit);
        city = findViewById(R.id.city);
        category = findViewById(R.id.category);
        type = findViewById(R.id.purpose);
        lladdress = findViewById(R.id.lladdress);
        backbtn = findViewById(R.id.back_btn);

        lladdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lladdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPropertyActivity.this, PicklocationActivity.class);
                intent.putExtra(PicklocationActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
                startActivityForResult(intent, PicklocationActivity.LOCATION_PICKER_ID);
            }
        });

        ArrayAdapter<String> typeSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, spinnertype);
        typeSpinner.setDropDownViewResource(R.layout.spinner);
        type.setAdapter(typeSpinner);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getname = name.getText().toString();
                String getphone = phone.getText().toString();
                String getaddress = address.getText().toString();
                String getbed = bed.getText().toString();
                String getbath = bath.getText().toString();
                String getarea = area.getText().toString();
                String getamenities = amenities.getText().toString();
                String getprice = price.getText().toString();
                String getdescription = description.getText().toString();
                if(TextUtils.isEmpty(getname)){
                    Toast.makeText(AddPropertyActivity.this, "Please Enter Property Name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getphone)){
                    Toast.makeText(AddPropertyActivity.this, "Please Enter Phone", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getaddress)){
                    Toast.makeText(AddPropertyActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getbed)){
                    Toast.makeText(AddPropertyActivity.this, "Please Enter Bed", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getbath)){
                    Toast.makeText(AddPropertyActivity.this, "Please Enter Bath", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getarea)){
                    Toast.makeText(AddPropertyActivity.this, "Please Enter Area", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getamenities)){
                    Toast.makeText(AddPropertyActivity.this, "Please Enter Amenities", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getprice)){
                    Toast.makeText(AddPropertyActivity.this, "Please Enter Price", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(getdescription)){
                    Toast.makeText(AddPropertyActivity.this, "Please Enter Description", Toast.LENGTH_SHORT).show();
                }
                else if(imageset ==null){
                    Toast.makeText(AddPropertyActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                }
                else if(floorplanset ==null){
                    Toast.makeText(AddPropertyActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadData();
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        floorplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floorPlan();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosegallery();
            }
        });
        getCategory();
        getCity();
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

    public void getDataCategory(String loginData) {
        if (null == loginData || loginData.length() == 0) {
        } else {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        CategoryModels item = new CategoryModels();
                        item.setCategoryId(userdata.getString("cid"));
                        catNameList.add(userdata.getString("cname"));
                        item.setCategoryImage(userdata.getString("cimage"));
                        categoriList.add(item);

                        ArrayAdapter<String> catSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, catNameList);
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

                                } else {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // TODO Auto-generated method stub

                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
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
                    cityList.add(item);

                    ArrayAdapter<String> citySpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, cityNameList);
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

                            } else {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    public void chooseImage() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("add picture")
                .imageTitle("Tap to select")
                .single()
                .limit(1)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(201);
    }

    public void floorPlan() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("add picture")
                .imageTitle("Tap to select")
                .single()
                .limit(1)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(202);
    }

    public void choosegallery() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("add picture")
                .imageTitle("Tap to select")
                .multi()
                .limit(5)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(203);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PicklocationActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                String addressset = data.getStringExtra(PicklocationActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(PicklocationActivity.LOCATION_LATLNG);
                address.setText(addressset);
                destinationLocation = latLng;
            }
        }
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                imageset = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(imageset.get(0).getPath()));
                Picasso.with(this).load(uri).into(image);
                isimage = true;

            }
        } else if (requestCode == 202) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                floorplanset = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(floorplanset.get(0).getPath()));
                Picasso.with(this).load(uri).into(floorplan);
                isfloorplan = true;

            }
        } else if (requestCode == 203) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                galleryset = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(galleryset.get(0).getPath()));
                Picasso.with(this).load(uri).into(gallery);
                isgallery = true;
            }
        }

    }

    public void uploadData() {
        Requesthttp request = Requesthttp.create(Constants.ADDPROPERTY);
        request.setMethod("POST")
                .setTimeout(0)
                .setLogger(new Logger(Logger.ERROR))
                .addParameter("userid", DrawerActivity.user_id)
                .addParameter("cid", categoriList.get(category.getSelectedItemPosition()).getCategoryId())
                .addParameter("cityid", cityList.get(city.getSelectedItemPosition()).getCityId())
                .addParameter("purpose", String.valueOf(type.getSelectedItem()))
                .addParameter("name", name.getText().toString())
                .addParameter("description", description.getText().toString())
                .addParameter("bed", bed.getText().toString())
                .addParameter("bath", bath.getText().toString())
                .addParameter("area", area.getText().toString())
                .addParameter("amenities", amenities.getText().toString())
                .addParameter("price", price.getText().toString())
                .addParameter("phone", phone.getText().toString())
                .addParameter("address", address.getText().toString())
                .addParameter("latitude", destinationLocation.latitude)
                .addParameter("longitude", destinationLocation.longitude);

        if (isimage) {
            request.addParameter("image", new File(imageset.get(0).getPath()));
        }

        if (isfloorplan) {
            request.addParameter("floorplan", new File(floorplanset.get(0).getPath()));
        }

        if (isgallery) {
            request.addParameter("galleryimage[]", galleryset);
        }

        request.setFileUploadListener(new FileUploadListener() {
            @Override
            public void onUploadingFile(File file, long size, long uploaded) {

            }
        })
                .setRequestStateListener(new RequestStateListener() {
                    @Override
                    public void onStart() {
                        dialog.setMessage("Please wait...");
                        dialog.setIndeterminate(false);
                        dialog.setCancelable(true);
                        dialog.show();
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onConnectionError(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setResponseListener(new JsonResponseListener() {
                    @Override
                    public void onOkResponse(JSONObject jsonObject) throws JSONException {
                        JSONArray jsonArray = jsonObject.getJSONArray("200");
                        JSONObject objJson = jsonArray.getJSONObject(0);
                        Constants.successmsg = objJson.getInt("success");
                        message = objJson.getString("msg");
                        if (Constants.successmsg == 0) {

                            Toast.makeText(AddPropertyActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddPropertyActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPropertyActivity.this, DrawerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(JSONObject jsonObject) {

                    }

                    @Override
                    public void onParseError(JSONException e) {

                    }
                }).execute();
    }

}
