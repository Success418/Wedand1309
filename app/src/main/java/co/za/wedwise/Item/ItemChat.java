package co.za.wedwise.Item;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import co.za.wedwise.R;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Fragment.ChatFragment;
import co.za.wedwise.Models.ChatModels;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by za on 12/12/2018.
 */

public class ItemChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatModels> mDataSet;
    String myID;
    private static final int mychat = 1;
    private static final int friendchat = 2;
    private static final int mychatimage = 3;
    private static final int otherchatimage = 4;
    private static final int mygifimage = 5;
    private static final int othergifimage = 6;
    private static final int alert_message = 7;

    private static final int my_audio_message = 8;
    private static final int other_audio_message = 9;



    Context context;
    Integer today_day = 0;

    private ItemChat.OnItemClickListener listener;
    private ItemChat.OnLongClickListener long_listener;

    public interface OnItemClickListener {
        void onItemClick(ChatModels item, View view);
    }

    public interface OnLongClickListener {
        void onLongclick (ChatModels item, View view);
    }

    public ItemChat(List<ChatModels> dataSet, String id, Context context, ItemChat.OnItemClickListener listener, ItemChat.OnLongClickListener long_listener) {
        mDataSet = dataSet;
        this.myID=id;
        this.context=context;
        this.listener = listener;
        this.long_listener=long_listener;
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View v = null;
        switch (viewtype){
            case mychat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_my, viewGroup, false);
                Chatviewholder mychatHolder = new Chatviewholder(v);
                return mychatHolder;
            case friendchat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_other, viewGroup, false);
                Chatviewholder friendchatHolder = new Chatviewholder(v);
                return friendchatHolder;
            case mychatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_my, viewGroup, false);
                Chatimageviewholder mychatimageHolder = new Chatimageviewholder(v);
                return mychatimageHolder;
            case otherchatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_other, viewGroup, false);
                Chatimageviewholder otherchatimageHolder = new Chatimageviewholder(v);
                return otherchatimageHolder;

            case my_audio_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chataudio, viewGroup, false);
                Chataudioviewholder chataudioviewholder = new Chataudioviewholder(v);
                return chataudioviewholder;

            case other_audio_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_other, viewGroup, false);
                Chataudioviewholder other = new Chataudioviewholder(v);
                return other;

           case mygifimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_gif_my, viewGroup, false);
                Chatimageviewholder mychatgigHolder = new Chatimageviewholder(v);
                return mychatgigHolder;
            case othergifimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_gif_other, viewGroup, false);
                Chatimageviewholder otherchatgifHolder = new Chatimageviewholder(v);
                return otherchatgifHolder;
            case alert_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_alert, viewGroup, false);
                Alertviewholder alertviewholder = new Alertviewholder(v);
                return alertviewholder;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatModels chat = mDataSet.get(position);

        if(chat.getType().equals("text")){
            Chatviewholder chatviewholder=(Chatviewholder) holder;
        // check if the message is from sender or receiver
            if(chat.getSender_id().equals(myID)){
                if(chat.getStatus().equals("1"))
                    chatviewholder.message_seen.setText("Seen at "+chat.getTime());
                else
                    chatviewholder.message_seen.setText("Sent");

            }else {
                chatviewholder.message_seen.setText("");
            }

        if (position != 0) {
            ChatModels chat2 = mDataSet.get(position - 1);
            if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                chatviewholder.datetxt.setVisibility(View.GONE);
            } else {
                chatviewholder.datetxt.setVisibility(View.VISIBLE);
                chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
            }
            chatviewholder.message.setText(chat.getText());
        }else {
            chatviewholder.datetxt.setVisibility(View.VISIBLE);
            chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
            chatviewholder.message.setText(chat.getText());
        }

        chatviewholder.bind(chat,long_listener);

        }

        else if(chat.getType().equals("image")){
            final Chatimageviewholder chatimageholder=(Chatimageviewholder) holder;
            if(chat.getSender_id().equals(myID)){
                if(chat.getStatus().equals("1"))
                    chatimageholder.messageSeen.setText("Seen at "+chat.getTime());
                else
                    chatimageholder.messageSeen.setText("Sent");

            }else {
                chatimageholder.messageSeen.setText("");
            }
            if(chat.getPic_url().equals("none")){
               if(ChatFragment.uploadingImageId.equals(chat.getChat_id())){
                chatimageholder.progressBar.setVisibility(View.VISIBLE);
                   chatimageholder.messageSeen.setText("");
                }else {
                   chatimageholder.progressBar.setVisibility(View.GONE);
                   chatimageholder.notSendMessageIcon.setVisibility(View.VISIBLE);
                   chatimageholder.messageSeen.setText("Not delivered. ");
               }
            }else {
                chatimageholder.notSendMessageIcon.setVisibility(View.GONE);
                chatimageholder.progressBar.setVisibility(View.GONE);
            }

            if (position != 0) {
                ChatModels chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                    chatimageholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatimageholder.datetxt.setVisibility(View.VISIBLE);
                    chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
                Picasso.with(context).load(chat.getPic_url()).placeholder(R.drawable.image_placeholder).resize(400,400).centerCrop().into(chatimageholder.chatimage);
            }else {
                chatimageholder.datetxt.setVisibility(View.VISIBLE);
                chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                Picasso.with(context).load(chat.getPic_url()).placeholder(R.drawable.image_placeholder).resize(400,400).centerCrop().into(chatimageholder.chatimage);


            }

            chatimageholder.bind(mDataSet.get(position),listener,long_listener);
        }



        else if(chat.getType().equals("audio")){
            final Chataudioviewholder chataudioviewholder=(Chataudioviewholder) holder;
            // check if the message is from sender or receiver
            if(chat.getSender_id().equals(myID)){
                if(chat.getStatus().equals("1"))
                    chataudioviewholder.messageSeen.setText("Seen at "+chat.getTime());
                else
                    chataudioviewholder.messageSeen.setText("Sent");

            }else {
                chataudioviewholder.messageSeen.setText("");
            }
            if(chat.getPic_url().equals("none")){
                if(ChatFragment.uploadingAudioId.equals(chat.getChat_id())){
                    chataudioviewholder.progressBar.setVisibility(View.VISIBLE);
                    chataudioviewholder.messageSeen.setText("");
                }else {
                    chataudioviewholder.progressBar.setVisibility(View.GONE);
                    chataudioviewholder.notSendMessageIcon.setVisibility(View.VISIBLE);
                    chataudioviewholder.messageSeen.setText("Not delivered. ");
                }
            }else {
                chataudioviewholder.notSendMessageIcon.setVisibility(View.GONE);
                chataudioviewholder.progressBar.setVisibility(View.GONE);
            }

            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModels chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                    chataudioviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    chataudioviewholder.datetxt.setVisibility(View.VISIBLE);
                    chataudioviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
            }else {
                chataudioviewholder.datetxt.setVisibility(View.VISIBLE);
                chataudioviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

            }

            if(ChatFragment.uploadingAudioId.equals("none")){

            }

            chataudioviewholder.seekBar.setEnabled(false);

            File fullpath = new File(Environment.getExternalStorageDirectory() +"/BaseApp/"+chat.getChat_id()+".mp3");
            if(fullpath.exists()) {
                chataudioviewholder.totalTime.setText(getfileduration(Uri.parse(fullpath.getAbsolutePath())));

            }else {
                chataudioviewholder.totalTime.setText(null);
            }


            chataudioviewholder.bind(mDataSet.get(position),listener,long_listener);

        }





        else if(chat.getType().equals("gif")){
            final Chatimageviewholder chatimageholder=(Chatimageviewholder) holder;
            if(chat.getSender_id().equals(myID)){
                if(chat.getStatus().equals("1"))
                chatimageholder.messageSeen.setText("Seen at "+chat.getTime());
                else
                    chatimageholder.messageSeen.setText("Sent");

            }else {
                chatimageholder.messageSeen.setText("");
            }
            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModels chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                    chatimageholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatimageholder.datetxt.setVisibility(View.VISIBLE);
                    chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
                Glide.with(context)
                        .load(Constants.gif_firstpart_chat+chat.getPic_url()+ Constants.gif_secondpart_chat)
                        .into(chatimageholder.chatimage);
            }else {
                chatimageholder.datetxt.setVisibility(View.VISIBLE);
                chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                Glide.with(context)
                        .load(Constants.gif_firstpart_chat+chat.getPic_url()+ Constants.gif_secondpart_chat)
                        .into(chatimageholder.chatimage);

            }

            chatimageholder.bind(mDataSet.get(position),listener,long_listener);
        }


        else if(chat.getType().equals("delete")){
            Alertviewholder alertviewholder=(Alertviewholder) holder;
            alertviewholder.message.setTextColor(context.getResources().getColor(R.color.gray));
            alertviewholder.message.setBackground(context.getResources().getDrawable(R.drawable.round_edittext_background));

            alertviewholder.message.setText( "This message is deleted by "+chat.getSender_name());

            if (position != 0) {
                ChatModels chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(11, 13).equals(chat.getTimestamp().substring(11, 13))) {
                    alertviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    alertviewholder.datetxt.setVisibility(View.VISIBLE);
                    alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }

            }else {
                alertviewholder.datetxt.setVisibility(View.VISIBLE);
                alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

            }

        }


    }

    @Override
    public int getItemViewType(int position) {
        if(mDataSet.get(position).getType().equals("text")){
         if (mDataSet.get(position).getSender_id().equals(myID)) {
            return mychat;
            }
        return friendchat;
        }
        else if(mDataSet.get(position).getType().equals("image")) {
            if (mDataSet.get(position).getSender_id().equals(myID)) {
                return mychatimage;
            }

            return otherchatimage;

        }
        else if(mDataSet.get(position).getType().equals("audio")) {
            if (mDataSet.get(position).getSender_id().equals(myID)) {
                return my_audio_message;
            }
            return other_audio_message;
        }

        else if(mDataSet.get(position).getType().equals("gif")) {
            if (mDataSet.get(position).getSender_id().equals(myID)) {
                return mygifimage;
            }

            return othergifimage;
        }
        else {
            return alert_message;
        }
    }

    class Chatviewholder extends RecyclerView.ViewHolder {
        TextView message,datetxt,message_seen;
        View view;
        public Chatviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.message = view.findViewById(R.id.msgtxt);
            this.datetxt=view.findViewById(R.id.datetxt);
            message_seen=view.findViewById(R.id.messageseen);
        }

        public void bind(final ChatModels item, final ItemChat.OnLongClickListener long_listener) {
            message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long_listener.onLongclick(item,v);
                    return false;
                }
            });
        }
    }


    // second is for the chat image
    class Chatimageviewholder extends RecyclerView.ViewHolder {
        ImageView chatimage;
        TextView datetxt, messageSeen;
        ProgressBar progressBar;
        ImageView notSendMessageIcon;
        View getView;
        public Chatimageviewholder(View itemView) {
            super(itemView);
            getView = itemView;
            this.chatimage = getView.findViewById(R.id.chatimage);
            this.datetxt= getView.findViewById(R.id.datetxt);
            messageSeen = getView.findViewById(R.id.messageseen);
            notSendMessageIcon = getView.findViewById(R.id.notsend);
            progressBar = getView.findViewById(R.id.progress);
        }

        public void bind(final ChatModels item, final ItemChat.OnItemClickListener listener, final ItemChat.OnLongClickListener long_listener) {

            chatimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item,v);
                }
            });

            chatimage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long_listener.onLongclick(item,v);
                    return false;
                }
            });
        }

    }

    class Chataudioviewholder extends RecyclerView.ViewHolder{
        TextView datetxt, messageSeen;
        ProgressBar progressBar;
        ImageView notSendMessageIcon;
        ImageView playBtn;
        SeekBar seekBar;
        TextView totalTime;
        LinearLayout audioBubble;
        View getView;

        public Chataudioviewholder(View itemView) {
            super(itemView);
            getView = itemView;
            audioBubble = getView.findViewById(R.id.audiobubble);
            datetxt = getView.findViewById(R.id.datetxt);
            messageSeen = getView.findViewById(R.id.messageseen);
            notSendMessageIcon = getView.findViewById(R.id.notsend);
            progressBar = getView.findViewById(R.id.progress);
            this.playBtn = getView.findViewById(R.id.playbtn);
            this.seekBar = getView.findViewById(R.id.seekbar);
            this.totalTime = getView.findViewById(R.id.totaltime);

        }

        public void bind(final ChatModels item, final ItemChat.OnItemClickListener listener, final ItemChat.OnLongClickListener long_listener) {



            audioBubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item,v);
                }
            });

            audioBubble.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long_listener.onLongclick(item,v);
                    return false;
                }
            });


        }


    }

    class Alertviewholder extends RecyclerView.ViewHolder {
        TextView message, datetxt;
        View getView;
        public Alertviewholder(View itemView) {
            super(itemView);
            getView = itemView;
            this.message = getView.findViewById(R.id.message);
            this.datetxt = getView.findViewById(R.id.datetxt);
        }

    }

    public String ChangeDate(String date){
        long currenttime= System.currentTimeMillis();

        long databasedate = 0;
        Date d = null;
        try {
            d = Constants.df.parse(date);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
       long difference=currenttime-databasedate;
       if(difference<86400000){
           int chatday= Integer.parseInt(date.substring(0,2));
           SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
           if(today_day==chatday)
           return "Today "+sdf.format(d);
           else if((today_day-chatday)==1)
           return "Yesterday "+sdf.format(d);
       }
       else if(difference<172800000){
           int chatday= Integer.parseInt(date.substring(0,2));
           SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
           if((today_day-chatday)==1)
           return "Yesterday "+sdf.format(d);
       }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy hh:mm a");

       if(d!=null)
       return sdf.format(d);
       else
           return "";
    }

    public String getfileduration(Uri uri) {
        try {

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        final int file_duration = Integer.parseInt(durationStr);

        long second = (file_duration / 1000) % 60;
        long minute = (file_duration / (1000 * 60)) % 60;

        return String.format("%02d:%02d", minute, second);
    }
         catch (Exception e){

        }
        return null;
    }


}
