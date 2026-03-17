package com.muhammadzain.mzkwallpaper.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.github.chrisbanes.photoview.PhotoView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.thesoftparrot.jkpm.JKPMActivity;
import com.muhammadzain.mzkwallpaper.ModelClass.ModelImage;
import com.muhammadzain.mzkwallpaper.R;
import com.muhammadzain.mzkwallpaper.databinding.ActivityPreviewAllBinding;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PreviewAllActivity extends AppCompatActivity {
    private String orignalurl = "";
    private PhotoView photoView;
    private ActivityPreviewAllBinding mBinding;
    private ModelImage list;
    private BottomSheetDialog bottomSheetDialog;
    private DownloadManager mDownloadManager;
    private BitmapAsyncTask mAsyncTask;

    private static final String TAG = "PreviewAllActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPreviewAllBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(mBinding.getRoot());
//        ContentResolver resolver = getContentResolver();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MZK WALLPAPER");
//        String path = String.valueOf(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues));
//        File folder = new File(path);
//        boolean isCreada = folder.exists();
//        if(!isCreada) {
//            folder.mkdirs();
//        }




        photoView=findViewById(R.id.photoviewall);
        mBinding.progressPreview.setVisibility(View.VISIBLE);
        getIntents();
        setclicks();

        setbottomSheet();
    }

    private void setbottomSheet() {

        mBinding.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                bottomSheetDialog=new BottomSheetDialog(PreviewAllActivity.this,R.style.BottomSheetTheme);

                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_persistent, (ViewGroup)findViewById(R.id.bottom_sheet_));

                sheetView.findViewById(R.id.downloadlarge).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bottomSheetDialog.dismiss();
                        mDownloadManager =(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri=Uri.parse(list.getOrignalUrl());
                        DownloadManager.Request request=new DownloadManager.Request(uri);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        mDownloadManager.enqueue(request);


                        Toast.makeText(PreviewAllActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
                    }
                });
                sheetView.findViewById(R.id.download_medium).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bottomSheetDialog.dismiss();

                        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(list.getLarge());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        mDownloadManager.enqueue(request);
                        Toast.makeText(PreviewAllActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();

                    }
                });
                sheetView.findViewById(R.id.download_low).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bottomSheetDialog.dismiss();

                            mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri = Uri.parse(list.getMediumurl());
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            mDownloadManager.enqueue(request);
                            Toast.makeText(PreviewAllActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();


                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });
        mBinding.setAsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog=new BottomSheetDialog(PreviewAllActivity.this,R.style.BottomSheetTheme);
                View sheetsetwallpaperView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_set_as_wallpaper_persistent, (ViewGroup)findViewById(R.id.bottom_sheet_set_as_wallpaper));

                sheetsetwallpaperView.findViewById(R.id.set_as_wallpaper).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WallpaperManager wallpaperManager=WallpaperManager.getInstance(PreviewAllActivity.this);
                        Bitmap bitmap=((BitmapDrawable)mBinding.photoviewall.getDrawable()).getBitmap();

                        try {
                            wallpaperManager.setBitmap(bitmap);
                            Toast.makeText(PreviewAllActivity.this, "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                bottomSheetDialog.setContentView(sheetsetwallpaperView);
                bottomSheetDialog.show();
            }
        });



    }

    private void setclicks() {

        mBinding.icBackClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                finish();


            }
        });
        mBinding.shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Create an ACTION_SEND Intent*/
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                /*This will be the actual content you wish you share.*/
                String shareBody = list.getOrignalUrl();

                intent.setType("text/plain");

                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                /*Fire!*/
                startActivity(Intent.createChooser(intent, shareBody));
            }
        });
    }

    private void getIntents() {

        Intent intent=getIntent();
        if (intent!=null){
            list= (ModelImage) intent.getSerializableExtra("list");
            String text=intent.getStringExtra("text");
            mBinding.textPhoto.setText("Photo Link : "+text);


        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Glide.with(PreviewAllActivity.this)
                        .load(list.getLargex2())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                mBinding.progressPreview.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                mBinding.progressPreview.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(photoView);
            }
        });


    }

    public void Link_Click(View view) {

        String url = list.getWallpaperUrl();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);


    }




//    public void SetWallpaperEvent(View view) {
//        WallpaperManager wallpaperManager=WallpaperManager.getInstance(PreviewAllActivity.this);
//        Bitmap bitmap=((BitmapDrawable)photoView.getDrawable()).getBitmap();
//        try {
//            wallpaperManager.setBitmap(bitmap);
//            Toast.makeText(this, "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}