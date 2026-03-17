package com.muhammadzain.mzkwallpaper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.muhammadzain.mzkwallpaper.Activities.PreviewAllActivity;
import com.muhammadzain.mzkwallpaper.ModelClass.ModelImage;
import com.muhammadzain.mzkwallpaper.R;

import java.util.List;

public class AllImagesAdapter extends RecyclerView.Adapter<AllImagesAdapter.AllImagesViewHolder> {

    private Context mContext;
    private List<ModelImage> list;

    public static final int AD_TYPE = 1;
    public static final int CONTENT_TYPE = 2;

    public AllImagesAdapter(Context mContext, List<ModelImage> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public AllImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//
//        AdView adView;
        View view = null;
        final AllImagesViewHolder viewHolder;
//
//        if (viewType == AD_TYPE) {
//                adView = new AdView(mContext);
//                adView.setAdSize(AdSize.BANNER);
//
//                adView.setAdUnitId(mContext.getString(R.string.banner_ad_id));
//
//                float density = mContext.getResources().getDisplayMetrics().density;
//                int height = Math.round(AdSize.BANNER.getHeight() * density);
//                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, height);
//                adView.setLayoutParams(params);
//                //ad request
//                AdRequest request = new AdRequest.Builder().build();
//                adView.loadAd(request);
//                viewHolder = new AllImagesViewHolder(adView);
//                return viewHolder;
//
//        } else {

            view = LayoutInflater.from(mContext).inflate(R.layout.item_images, parent, false);
            viewHolder = new AllImagesViewHolder(view);
            return viewHolder;

//        }


    }

    @Override
    public void onBindViewHolder(@NonNull AllImagesViewHolder holder, final int position) {

//        int whichData = getItemViewType(position);
//        if (whichData==CONTENT_TYPE){

            final ModelImage listmodel=list.get(position);

            holder.mProgressBar.setVisibility(View.VISIBLE);
                   Glide.with(mContext)
                           .load(listmodel.getMediumurl())
                           .listener(new RequestListener<Drawable>() {
                               @Override
                               public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                   holder.mProgressBar.setVisibility(View.GONE);
                                   return false;
                               }

                               @Override
                               public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                   holder.mProgressBar.setVisibility(View.GONE);
                                   return false;
                               }
                           })
                           .into(holder.mImageview);



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, PreviewAllActivity.class);
                    intent.putExtra("list",listmodel);
                    intent.putExtra("text",listmodel.getWallpaperUrl());
                    mContext.startActivity(intent);
                }
            });

//        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//
//        if ((position+1) % 10 == 0 && (position+1) != 1)
//            return AD_TYPE;
//        return CONTENT_TYPE;
////        if(position>0) {
////            if (position % 3 == 0) {
////                return AD_TYPE;
////            } else {
////                return CONTENT_TYPE;
////            }
////        }
////        return position;
//    }

    public static class AllImagesViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageview;
        private ProgressBar mProgressBar;

        public AllImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageview=itemView.findViewById(R.id.image);
            mProgressBar=itemView.findViewById(R.id.progress_all);


        }
    }




}
