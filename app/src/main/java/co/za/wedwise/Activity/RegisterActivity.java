package co.za.wedwise.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Fragment.EnableLlocationFragment;
import co.za.wedwise.R;
import co.za.wedwise.Utils.Prefs;

public class RegisterActivity extends AppCompatActivity {
    co.za.wedwise.Constants.BaseApp BaseApp;
    ImageView profileImage, backbtn;
    EditText fullName, lastname, email, phone, password;
    SharedPreferences sharedPreferences;

    DatabaseReference rootref;
    TextView back_login;

    Button submit;
    IOSDialog iosDialog;
    RelativeLayout editProfileImage;
    byte[] imageByteArray;
    String phone_no;
    private Prefs prefs;

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        BaseApp = co.za.wedwise.Constants.BaseApp.getInstance();
//        phone_no =getIntent().getExtras().getString("number");
//        phone_no = phone_no.replace("+","");
        prefs = new Prefs(RegisterActivity.this);
        sharedPreferences = getSharedPreferences(Constants.pref_name, Context.MODE_PRIVATE);

        rootref = FirebaseDatabase.getInstance().getReference();

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        profileImage = findViewById(R.id.profileimage);
        fullName = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        editProfileImage = findViewById(R.id.editphotoprofile);
        submit = findViewById(R.id.submit);
        backbtn = findViewById(R.id.back_btn);
        back_login = findViewById(R.id.back_login);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String f_name = fullName.getText().toString().trim();

                boolean isEmail = isEmailValid(email.getText().toString().trim());
                boolean isPassword = false;
                boolean isData = false;
                if(password.getText().toString().length()<8 &&!isValidPassword(password.getText().toString())){
                    isPassword = false;
                }else{
                    isPassword = true;
                }


               /* if (imageByteArray == null) {
                    Toast.makeText(RegisterActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                } else */
                if (TextUtils.isEmpty(f_name)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your First Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(lastname.getText().toString().trim())) {
                    Toast.makeText(RegisterActivity.this, "Please enter your last Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phone.getText().toString().trim())) {
                    Toast.makeText(RegisterActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                } else if (!isEmail){
                    Toast.makeText(BaseApp, "Email address is incorrect", Toast.LENGTH_LONG).show();
                } else if (!isPassword){
                    Toast.makeText(BaseApp, "Password is incorrect", Toast.LENGTH_LONG).show();
                } else {
                    isData = true;
                }
//                else if (TextUtils.isEmpty(email.getText().toString().trim())) {
//                    Toast.makeText(RegisterActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
//                } else if (!isValidEmail(email.getText().toString().trim())) {
//                    Toast.makeText(RegisterActivity.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
//                } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
//                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    //SaveData();
//                }
                if (isEmail && isPassword && isData){
                    SaveData();
                }


            }
        });
    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 2) {

                Uri selectedImage = data.getData();
                beginCrop(selectedImage);
            } else if (requestCode == 123) {
                handleCrop(resultCode, data);
            }
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this, 123);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri group_image_uri = Crop.getOutput(result);
            profileImage.setImageBitmap(null);
            profileImage.setImageURI(null);
            profileImage.setImageURI(group_image_uri);

            Bitmap bmpSample = BitmapFactory.decodeFile(group_image_uri.getPath());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmpSample.compress(Bitmap.CompressFormat.JPEG, 100, out);

            imageByteArray = out.toByteArray();

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void SaveData() {
        iosDialog.show();

        CallNewSignup(fullName.getText().toString().trim(), lastname.getText().toString().trim(), email.getText().toString().trim(),
                phone.getText().toString().trim(), password.getText().toString().trim());

        /*StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference filelocation = storageReference.child("User_image")
                .child(phone_no + ".jpg");
        filelocation.putBytes(imageByteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                CallSignup(phone_no,
                        fullName.getText().toString(),
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//                        taskSnapshot.getDownloadUrl().toString());
            }
        });*/
    }

    private void CallNewSignup(String fname, String lname, String email, String phone, String password) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("first_name", fname);
            parameters.put("last_name", lname);
            parameters.put("email", email);
            parameters.put("mobile", phone);
            parameters.put("password", password);
            parameters.put("fcm_token", sharedPreferences.getString(Constants.device_token, "null"));
            parameters.put("player_id", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(">>>", "CallNewSignup: " + parameters.toString());
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.REGISTER, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        iosDialog.cancel();
                        signupData(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Toast.makeText(RegisterActivity.this, "Something Wrong with Api", Toast.LENGTH_LONG).show();
                        Log.d("respo", error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    private void CallSignup(String user_id, String f_name, String picture) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", user_id);
            parameters.put("fullname", f_name);
            parameters.put("imageprofile", picture);
            parameters.put("fcm_token", sharedPreferences.getString(Constants.device_token, "null"));
            parameters.put("player_id", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.SIGNUP, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        iosDialog.cancel();
                        signupData(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Toast.makeText(RegisterActivity.this, "Something Wrong with Api", Toast.LENGTH_LONG).show();
                        Log.d("respo", error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void signupData(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray jsonArray = jsonObject.getJSONArray("msg");
                JSONObject userdata = jsonArray.getJSONObject(0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.uid, userdata.optString("user_id"));
                editor.putString(Constants.f_name, userdata.optString("first_name") + " " + userdata.optString("last_name"));
                editor.putString(Constants.u_pic, userdata.optString("imageprofile"));
                BaseApp.saveIsLogin(true);
                prefs.setPrefBoolean("login", true);
                editor.commit();

                enableLocation();
            } else {
                Toast.makeText(this, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }
    }

    private void enableLocation() {
        EnableLlocationFragment enable_llocationFragment = new EnableLlocationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStackImmediate();
        transaction.replace(R.id.register, enable_llocationFragment).addToBackStack(null).commit();
    }

}
