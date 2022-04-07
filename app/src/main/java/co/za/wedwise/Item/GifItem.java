package co.za.wedwise.Item;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import co.za.wedwise.R;
import co.za.wedwise.Constants.Constants;

import java.util.ArrayList;

/**
 * Created by za on 12/20/2018.
 */

public class GifItem extends RecyclerView.Adapter<GifItem.CustomViewHolder >{
    public Context context;
    ArrayList<String> gifList = new ArrayList<>();
    private GifItem.OnItemClickListener listener;

public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public GifItem(Context context, ArrayList<String> urllist, GifItem.OnItemClickListener listener) {
        this.context = context;
        this.gifList = urllist;
        this.listener = listener;

    }

    @Override
    public GifItem.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gif,null);
        GifItem.CustomViewHolder viewHolder = new GifItem.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return gifList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView gifImage;

        public CustomViewHolder(View view) {
            super(view);
            gifImage = view.findViewById(R.id.gifimage);
        }

        public void bind(final String item, final GifItem.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });


        }

    }


    @Override
    public void onBindViewHolder(final GifItem.CustomViewHolder holder, final int i) {
        holder.bind(gifList.get(i),listener);
        Glide.with(context)
                .load(Constants.gif_firstpart+ gifList.get(i)+ Constants.gif_secondpart)
                .into(holder.gifImage);
   }




}