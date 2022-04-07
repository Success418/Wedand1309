package co.za.wedwise.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import co.za.wedwise.Constants.BaseApp;
import co.za.wedwise.Models.AboutModels;
import co.za.wedwise.Utils.NetworkUtils;
import co.za.wedwise.Utils.SendAudio;
import co.za.wedwise.Item.ItemChat;
import co.za.wedwise.Item.GifItem;
import co.za.wedwise.Constants.Functions;
import co.za.wedwise.Constants.Constants;
import co.za.wedwise.Activity.DrawerActivity;
import co.za.wedwise.R;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApi;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListMediaResponse;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import co.za.wedwise.Models.ChatModels;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static android.app.Activity.RESULT_OK;


public class ChatFragment extends Fragment {

    DatabaseReference reference;
    String senderid = "";
    String Receiverid = "";
    String Receiver_name = "";
    String Receiver_pic = "null";
    public static String token = "null";
    TextView typingIndicator;
    EmojiconEditText message;
    AboutModels modelAbout;

    private DatabaseReference adduserToInbox;
    private DatabaseReference mchatRefReteriving;
    private DatabaseReference sendTypingIndication;
    private DatabaseReference receiveTypingIndication;

    RecyclerView chatrecyclerview;
    TextView userName;
    private List<ChatModels> mChats = new ArrayList<>();
    ItemChat mAdapter;
    ProgressBar progressBar;

    Query queryGetchat;
    Query myBlockStatusQuery;
    Query otherBlockStatusQuery;
    boolean isUserAlreadyBlock = false;

    ImageView profileimage;
    public static String senderidForCheckNotification = "";
    public static String uploadingImageId = "none";

    Context context;
    IOSDialog loadingView;
    View getView;
    LinearLayout gifLayout, takephoto, llcamera, opengallery;
    ImageView uploadGifBtn, sendbtn, camerabtn;
    ImageButton block, micBtn;

    public static String uploadingAudioId = "none";

    File direct;

    SendAudio sendAudio;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        getView = inflater.inflate(R.layout.fragment_chat, container, false);
        modelAbout = new AboutModels();
        context = getContext();
        Log.e("Chat", "called");
        if (NetworkUtils.isConnected(getActivity())) {
            Log.e("Chat", "--->");
            getSetting();
        } else {
            Toast.makeText(context, "no internet connection", Toast.LENGTH_SHORT).show();
        }
        direct = new File(Environment.getExternalStorageDirectory() + "/BaseKencan/");

        reference = FirebaseDatabase.getInstance().getReference();
        adduserToInbox = FirebaseDatabase.getInstance().getReference();

        userName = getView.findViewById(R.id.fullname);
        profileimage = getView.findViewById(R.id.profileimage);
        block = getView.findViewById(R.id.block);
        progressBar = getView.findViewById(R.id.progress_bar);
        chatrecyclerview = getView.findViewById(R.id.chatlist);
        gifLayout = getView.findViewById(R.id.gif_layout);
        sendbtn = getView.findViewById(R.id.sendbtn);
        takephoto = getView.findViewById(R.id.takephoto);
        opengallery = getView.findViewById(R.id.opengallery);
        uploadGifBtn = getView.findViewById(R.id.upload_gif_btn);
        llcamera = getView.findViewById(R.id.llcamera);
        camerabtn = getView.findViewById(R.id.uploadimagebtn);
        micBtn = getView.findViewById(R.id.mic_btn);

