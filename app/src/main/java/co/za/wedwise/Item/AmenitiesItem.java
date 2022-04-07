package co.za.wedwise.Item;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import co.za.wedwise.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by za on 3/24/2019.
 */
public class AmenitiesItem extends RecyclerView.Adapter<AmenitiesItem.ItemRowHolder> {

    private ArrayList<String> dataList;
    private Context mContext;

    public AmenitiesItem(Context context, ArrayList<String> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_amenities, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemRowHolder holder, final int position) {
        holder.text.setText(dataList.get(position));

        Random rand = new Random();
        int i = rand.nextInt(4);


        switch (i) {
            case 1:
                holder.text.getBackground().setColorFilter(Color.parseColor("#fd0f57"), PorterDuff.Mode.SRC_ATOP);
                break;
            case 2:
                holder.text.getBackground().setColorFilter(Color.parseColor("#03abcc"), PorterDuff.Mode.SRC_ATOP);
                break;

            case 3:
                holder.text.getBackground().setColorFilter(Color.parseColor("#00d953"), PorterDuff.Mode.SRC_ATOP);
                break;

            case 4:
                holder.text.getBackground().setColorFilter(Color.parseColor("#ffb700"), PorterDuff.Mode.SRC_ATOP);
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public TextView text;
        private ItemRowHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
