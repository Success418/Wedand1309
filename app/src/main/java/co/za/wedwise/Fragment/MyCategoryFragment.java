package co.za.wedwise.Fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Item.CategoryItem;
import co.za.wedwise.Models.CategoryModels;
import co.za.wedwise.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCategoryFragment extends Fragment {


    public MyCategoryFragment() {
        // Required empty public constructor
    }


    RecyclerView category;
    ArrayList<CategoryModels> mListCat = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_category, container, false);

        category = view.findViewById(R.id.category);
        category.setHasFixedSize(true);
        category.setNestedScrollingEnabled(false);
        //category.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        category.setLayoutManager(new LinearLayoutManager(getActivity()));
        getCategory();

        return view;
    }

    private void getCategory() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
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
                   // catNameList.add(userdata.getString("cname"));
                    item.setCategoryName(userdata.getString("cname"));
                    item.setCategoryImage(userdata.getString("cimage"));
                    mListCat.add(item);

                }

                Collections.sort(mListCat, new Comparator<CategoryModels>() {
                    public int compare(CategoryModels v1, CategoryModels v2) {
                        return v1.getCategoryName().compareTo(v2.getCategoryName());
                    }
                });

                CategoryItem categoryItem = new CategoryItem(getActivity(), mListCat, R.layout.item_new_category);
                category.setAdapter(categoryItem);
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }
}