        message = getView.findViewById(R.id.msgedittext);
        message.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.toString().trim().length() == 0) {
                    sendbtn.setVisibility(View.GONE);
                    micBtn.setVisibility(View.VISIBLE);
                } else {
                    sendbtn.setVisibility(View.VISIBLE);
                    micBtn.setVisibility(View.GONE);
                }
            }
        });


        ImageView Emoji = getView.findViewById(R.id.emoticon);
        EmojiconEditText emojiconEditText = getView.findViewById(R.id.msgedittext);
        EmojIconActions emojIcon = new EmojIconActions(getActivity(), getView, emojiconEditText, Emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {

            }

            @Override
            public void onKeyboardClose() {
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isUserAlreadyBlock)
                    unblock();
                else
                    block();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            senderid = bundle.getString("Sender_Id");
            Receiverid = bundle.getString("Receiver_Id");
            Receiver_name = bundle.getString("name");
            Receiver_pic = bundle.getString("picture");
            userName.setText(Receiver_name);
            senderidForCheckNotification = Receiverid;

            if (Receiver_pic.isEmpty()) {
                Picasso.with(context).load(Constants.BASEURL + "/asset/images/user.png")
                        .resize(100, 100)
                        .into(profileimage);
            } else {
                Picasso.with(context).load(Receiver_pic)
                        .resize(100, 100)
                        .into(profileimage);
            }


            sendAudio = new SendAudio(context, message, reference, adduserToInbox,
                    senderid, Receiverid, Receiver_name, Receiver_pic);

            reference.child("Users").child(Receiverid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        token = dataSnapshot.child("token").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        loadingView = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        final LinearLayoutManager layout = new LinearLayoutManager(context);
        layout.setStackFromEnd(false);
        chatrecyclerview.setLayoutManager(layout);
        chatrecyclerview.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(chatrecyclerview, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        mAdapter = new ItemChat(mChats, senderid, context, new ItemChat.OnItemClickListener() {
            @Override
            public void onItemClick(ChatModels item, View v) {

                if (item.getType().equals("image"))
                    OpenfullsizeImage(item);

                if (v.getId() == R.id.audiobubble) {
                    RelativeLayout mainlayout = (RelativeLayout) v.getParent();

                    File fullpath = new File(Environment.getExternalStorageDirectory() + "/BaseKencan/" + item.getChat_id() + ".mp3");
                    if (fullpath.exists()) {

                        OpenAudio(fullpath.getAbsolutePath());

                    } else {
                        download_audio((ProgressBar) mainlayout.findViewById(R.id.progress), item);
                    }

                }


            }


        }, new ItemChat.OnLongClickListener() {
            @Override
            public void onLongclick(ChatModels item, View view) {
                if (view.getId() == R.id.msgtxt) {
                    if (senderid.equals(item.getSender_id()) && istodaymessage(item.getTimestamp()))
                        delete_Message(item);
                } else if (view.getId() == R.id.chatimage) {
                    if (senderid.equals(item.getSender_id()) && istodaymessage(item.getTimestamp()))
                        delete_Message(item);
                } else if (view.getId() == R.id.audiobubble) {
                    if (senderid.equals(item.getSender_id()) && istodaymessage(item.getTimestamp()))
                        delete_Message(item);
                }
            }
        });

        chatrecyclerview.setAdapter(mAdapter);
        chatrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = layout.findFirstCompletelyVisibleItemPosition();

                if (userScrolled && (scrollOutitems == 0 && mChats.size() > 9)) {
                    userScrolled = false;
                    loadingView.show();
                    reference.child("chat").child(senderid + "-" + Receiverid).orderByChild("chat_id")
                            .endAt(mChats.get(0).getChat_id()).limitToLast(20)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<ChatModels> arrayList = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ChatModels item = snapshot.getValue(ChatModels.class);
                                        arrayList.add(item);
                                    }
                                    for (int i = arrayList.size() - 2; i >= 0; i--) {
                                        mChats.add(0, arrayList.get(i));
                                    }

                                    mAdapter.notifyDataSetChanged();
                                    loadingView.cancel();

                                    if (arrayList.size() > 8) {
                                        chatrecyclerview.scrollToPosition(arrayList.size());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(message.getText().toString())) {
                    if (gifLayout.getVisibility() == View.VISIBLE) {
                        searchGif(message.getText().toString());
                    } else {
                        SendMessage(message.getText().toString());
                        message.setText(null);
                    }

                }
            }
        });
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_camrapermission())
                    openCameraIntent();
            }
        });

        opengallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_ReadStoragepermission()) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
            }
        });


        uploadGifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gifLayout.getVisibility() == View.VISIBLE) {
                    slideDown();
                } else {
                    slideUp();
                    GetGipy();
                }
            }
        });
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llcamera.getVisibility() == View.VISIBLE) {
                    closecameralayout();
                } else {
                    opencameralayout();
                }
            }
        });

        getView.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            }
        });

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    SendTypingIndicator(false);
                }
            }
        });


        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    SendTypingIndicator(false);
                } else {
                    SendTypingIndicator(true);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final long[] touchtime = {System.currentTimeMillis()};
        micBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (check_Recordpermission() && check_writeStoragepermission()) {
                        touchtime[0] = System.currentTimeMillis();
                        sendAudio.Runbeep("start");
                        Toast.makeText(context, "Recording...", Toast.LENGTH_SHORT).show();
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (touchtime[0] + 4000 > System.currentTimeMillis()) {
                        sendAudio.stop_timer();
                        Toast.makeText(context, "Hold Mic Button to Record", Toast.LENGTH_SHORT).show();
                    } else {
                        sendAudio.stopRecording();
                        Toast.makeText(context, "Stop Recording...", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }

        });
        ReceivetypeIndication();

        getChat();
        return getView;
    }

    ValueEventListener valueEventListener;
    ChildEventListener eventListener;
    ValueEventListener my_inbox_listener;
    ValueEventListener other_inbox_listener;

    public void getChat() {
        mChats.clear();
        mchatRefReteriving = FirebaseDatabase.getInstance().getReference();
        queryGetchat = mchatRefReteriving.child("chat").child(senderid + "-" + Receiverid);

        myBlockStatusQuery = mchatRefReteriving.child("Inbox")
                .child(DrawerActivity.user_id)
                .child(Receiverid);

        otherBlockStatusQuery = mchatRefReteriving.child("Inbox")
                .child(Receiverid)
                .child(DrawerActivity.user_id);

        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    ChatModels model = dataSnapshot.getValue(ChatModels.class);
                    mChats.add(model);
                    mAdapter.notifyDataSetChanged();
                    chatrecyclerview.scrollToPosition(mChats.size() - 1);
                } catch (Exception ex) {
                    Log.e("", ex.getMessage());
                }
                ChangeStatus();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    try {
                        ChatModels model = dataSnapshot.getValue(ChatModels.class);

                        for (int i = mChats.size() - 1; i >= 0; i--) {
                            if (mChats.get(i).getTimestamp().equals(dataSnapshot.child("timestamp").getValue())) {
                                mChats.remove(i);
                                mChats.add(i, model);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Log.e("", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", databaseError.getMessage());
            }
        };

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(senderid + "-" + Receiverid)) {
                    progressBar.setVisibility(View.GONE);
                    queryGetchat.removeEventListener(valueEventListener);
                } else {
                    progressBar.setVisibility(View.GONE);
                    queryGetchat.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        my_inbox_listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("block").getValue() != null) {
                    String block = dataSnapshot.child("block").getValue().toString();
                    if (block.equals("1")) {
                        getView.findViewById(R.id.writechatlayout).setVisibility(View.INVISIBLE);
                    } else {
                        getView.findViewById(R.id.writechatlayout).setVisibility(View.VISIBLE);
                    }
                } else {
                    getView.findViewById(R.id.writechatlayout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        other_inbox_listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("block").getValue() != null) {
                    String block = dataSnapshot.child("block").getValue().toString();
                    isUserAlreadyBlock = block.equals("1");
                } else {
                    isUserAlreadyBlock = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        queryGetchat.limitToLast(20).addChildEventListener(eventListener);
        mchatRefReteriving.child("chat").addValueEventListener(valueEventListener);

        myBlockStatusQuery.addValueEventListener(my_inbox_listener);
        otherBlockStatusQuery.addValueEventListener(other_inbox_listener);
    }

    public void SendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Constants.df.format(c);

        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

        DatabaseReference reference = this.reference.child("chat").child(senderid + "-" + Receiverid).push();
        final String pushid = reference.getKey();
        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", Receiverid);
        message_user_map.put("sender_id", senderid);
        message_user_map.put("chat_id", pushid);
        message_user_map.put("text", message);
        message_user_map.put("type", "text");
        message_user_map.put("pic_url", "");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", DrawerActivity.user_name);
        message_user_map.put("timestamp", formattedDate);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);
        user_map.put(chat_user_ref + "/" + pushid, message_user_map);

        this.reference.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
                String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;

                HashMap sendermap = new HashMap<>();
                sendermap.put("rid", senderid);
                sendermap.put("name", DrawerActivity.user_name);
                sendermap.put("pic", DrawerActivity.image);
                sendermap.put("msg", message);
                sendermap.put("status", "0");
                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                sendermap.put("date", formattedDate);

                HashMap receivermap = new HashMap<>();
                receivermap.put("rid", Receiverid);
                receivermap.put("name", Receiver_name);
                receivermap.put("pic", Receiver_pic);
                receivermap.put("msg", message);
                receivermap.put("status", "1");
                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                receivermap.put("date", formattedDate);

                HashMap both_user_map = new HashMap<>();
                both_user_map.put(inbox_sender_ref, receivermap);
                both_user_map.put(inbox_receiver_ref, sendermap);

                adduserToInbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ChatFragment.SendPushNotification(ChatFragment.this.reference, DrawerActivity.user_name, message,
                                DrawerActivity.image,
                                token, Receiverid, senderid);
                    }
                });
            }
        });
    }

    public void UploadImage(ByteArrayOutputStream byteArrayOutputStream) {
        byte[] data = byteArrayOutputStream.toByteArray();
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Constants.df.format(c);


        DatabaseReference dref = this.reference.child("chat").child(senderid + "-" + Receiverid).push();
        final String key = dref.getKey();
        uploadingImageId = key;
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

        HashMap my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", Receiverid);
        my_dummi_pic_map.put("sender_id", senderid);
        my_dummi_pic_map.put("chat_id", key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type", "image");
        my_dummi_pic_map.put("pic_url", "none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", DrawerActivity.user_name);
        my_dummi_pic_map.put("timestamp", formattedDate);

        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        this.reference.updateChildren(dummy_push);


        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("images").child(key + ".png");
        final UploadTask uploadTask = reference.putBytes(data);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    uploadingImageId = "none";
                    HashMap message_user_map = new HashMap<>();
                    message_user_map.put("receiver_id", Receiverid);
                    message_user_map.put("sender_id", senderid);
                    message_user_map.put("chat_id", key);
                    message_user_map.put("text", "");
                    message_user_map.put("type", "image");
                    message_user_map.put("pic_url", downloadUri.toString());
//                message_user_map.put("pic_url",taskSnapshot.getDownloadUrl().toString());
                    message_user_map.put("status", "0");
                    message_user_map.put("time", "");
                    message_user_map.put("sender_name", DrawerActivity.user_name);
                    message_user_map.put("timestamp", formattedDate);

                    HashMap user_map = new HashMap<>();

                    user_map.put(current_user_ref + "/" + key, message_user_map);
                    user_map.put(chat_user_ref + "/" + key, message_user_map);

                    ChatFragment.this.reference.updateChildren(user_map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
                            String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;

                            HashMap sendermap = new HashMap<>();
                            sendermap.put("rid", senderid);
                            sendermap.put("name", DrawerActivity.user_name);
                            sendermap.put("pic", DrawerActivity.image);
                            sendermap.put("msg", "Send an image...");
                            sendermap.put("status", "0");
                            sendermap.put("timestamp", -1 * System.currentTimeMillis());
                            sendermap.put("date", formattedDate);

                            HashMap receivermap = new HashMap<>();
                            receivermap.put("rid", Receiverid);
                            receivermap.put("name", Receiver_name);
                            receivermap.put("pic", Receiver_pic);
                            receivermap.put("msg", "Send an image...");
                            receivermap.put("status", "1");
                            receivermap.put("timestamp", -1 * System.currentTimeMillis());
                            receivermap.put("date", formattedDate);

                            HashMap both_user_map = new HashMap<>();
                            both_user_map.put(inbox_sender_ref, receivermap);
                            both_user_map.put(inbox_receiver_ref, sendermap);

                            adduserToInbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    ChatFragment.SendPushNotification(ChatFragment.this.reference, DrawerActivity.user_name, "Send an Image....",
                                            DrawerActivity.image,
                                            token, Receiverid, senderid);


                                }
                            });
                        }
                    });

