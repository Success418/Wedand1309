package co.za.wedwise.Fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import co.za.wedwise.Item.ItemProfile;
import co.za.wedwise.Constants.Functions;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Activity.DrawerActivity;
import co.za.wedwise.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;
import com.wonshinhyo.dragrecyclerview.SimpleDragListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment {

    View getView;
    Context context;

    DragRecyclerView listPhoto;
    ImageView backBtn;
    IOSDialog iosDialog;
    EditText fullName;

    byte[] imageByteArray;

    ItemProfile profilePhotosAdapter;
    ArrayList<String> imagesList;

    Button submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_editprofile, container, false);
        context=getContext();


        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(true)
                .setMessageContentGravity(Gravity.END)
                .build();

        fullName = getView.findViewById(R.id.firstname);

        backBtn = getView.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            }
        });

        submit = getView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imagesList.isEmpty()){
                    Toast.makeText(getActivity(), "Select Image", Toast.LENGTH_SHORT).show();
                }else
                    Edit();
            }
        });

        imagesList =new ArrayList<>();


        listPhoto = getView.findViewById(R.id.listphoto);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
        listPhoto.setLayoutManager(layoutManager);
        listPhoto.setHasFixedSize(false);

        profilePhotosAdapter =new ItemProfile(context, imagesList, new ItemProfile.OnItemClickListener() {
            @Override
            public void onItemClick(String item,int postion, View view) {
                if(view.getId()==R.id.button){
                    if(item.equals("")){
                        selectImage();
                    }else {
                        deletelink(item);
                        profilePhotosAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        profilePhotosAdapter.setOnItemDragListener(new SimpleDragListener() {

            @Override
            public void onDrop(int fromPosition, int toPosition) {
                super.onDrop(fromPosition, toPosition);
            }

            @Override
            public void onSwiped(int pos) {
                super.onSwiped(pos);
                Log.d("drag", "onSwiped " + pos);
            }
        });
        listPhoto.setAdapter(profilePhotosAdapter);
        getUser();

        return getView;
    }

    private void selectImage() {
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.Select_image_from_gallry_code);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult","-> "+requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.Select_image_from_gallry_code) {

                Uri selectedImage = data.getData();

                Log.e("selectedImage","-> "+selectedImage.toString());
                beginCrop(selectedImage);
            }
            else if (requestCode == 123) {
                handleCrop(resultCode, data);
            }

        }
    }



    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getContext().getCacheDir(), "cropped"));
        Log.e("destination","->"+destination.toString());
//        Crop.of(source, destination).asSquare().start(getActivity(),getCurrentFragment(),123);
        Crop.of(source, destination).asSquare().start(getActivity(),123);

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri group_image_uri=Crop.getOutput(result);
            Bitmap bmpSample = BitmapFactory.decodeFile(group_image_uri.getPath());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmpSample.compress(Bitmap.CompressFormat.JPEG, 100, out);
            imageByteArray = out.toByteArray();

            SavePicture();

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Fragment getCurrentFragment(){
        return getActivity().getSupportFragmentManager().findFragmentById(R.id.editprofile);

    }

    public void SavePicture(){
        iosDialog.show();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        String id=reference.push().getKey();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference filelocation = storageReference.child(DrawerActivity.user_id)
                .child(id+".jpg");
        filelocation.putBytes(imageByteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                String url=taskSnapshot.getDownloadUrl().toString();
                uploadLink(url);
                iosDialog.cancel();

            }});
    }

    private void uploadLink(String link) {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", DrawerActivity.user_id);
            parameters.put("image_link",link);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.UPLOADIMAGES, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        iosDialog.cancel();

                        try {
                            JSONObject jsonObject=new JSONObject(respo);
                            String code=jsonObject.optString("code");
                            if(code.equals("200")){
                                getUser();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    private void deletelink(String link) {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", DrawerActivity.user_id);
            parameters.put("image_link",link);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.DELETEIMAGES, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        iosDialog.cancel();
                        try {
                            JSONObject jsonObject=new JSONObject(respo);
                            String code=jsonObject.optString("code");
                            if(code.equals("200")){
                                getUser();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    private void getUser() {
        iosDialog.show();
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
                        String respo=response.toString();
                        Log.d("responce",respo);
                        parseUser(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void parseUser(String loginData){
        iosDialog.cancel();
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msg=jsonObject.getJSONArray("msg");
                JSONObject userdata=msg.getJSONObject(0);


                imagesList.clear();
                imagesList.add(userdata.optString("imageprofile"));
                fullName.setText(userdata.optString("fullname"));
                profilePhotosAdapter.notifyDataSetChanged();



            }
        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }

    private void Edit() {

        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", DrawerActivity.user_id);
            parameters.put("fullname", fullName.getText().toString());



        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.EDITPROFILE, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        editData(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void editData(String loginData){
        iosDialog.cancel();
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){

                JSONArray msg=jsonObject.getJSONArray("msg");
                JSONObject userdata=msg.getJSONObject(0);

                // if data is save then we will go back
                getActivity().onBackPressed();

            }
        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }




}
