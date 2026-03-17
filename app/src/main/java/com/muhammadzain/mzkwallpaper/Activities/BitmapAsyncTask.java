package com.muhammadzain.mzkwallpaper.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BitmapAsyncTask extends AsyncTask<String,Void, Bitmap> {
    private BitmapOperationListner mBitmapoperationListner;

    public BitmapAsyncTask(BitmapOperationListner mBitmapoperationListner) {
        this.mBitmapoperationListner = mBitmapoperationListner;
    }

    @Override
    protected Bitmap doInBackground(String... paths) {
        String imagepath=paths[0];

        File file=new File(imagepath);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig=Bitmap.Config.ARGB_8888;
        Bitmap bitmap=null;
        try {
            bitmap=BitmapFactory.decodeStream(new FileInputStream(file),null,options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (mBitmapoperationListner!=null){
            mBitmapoperationListner.onBitmapCreated(bitmap);
        }


    }
    public interface BitmapOperationListner{
        void onBitmapCreated(Bitmap createdbitmap);
    }
}