//                    createNewMessage(Common.TYPE_IMAGE, String.valueOf(downloadUri));
                } else {
                    // Handle failures
                    // ...
                }
            }
        })
//        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.d(TAG, "Image Uploaded");
//
//                if (taskSnapshot != null) {
//                    createNewMessage(Common.TYPE_IMAGE, String.valueOf(storageReference.getDownloadUrl()));
////                    Log.d(TAG, "URL" + taskSnapshot.getMetadata().getReference().getDownloadUrl().getResult(););
//                }
//            }
//        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "Image Upload Failed");

                    }
                });


    }

    public void SendGif(String url) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Constants.df.format(c);


        DatabaseReference dref = reference.child("chat").child(senderid + "-" + Receiverid).push();
        final String key = dref.getKey();

        String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

        HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", Receiverid);
        message_user_map.put("sender_id", senderid);
        message_user_map.put("chat_id", key);
        message_user_map.put("text", "");
        message_user_map.put("type", "gif");
        message_user_map.put("pic_url", url);
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", DrawerActivity.user_name);
        message_user_map.put("timestamp", formattedDate);
        HashMap user_map = new HashMap<>();

        user_map.put(current_user_ref + "/" + key, message_user_map);
        user_map.put(chat_user_ref + "/" + key, message_user_map);

        reference.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
                String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;


                HashMap sendermap = new HashMap<>();
                sendermap.put("rid", senderid);
                sendermap.put("name", DrawerActivity.user_name);
                sendermap.put("pic", DrawerActivity.image);
                sendermap.put("msg", "Send an gif image...");
                sendermap.put("status", "0");
                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                sendermap.put("date", formattedDate);

                HashMap receivermap = new HashMap<>();
                receivermap.put("rid", Receiverid);
                receivermap.put("name", Receiver_name);
                receivermap.put("pic", Receiver_pic);
                receivermap.put("msg", "Send an gif image...");
                receivermap.put("status", "1");
                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                receivermap.put("date", formattedDate);

                HashMap both_user_map = new HashMap<>();
                both_user_map.put(inbox_sender_ref, receivermap);
                both_user_map.put(inbox_receiver_ref, sendermap);

                adduserToInbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        ChatFragment.SendPushNotification(reference, DrawerActivity.user_name, "Send an gif image....",
                                DrawerActivity.image,
                                token, Receiverid, senderid);

                    }
                });

            }
        });
    }

    public void ChangeStatus() {
        final Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final Query query1 = reference.child("chat").child(Receiverid + "-" + senderid).orderByChild("status").equalTo("0");
        final Query query2 = reference.child("chat").child(senderid + "-" + Receiverid).orderByChild("status").equalTo("0");

        final DatabaseReference inbox_change_status_1 = reference.child("Inbox").child(senderid + "/" + Receiverid);
        final DatabaseReference inbox_change_status_2 = reference.child("Inbox").child(Receiverid + "/" + senderid);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)) {
                        String key = nodeDataSnapshot.getKey();
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", sdf.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)) {
                        String key = nodeDataSnapshot.getKey();
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", sdf.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        inbox_change_status_1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("rid").getValue().equals(Receiverid)) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        inbox_change_status_1.updateChildren(result);

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        inbox_change_status_2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("rid").getValue().equals(Receiverid)) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        inbox_change_status_2.updateChildren(result);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void download_audio(final ProgressBar p_bar, ChatModels item) {
        p_bar.setVisibility(View.VISIBLE);
        int downloadId = PRDownloader.download(item.getPic_url(), direct.getPath(), item.getChat_id() + ".mp3")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        p_bar.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onError(Error error) {

                    }
                });

    }

    public void OpenAudio(String path) {
        PlayAudioFragment play_audio_fragment = new PlayAudioFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("path", path);
        play_audio_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Chat_F, play_audio_fragment).commit();

    }

    private void delete_Message(final ChatModels chat_models) {

        final CharSequence[] options = {"Delete this message", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Delete this message")) {
                    update_message(chat_models);

                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    public void update_message(ChatModels item) {
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;


        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", item.getReceiver_id());
        message_user_map.put("sender_id", item.getSender_id());
        message_user_map.put("chat_id", item.getChat_id());
        message_user_map.put("text", "Delete this message");
        message_user_map.put("type", "delete");
        message_user_map.put("pic_url", "");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", DrawerActivity.user_name);
        message_user_map.put("timestamp", item.getTimestamp());

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + item.getChat_id(), message_user_map);
        user_map.put(chat_user_ref + "/" + item.getChat_id(), message_user_map);

        reference.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });

    }

    public void Block_user() {
        reference.child("Inbox")
                .child(Receiverid)
                .child(DrawerActivity.user_id).child("block").setValue("1");
        Toast.makeText(context, "User Blocked", Toast.LENGTH_SHORT).show();

    }

    public void UnBlock_user() {
        reference.child("Inbox")
                .child(Receiverid)
                .child(DrawerActivity.user_id).child("block").setValue("0");
        Toast.makeText(context, "User UnBlocked", Toast.LENGTH_SHORT).show();

    }

    public boolean istodaymessage(String date) {
        Calendar cal = Calendar.getInstance();
        int today_day = cal.get(Calendar.DAY_OF_MONTH);
        long currenttime = System.currentTimeMillis();

        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        long databasedate = 0;
        Date d = null;
        try {
            d = f.parse(date);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = currenttime - databasedate;
        if (difference < 86400000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            return today_day == chatday;
        }

        return false;
    }

    public boolean check_Recordpermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    Constants.permission_Recording_audio);
        }
        return false;
    }

    private boolean check_camrapermission() {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA}, Constants.permission_camera_code);
        }
        return false;
    }

    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.permission_Read_data);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    private boolean check_writeStoragepermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.permission_write_data);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.permission_camera_code) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Tap again", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show();

            }
        }

        if (requestCode == Constants.permission_Read_data) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Tap again", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == Constants.permission_write_data) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Tap Again", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == Constants.permission_Recording_audio) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Tap Again", Toast.LENGTH_SHORT).show();
            }
        }


    }


    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(context.getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, 1);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
                Matrix matrix = new Matrix();
                try {
                    ExifInterface exif = new ExifInterface(imageFilePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));

                InputStream imageStream = null;
                try {
                    imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                UploadImage(baos);

            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

                String path = getPath(selectedImage);
                Matrix matrix = new Matrix();
                ExifInterface exif = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    try {
                        exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
                UploadImage(baos);

            }

        }

    }

    public void SendTypingIndicator(boolean indicate) {
        if (indicate) {
            final HashMap message_user_map = new HashMap<>();
            message_user_map.put("receiver_id", Receiverid);
            message_user_map.put("sender_id", senderid);

            sendTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");
            sendTypingIndication.child(senderid + "-" + Receiverid).setValue(message_user_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendTypingIndication.child(Receiverid + "-" + senderid).setValue(message_user_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }
            });
        } else {
            sendTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");

            sendTypingIndication.child(senderid + "-" + Receiverid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    sendTypingIndication.child(Receiverid + "-" + senderid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                }
            });

        }

    }


    public void ReceivetypeIndication() {
        typingIndicator = getView.findViewById(R.id.typeindicator);

        receiveTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");
        receiveTypingIndication.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Receiverid + "-" + senderid).exists()) {
                    String receiver = String.valueOf(dataSnapshot.child(Receiverid + "-" + senderid).child("sender_id").getValue());
                    if (receiver.equals(Receiverid)) {
                        typingIndicator.setVisibility(View.VISIBLE);
                    }
                } else {
                    typingIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uploadingImageId = "none";
        SendTypingIndicator(false);
        queryGetchat.removeEventListener(eventListener);
        myBlockStatusQuery.removeEventListener(my_inbox_listener);
        otherBlockStatusQuery.removeEventListener(other_inbox_listener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        uploadingImageId = "none";
        SendTypingIndicator(false);
        queryGetchat.removeEventListener(eventListener);
        myBlockStatusQuery.removeEventListener(my_inbox_listener);
        otherBlockStatusQuery.removeEventListener(other_inbox_listener);
    }

    public void OpenfullsizeImage(ChatModels item) {
        FullImageFragment see_image_f = new FullImageFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle args = new Bundle();
        args.putSerializable("image_url", item.getPic_url());
        args.putSerializable("chat_id", item.getChat_id());
        see_image_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Chat_F, see_image_f).commit();

    }

    GifItem gif_item;
    final ArrayList<String> url_list = new ArrayList<>();
    RecyclerView gips_list;

    public void GetGipy() {
        String url = (modelAbout.getGhiphy());
        GPHApi client = new GPHApiClient(url);
        url_list.clear();
        gips_list = getView.findViewById(R.id.gif_recylerview);
        gips_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        gif_item = new GifItem(context, url_list, new GifItem.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                SendGif(item);
                slideDown();
            }
        });
        gips_list.setAdapter(gif_item);

        client.trending(MediaType.gif, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                if (result == null) {
                } else {
                    if (result.getData() != null) {
                        for (Media gif : result.getData()) {

                            url_list.add(gif.getId());
                        }
                        gif_item.notifyDataSetChanged();

                    } else {
                        Log.e("giphy error", "No results found");
                    }
                }
            }
        });
    }

    public void searchGif(String search) {
        String url = (modelAbout.getGhiphy());
        GPHApi client = new GPHApiClient(url);
        client.search(search, MediaType.gif, null, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                if (result == null) {
                } else {
                    if (result.getData() != null) {
                        url_list.clear();
                        for (Media gif : result.getData()) {
                            url_list.add(gif.getId());
                            gif_item.notifyDataSetChanged();
                        }
                        gips_list.smoothScrollToPosition(0);

                    } else {
                        Log.e("giphy error", "No results found");
                    }
                }
            }
        });
    }

    public void slideUp() {
        message.setHint("Search Gifs");
        uploadGifBtn.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
        gifLayout.setVisibility(View.VISIBLE);
        sendbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_searchbtn));
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                getView.getHeight(),
                0);
        animate.setDuration(400);
        animate.setFillAfter(true);
        gifLayout.startAnimation(animate);
    }

    public void slideDown() {
        message.setHint("Send Message here...");
        message.setText("");
        uploadGifBtn.setColorFilter(context.getResources().getColor(R.color.gray));
        sendbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send));
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                getView.getHeight());
        animate.setDuration(400);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gifLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        gifLayout.startAnimation(animate);
    }

    public void closecameralayout() {
        camerabtn.setColorFilter(context.getResources().getColor(R.color.gray));
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                getView.getHeight());
        animate.setDuration(700);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llcamera.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        llcamera.startAnimation(animate);
    }


    public void opencameralayout() {
        llcamera.setVisibility(View.VISIBLE);
        camerabtn.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                getView.getHeight(),
                0);
        animate.setDuration(700);
        animate.setFillAfter(true);
        llcamera.startAnimation(animate);
    }

    public static void SendPushNotification(DatabaseReference rootref,
                                            String name, String message,
                                            String picture, String token,
                                            String receiverid, String senderid) {

        Map<String, String> notimap = new HashMap<>();
        notimap.put("name", name);
        notimap.put("message", message);
        notimap.put("picture", picture);
        notimap.put("token", token);
        notimap.put("receiverid", receiverid);
        notimap.put("action_type", "message");
        rootref.child("notifications").child(senderid).push().setValue(notimap);


    }


    public void block() {
        new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to block this user?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Block_user();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void unblock() {
        new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to unblock this user?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        UnBlock_user();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void getSetting() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.SETTINGAPP, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        getData(respo);
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

    public void getData(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                JSONObject userdata = msg.getJSONObject(0);

                modelAbout.setGhiphy(userdata.getString("ghipy_api"));

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

}
