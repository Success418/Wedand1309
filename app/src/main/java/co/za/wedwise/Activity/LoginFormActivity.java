package co.za.wedwise.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.ViewFlipper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.za.wedwise.Constants.Constants;
import co.za.wedwise.R;
import co.za.wedwise.Utils.Prefs;

public class LoginFormActivity extends AppCompatActivity {
    co.za.wedwise.Constants.BaseApp BaseApp;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth fbAuth;
    EditText phoneText, numOne, numTwo, numThree, numFour, numFive, numSix;
    TextView countryCode, sendTo;
    Button buttonLogin, buttonfb, buttongmail, confirmButton;
    ImageView backButton, backButtonverify;
    ViewFlipper viewFlipper;
    String phoneNumber;
    RelativeLayout number_layout;
    private CallbackManager mCallbackManager;
    public IOSDialog iosDialog;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    SharedPreferences sharedPreferences;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        fbAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        prefs = new Prefs(LoginFormActivity.this);

        BaseApp = co.za.wedwise.Constants.BaseApp.getInstance();
        if (firebaseUser != null) {
            firebaseUser.getIdToken(true).toString();
        }

        sharedPreferences = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);

//        phoneText= findViewById(R.id.phonenumber);
//
//        countryCode = findViewById(R.id.countrycode);
//        countryCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Showcountry();
//            }
//        });


        buttongmail = findViewById(R.id.buttongmail);
        buttongmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginwithgmail();
            }
        });

        buttonfb = findViewById(R.id.buttonfb);
        buttonfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFacebook(viewFlipper);
            }
        });

//        number_layout = findViewById(R.id.number_layout);
//        number_layout.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        backButtonverify = findViewById(R.id.back_btn_verify);
//        backButtonverify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

