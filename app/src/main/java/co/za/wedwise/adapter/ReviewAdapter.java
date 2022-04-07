package co.za.wedwise.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import co.za.wedwise.Activity.DrawerActivity;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Item.ReviewItem;
import co.za.wedwise.R;
import co.za.wedwise.Utils.ProjectUtils;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    Activity context;
    ArrayList<ReviewItem> list;

    public ReviewAdapter(Activity context, ArrayList<ReviewItem> reviewModels) {
        this.context = context;
        this.list = reviewModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ReviewItem item = list.get(position);
        if (ProjectUtils.isNumeric(item.getRate())) {
            holder.ratingView.setRating(Float.valueOf(item.getRate()));
        } else {
            holder.itemView.setVisibility(View.GONE);
        }

        holder.fullname.setText(item.getUser_name());
        holder.message.setText(item.getReview());
        holder.imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportReview(item);
            }
        });

        if (item.getImageprofile() != null && !item.getImageprofile().equals("")) {
            Picasso.with(context).load(item.getImageprofile()).into(holder.imgProfile);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fullname, message;
        RatingView ratingView;
        CircleImageView imgProfile;
        ImageView imgReport;

        public MyViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            ratingView = itemView.findViewById(R.id.ratingView);
            fullname = itemView.findViewById(R.id.fullname);
            imgProfile = itemView.findViewById(R.id.userimages);
            imgReport = itemView.findViewById(R.id.img_report);
        }
    }

    private void reportReview(final ReviewItem item) {


        final EditText edittext = new EditText(context);
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AlertDialog);
        alert.setMessage("Enter Your Message");
        alert.setTitle("");
        edittext.setTextColor(context.getResources().getColor(R.color.black));
        edittext.setHint("Enter Your Message Here");
        alert.setView(edittext);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
//            Editable YouEditTextValue = edittext.getText();
                //OR
                String message = edittext.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(context, "Please enter your message", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject parameters = new JSONObject();

                    RequestQueue rq = Volley.newRequestQueue(context);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.POST, Constants.FAVOURITE + "&user_id="+user_id+"&venue_id"+venue_id+"&owner_id"+owner_id, parameters, new Response.Listener<JSONObject>() {
                            (Request.Method.GET, Constants.RATING_REPORT + "&user_id=" + DrawerActivity.user_id + "&venue_id=" + item.getPropid() + "&message=" + message, parameters, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    String respo = response.toString();
                                    Log.d("responce", respo);
                                    try {
                                        JSONObject jsonObject = new JSONObject(respo);
                                        String code = jsonObject.optString("code");
                                        Boolean status = jsonObject.optBoolean("status");
                                        if (code.equals("200")) {
                                            if (status) {
                                                Toast.makeText(context, "Report has been added", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "You have already reported this review.", Toast.LENGTH_SHORT).show();
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
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.dismiss();
            }
        });

        alert.show();


    }
}
