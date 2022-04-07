package co.za.wedwise.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import co.za.wedwise.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FullImageFragment extends Fragment {

    View getView;
    Context context;
    ImageButton sharebtn;
    Button savebtn, savebtn2;

    ImageView singleImage, closeGallery;
    String imageUrl,chat_id;
    ProgressBar progressBar;

    ProgressDialog progressDialog;
    DownloadRequest prDownloader;

    File direct;
    File fullpath;
    int width,height;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_fullimage, container, false);
        context = getContext();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         height = displayMetrics.heightPixels;
         width = displayMetrics.widthPixels;
        imageUrl = getArguments().getString("image_url");
        chat_id = getArguments().getString("chat_id");
        savebtn2 = getView.findViewById(R.id.savebtn2);

        closeGallery = getView.findViewById(R.id.close_gallery);
        closeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait");

        PRDownloader.initialize(getActivity().getApplicationContext());

        fullpath = new File(Environment.getExternalStorageDirectory() +"/Kencan/"+chat_id+".jpg");

        savebtn= getView.findViewById(R.id.savebtn);
        if(fullpath.exists()){
            savebtn.setVisibility(View.GONE);
            savebtn2.setVisibility(View.VISIBLE);
        }

        direct = new File(Environment.getExternalStorageDirectory() +"/Kencan/");

        prDownloader = PRDownloader.download(imageUrl, direct.getPath(), chat_id+".jpg")
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
                });


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Savepicture(false);
            }
        });
        progressBar = getView.findViewById(R.id.progress);
        singleImage = getView.findViewById(R.id.single_image);

        if(fullpath.exists()){
            Uri uri= Uri.parse(fullpath.getAbsolutePath());
            singleImage.setImageURI(uri);
        }else {
            progressBar.setVisibility(View.VISIBLE);
            Picasso.with(context).load(imageUrl).placeholder(R.drawable.image_placeholder)
                    .into(singleImage, new Callback() {
                        @Override
                        public void onSuccess() {

                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            // TODO Auto-generated method stub
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }

        sharebtn= getView.findViewById(R.id.sharebtn);
        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePicture();
            }
        });


        return getView;
    }

    public void SharePicture(){
        if(Checkstoragepermision()) {





            Uri bitmapuri;
            if(fullpath.exists()){
//                bitmapuri= Uri.parse(fullpath.getAbsolutePath());
//                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//                intent.setType("image/jpg");
//                intent.putExtra(Intent.EXTRA_STREAM, bitmapuri);
//                startActivity(Intent.createChooser(intent, ""));

                Bitmap b = BitmapFactory.decodeFile(fullpath.getAbsolutePath());

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getActivity().getApplicationContext().getContentResolver(), b, "Title", null);
                Uri imageUri =  Uri.parse(path);
                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(share, "Select"));

            }
            else {
                Savepicture(true);
            }

        }
    }

    public void Savepicture(final boolean isfromshare){
        if(Checkstoragepermision()) {

            final File direct = new File(Environment.getExternalStorageDirectory() + "/DCIM/Kencan/");
            progressDialog.show();
            prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.parse(direct.getPath() + chat_id + ".jpg"));
                    context.sendBroadcast(intent);
                    progressDialog.dismiss();
                    if (isfromshare) {
                        SharePicture();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                //set title
                                .setTitle("Image Saved")
                                //set message
                                .setMessage(fullpath.getAbsolutePath())
                                //set negative button
                                .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                    }
                }

                @Override
                public void onError(Error error) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();

                }


            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, "Click Again", Toast.LENGTH_LONG).show();
        }
    }

    public boolean Checkstoragepermision(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }else {

            return true;
        }
    }


}


