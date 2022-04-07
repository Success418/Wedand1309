package co.za.wedwise.Item;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import co.za.wedwise.Activity.AllPropByCityActivity;
import co.za.wedwise.Models.CityModels;
import co.za.wedwise.R;
//import co.za.wedwise.Utils.BannerAds;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by za on 3/24/2019.
 */

public class CityItem extends RecyclerView.Adapter<CityItem.ItemRowHolder> {

    private ArrayList<CityModels> dataList;
    private Context mContext;
    private int rowLayout;

    public CityItem(Context context, ArrayList<CityModels> dataList, int rowLayout) {
        this.dataList = dataList;
        this.mContext = context;
        this.rowLayout = rowLayout;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_square, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
        final CityModels singleItem = dataList.get(position);
        holder.text.setText(singleItem.getCityName());
        Picasso.with(mContext)
                .load(singleItem.getCityImage())
                .resize(100,100)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.images);

        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                BannerAds.ShowInterstitialAds(mContext);
                Intent intent = new Intent(mContext, AllPropByCityActivity.class);
                intent.putExtra("Id", singleItem.getCityId());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView images;

        ItemRowHolder(View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
        }
    }
}
