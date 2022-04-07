package co.za.wedwise.Activity;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Constants.Functions;
import co.za.wedwise.R;

public class RegisterAsVendorActivity extends Activity implements View.OnClickListener {


    EditText etName, etSurname, etEmail, etPhone, etCompanyName, etDisplayName;
    Button btnSignUp;
    ProgressBar progressBar;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_vendor);
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(RegisterAsVendorActivity.this);
                onBackPressed();
            }
        });

        inIt();
    }

    private void inIt() {
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCompanyName = findViewById(R.id.etCompanyName);
        etDisplayName = findViewById(R.id.etDisplayName);
        btnSignUp = findViewById(R.id.formOKBtn);
        btnSignUp.setOnClickListener(this);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

    }

    private void validateDetails() {
        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError(getResources().getString(R.string.please_enter_your_name));
            etName.requestFocus();
        } else if (TextUtils.isEmpty(etSurname.getText())) {
            etSurname.setError(getResources().getString(R.string.please_enter_your_surname));
            etSurname.requestFocus();
        } else if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError(getResources().getString(R.string.please_enter_your_email));
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(etPhone.getText())) {
            etPhone.setError(getResources().getString(R.string.please_enter_your_phone));
            etPhone.requestFocus();
        } else {
//            http://wedwise.co.za/admin/api.php?vendorRegistration&user_id=1&first_name=2&last_name=3&email=aa@aa.com&phone=123&company_name=test
            Map<String, String> parameters = new HashMap<>();
//            parameters.put("user_id", DrawerActivity.user_id);
//            parameters.put("first_name", Uri.encode(etName.getText().toString()));
//            parameters.put("last_name", Uri.encode(etSurname.getText().toString()));
//            parameters.put("email", Uri.encode(etEmail.getText().toString()));
//            parameters.put("phone", Uri.encode(etPhone.getText().toString()));
//            parameters.put("company_name", Uri.encode(etCompanyName.getText().toString()));
//            parameters.put("display_name", Uri.encode(etDisplayName.getText().toString()));
            progressBar.setVisibility(View.VISIBLE);
            RequestQueue rq = Volley.newRequestQueue(context);

            String url = Constants.VENDOR_REGISTRATION
                    + "&user_id=" + Uri.encode(DrawerActivity.user_id)
                    + "&first_name="+Uri.encode(etName.getText().toString())
                    + "&last_name="+Uri.encode(etSurname.getText().toString())
                    + "&email="+Uri.encode(etEmail.getText().toString())
                    + "&phone="+Uri.encode(etPhone.getText().toString())
                    + "&company_name="+Uri.encode(etCompanyName.getText().toString())
                    + "&display_name="+Uri.encode(etDisplayName.getText().toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, new JSONObject(parameters), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String respo = response.toString();
                            Log.d("responce", respo);
                            progressBar.setVisibility(View.GONE);
                            try {
                                if (response.getBoolean("status")) {
                                    AlertDialog dialog = new AlertDialog.Builder(context).
                                            setTitle(getResources().getString(R.string.registration_success))
                                            .setMessage(getResources().getString(R.string.vendor_registration_message))
                                            .setCancelable(false)
                                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialog) {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            })
                                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            }).create();
                                    dialog.show();

                                } else {
                                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                Toast.makeText(context, "Problem occured while registration", Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Log.d("respo", error.toString());
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "Problem occured while registration", Toast.LENGTH_SHORT).show();
                        }
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rq.getCache().clear();
            rq.add(jsonObjectRequest);
        }


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.formOKBtn) {
            validateDetails();
        }
    }
}