//        confirmButton = findViewById(R.id.buttonconfirm);
//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                verifyCode(viewFlipper);
//            }
//        });

        sendTo = findViewById(R.id.sendtotxt);

        viewFlipper = findViewById(R.id.viewflipper);

        codenumber();

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();
        TextView text_register = findViewById(R.id.text_register);
        /*SpannableString ss = new SpannableString("Don't have an account? Register Now");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(LoginFormActivity.this, RegisterActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 23, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        text_register.setText(ss);
        text_register.setMovementMethod(LinkMovementMethod.getInstance());
        text_register.setHighlightColor(Color.TRANSPARENT);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            text_register.setTextAppearance(R.style.NavigationDrawerStyle);
//        }*/

        text_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginFormActivity.this, RegisterActivity.class));
            }
        });

        final EditText login_username = findViewById(R.id.login_username);
        final EditText login_password = findViewById(R.id.login_password);

        buttonLogin = findViewById(R.id.buttonlogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isEmail = isEmailValid(login_username.getText().toString().trim());
                boolean isPassword = false;
                if(login_password.getText().toString().length()<8 &&!isValidPassword(login_password.getText().toString())){
                    isPassword = false;
                }else{
                    isPassword = true;
                }
                if (!isEmail){
                    Toast.makeText(BaseApp, "Email address is incorrect", Toast.LENGTH_LONG).show();
                }
                if (!isPassword){
                    Toast.makeText(BaseApp, "Password is incorrect", Toast.LENGTH_LONG).show();
                }
                if (isEmail && isPassword) {
                    iosDialog.show();
                    Calllogin(login_username.getText().toString().trim(), login_password.getText().toString().trim());
                }
//                //Nextbtn(viewFlipper);
//                if (TextUtils.isEmpty(login_username.getText().toString().trim())) {
//                    Toast.makeText(LoginFormActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
//                } else if (!isValidEmail(login_username.getText().toString().trim())) {
//                    Toast.makeText(LoginFormActivity.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
//                } else if (TextUtils.isEmpty(login_password.getText().toString().trim())) {
//                    Toast.makeText(LoginFormActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
//                } else {
//                    iosDialog.show();
//                    Calllogin(login_username.getText().toString().trim(), login_password.getText().toString().trim());
//                }
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

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void codenumber() {
        numOne = findViewById(R.id.numone);
        numTwo = findViewById(R.id.numtwo);
        numThree = findViewById(R.id.numthree);
        numFour = findViewById(R.id.numfour);
        numFive = findViewById(R.id.numfive);
        numSix = findViewById(R.id.numsix);

        numOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (numOne.getText().toString().length() == 0) {
                    numTwo.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (numTwo.getText().toString().length() == 0) {
                    numThree.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (numThree.getText().toString().length() == 0) {
                    numFour.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (numFour.getText().toString().length() == 0) {
                    numFive.requestFocus();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (numFive.getText().toString().length() == 0) {
                    numSix.requestFocus();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    String country_iso_code = "EN";
//    public void Showcountry(){
//        final CountryPicker picker = CountryPicker.newInstance("Select Country");
//        picker.setListener(new CountryPickerListener() {
//            @Override
//            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
//                // Implement your code here
//                countryCode.setText(dialCode);
//                picker.dismiss();
//                country_iso_code=code;
//            }
//        });
//        picker.setStyle(R.style.countrypicker_style,R.style.countrypicker_style);
//        picker.show(getSupportFragmentManager(), "Select Country");
//    }

//    public void Nextbtn(View view) {
//        phoneNumber= countryCode.getText().toString()+phoneText.getText().toString();
//        String ccode= countryCode.getText().toString();
//
//        if((!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(ccode)) && phoneNumber.length()>11){
//            iosDialog.show();
//            Send_Number_tofirebase(phoneNumber);
//
//        }else {
//            Toast.makeText(this, "Enter Correct number", Toast.LENGTH_SHORT).show();
//        }
//    }


//    public void Send_Number_tofirebase(String phoneNumber){
//        setUpVerificatonCallbacks();
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                verificationCallbacks);
//    }

    private void setUpVerificatonCallbacks() {
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {

//                            signInWithPhoneAuthCredential(credential);

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        iosDialog.cancel();
                        Log.d("responce", e.toString());
                        Toast.makeText(LoginFormActivity.this, "Enter Correct Number.", Toast.LENGTH_SHORT).show();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;
                        sendTo.setText("Send to ( " + phoneNumber + " )");
                        iosDialog.cancel();
                        viewFlipper.setInAnimation(LoginFormActivity.this, R.anim.in_from_right);
                        viewFlipper.setOutAnimation(LoginFormActivity.this, R.anim.out_to_left);
                        viewFlipper.setDisplayedChild(1);
                    }
                };
    }


//    public void verifyCode(View view) {
//    String code=""+ numOne.getText().toString()+ numTwo.getText().toString()+ numThree.getText().toString()+ numFour.getText().toString()+ numFive.getText().toString()+ numSix.getText().toString();
//    if(!code.equals("")){
//        iosDialog.show();
//    PhoneAuthCredential credential =
//                PhoneAuthProvider.getCredential(phoneVerificationId, code);
//        signInWithPhoneAuthCredential(credential);
//    }else {
//        Toast.makeText(this, "Enter the Correct varification Code", Toast.LENGTH_SHORT).show();
//    }
//
//    }


//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        fbAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                            // get the user info to know that user is already login or not
//                            Getuser();
//
//                        } else {
//                            if (task.getException() instanceof
//                                    FirebaseAuthInvalidCredentialsException) {
//                                iosDialog.cancel();
//                            }
//                        }
//                    }
//                });
//    }
//
//
//    public void resendCode(View view) {
//
//       setUpVerificatonCallbacks();
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,
//                60,
//                TimeUnit.SECONDS,
//                this,
//                verificationCallbacks,
//                resendToken);
//    }


    private void Getuser() {
        iosDialog.show();
        final String phone_no = phoneNumber.replace("+", "");
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", phone_no);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.USERDATA, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);

                        iosDialog.cancel();
                        try {
                            JSONObject jsonObject = new JSONObject(respo);
                            String code = jsonObject.optString("code");
                            if (code.equals("200")) {

                                // if user is already logedin then we will save the user data and go to the enable location screen
                                JSONArray msg = jsonObject.getJSONArray("msg");
                                JSONObject userdata = msg.getJSONObject(0);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.uid, phone_no);
                                editor.putString(Constants.f_name, userdata.optString("fullname"));
                                editor.putString(Constants.u_pic, userdata.optString("imageprofile"));
                                BaseApp.saveIsLogin(true);
                                prefs.setPrefBoolean("login", true);
                                Intent intent = new Intent(LoginFormActivity.this, DrawerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                editor.commit();

                                // after all things done we will move the user to enable location screen
                                enable_location();


                            } else {
                                // if user is first time login then we will get the usser picture and name
                                Intent intent = new Intent(LoginFormActivity.this, RegisterActivity.class);
                                intent.putExtra("number", phone_no);
                                startActivity(intent);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();

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
                        iosDialog.cancel();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.getCache().clear();

        rq.add(jsonObjectRequest);
    }

    GoogleSignInClient mGoogleSignInClient;

    public void loginwithgmail() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginFormActivity.this);
        if (account != null) {
            String id = account.getId();
            String email = account.getEmail();
            String f_name = account.getGivenName() + " " + account.getFamilyName();
            String pic_url = Constants.BASEURL + "/asset/images/users.png";
            if (account.getPhotoUrl() != null) {
                pic_url = account.getPhotoUrl().toString();
            }
            ApiForSignup(id, f_name, pic_url, email);

        } else {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 123);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {

                String id = account.getId();
                String f_name = account.getGivenName() + account.getFamilyName();
                String email = account.getEmail();


                String pic_url = Constants.BASEURL + "/asset/images/users.png";
                if (account.getPhotoUrl() != null) {
                    pic_url = account.getPhotoUrl().toString();
                }

                ApiForSignup(id, f_name, pic_url, email);

            }
        } catch (ApiException e) {
            Log.w("Error message", "signInResult:failed code=" + e.getStatusCode());
        }

    }

    private void ApiForSignup(String user_id, String f_name, String picture, String email) {

        iosDialog.show();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("userid", user_id);
            parameters.put("fullname", f_name);
            parameters.put("imageprofile", picture);
            parameters.put("email", email);
            parameters.put("fcm_token", sharedPreferences.getString(Constants.device_token, "null"));
            parameters.put("player_id", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp", parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest

                (Request.Method.POST, Constants.SIGNUP, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        iosDialog.cancel();
                        signupdata(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Toast.makeText(LoginFormActivity.this, "Something wrong with Api", Toast.LENGTH_SHORT).show();
                        Log.d("respo", error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void signupdata(String loginData) {

        try {

            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray jsonArray = jsonObject.getJSONArray("msg");
                JSONObject userdata = jsonArray.getJSONObject(0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.uid, userdata.optString("userid"));
                editor.putString(Constants.f_name, userdata.optString("fullname"));
                editor.putString(Constants.u_pic, userdata.optString("imageprofile"));
                BaseApp.saveIsLogin(true);
                prefs.setPrefBoolean("login", true);
                Intent intent = new Intent(LoginFormActivity.this, DrawerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                editor.commit();

                // after all things done we will move the user to enable location screen
                enable_location();
            } else {
                Toast.makeText(this, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }

    public void LoginFacebook(View view) {

        LoginManager.getInstance().logInWithReadPermissions(LoginFormActivity.this,
                Arrays.asList("public_profile", "email"));

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginFormActivity.this, "Login Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("resp", "" + error.toString());
                Toast.makeText(LoginFormActivity.this, "Login Error" + error.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (mCallbackManager != null)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleFacebookAccessToken(final AccessToken token) {
        // if user is login then this method will call and
        // facebook will return us a token which will user for get the info of user
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            iosDialog.show();
                            final FirebaseUser user_firebase = mAuth.getCurrentUser();
                            final String id = Profile.getCurrentProfile().getId();
                            GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                    Log.d("resp", user.toString());
                                    ApiForSignup("" + id, "" + user.optString("first_name") + " " + user.optString("last_name")
                                            , "https://graph.facebook.com/" + id + "/picture?width=500&width=500", "");
                                }
                            });

                            // here is the request to facebook sdk for which type of info we have required
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "last_name,first_name,email,birthday,gender");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {
                            Toast.makeText(LoginFormActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void enable_location() {
        Intent intent = new Intent(this, DrawerActivity.class);
        startActivity(intent);
    }


    private void Calllogin(String email, String password) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("email", email);
            parameters.put("password", password);
            parameters.put("fcm_token", sharedPreferences.getString(Constants.device_token, "null"));
            parameters.put("player_id", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.LOGIN, parameters, new Response.Listener<JSONObject>() {

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
                        Toast.makeText(LoginFormActivity.this, "Something Wrong with Api", Toast.LENGTH_LONG).show();
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

                enable_location();
            } else {
                Toast.makeText(this, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }
    }

}
