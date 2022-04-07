package co.za.wedwise.Item;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import co.za.wedwise.R;
import com.squareup.picasso.Picasso;
import com.wonshinhyo.dragrecyclerview.DragAdapter;
import com.wonshinhyo.dragrecyclerview.DragHolder;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by za on 11/16/2018.
 */

public class ItemProfile extends DragAdapter {
    Context context;

    ArrayList<String> photos;

    private ItemProfile.OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(String item, int postion, View view);
    }


    public ItemProfile(Context context, ArrayList<String> arrayList, ItemProfile.OnItemClickListener listener)  {
        super(context,arrayList);
        this.context=context;
        photos=arrayList;
        this.listener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {

        return new HistoryviewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_editprofile, viewGroup, false));


    }



    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public void onBindViewHolder(final DragRecyclerView.ViewHolder hol, final int position) {
        super.onBindViewHolder(hol, position);
        HistoryviewHolder holder = (HistoryviewHolder) hol;
        holder.bind(photos.get(position),position,listener);

        if(photos.get(position).equals("")){
            holder.cancelButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add));
            Picasso.with(context).load("null").placeholder(R.drawable.ic_man).centerCrop().resize(200,300).into(holder.image);

        }else {
            holder.cancelButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel));
            Picasso.with(context).load(photos.get(position)).placeholder(R.drawable.ic_man).centerCrop().resize(200,300).into(holder.image);
        }
    }
    /**
     * Inner Class for a recycler getView
     */
    class HistoryviewHolder extends DragHolder {
        View getView;
        CircleImageView image;
        ImageButton cancelButton;
        public HistoryviewHolder(View itemView) {
            super(itemView);
            getView = itemView;
            image = getView.findViewById(R.id.image);
            cancelButton = getView.findViewById(R.id.button);
        }


        public void bind(final String item, final int position , final ItemProfile.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item,position,v);
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item,position,v);
                }
            });
        }


    }

}

