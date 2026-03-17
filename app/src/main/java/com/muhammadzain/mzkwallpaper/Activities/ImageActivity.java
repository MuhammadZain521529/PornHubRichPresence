package com.muhammadzain.mzkwallpaper.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.muhammadzain.mzkwallpaper.Adapters.AllImagesAdapter;
import com.muhammadzain.mzkwallpaper.ModelClass.ModelImage;
import com.muhammadzain.mzkwallpaper.R;
import com.muhammadzain.mzkwallpaper.databinding.ActivityImageBinding;
import com.thesoftparrot.jkpm.JKPMActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageActivity extends JKPMActivity {
    private ActivityImageBinding mBinding;
    private ModelImage mList;
    private List<ModelImage> list;
    private String query;
    private AlertDialog.Builder alert;
    private AllImagesAdapter adapter;
    private static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SET_WALLPAPER
    };
    int pagenumber = 1;
    private static final String TAG = "ImageActivity";
    private Boolean isScrolled = false;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int data;
    private int currentitems, totalitems, scrollOutItems;
    private String url = "https://api.pexels.com/v1/curated/?page=" + pagenumber + "&per_page=80";
    private MenuItem item;
    private ProgressDialog mProgressDialog;
    private boolean isAvailable;
    public static final int ITEM_PER_AD = 3;
    private List<Object> recyclerviewitems = new ArrayList<>();

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityImageBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("MyNotification", "MyNotification", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//        FirebaseMessaging.getInstance().subscribeToTopic("general")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = "Successfull";
//                        if (!task.isSuccessful()) {
//                            msg = "Failed";
//                        }
//                        Log.d(TAG, msg);
//                        Toast.makeText(ImageActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });


        if (!areAllPermissionsEnabled(PERMISSION)){
            askRuntimePermissions(PERMISSION);
        }

        setSupportActionBar(mBinding.maintoolbar);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

       mAdView=findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId(String.valueOf(R.string.app_id));

        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setMessage("Please Wait.....");
        mProgressDialog.setCanceledOnTouchOutside(false);
        toggleButtonColor(mBinding.allClick);
        mProgressDialog.dismiss();
        internetAvailableOrNot();
        setupToolbar();
        setClick();
        final GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
             list=new ArrayList<>();
             adapter=new AllImagesAdapter(ImageActivity.this,list);
             mBinding.recyclerviewMain.setAdapter(adapter);
             mBinding.recyclerviewMain.setLayoutManager(gridLayoutManager);
             mBinding.recyclerviewMain.setItemAnimator(null);
//             gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                 @Override
//                 public int getSpanSize(int position) {
//                     switch(adapter.getItemViewType(position)){
//                         case AllImagesAdapter.AD_TYPE:
//                             return 3;
//                         case AllImagesAdapter.CONTENT_TYPE:
//                             return 1;
//                         default:
//                             return -1;
//                     }
//                 }
//             });
        mBinding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               
                mBinding.swiperefresh.setRefreshing(false);
            }
        });

        mBinding.recyclerviewMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolled=true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentitems=gridLayoutManager.getChildCount();
                totalitems=gridLayoutManager.getItemCount();
                scrollOutItems=gridLayoutManager.findFirstVisibleItemPosition();

                if (isScrolled && (currentitems+scrollOutItems==totalitems)){
                    isScrolled=false;
                    mBinding.loadMoreClick.setVisibility(View.VISIBLE);
                    mBinding.loadMoreClick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            internetAvailableOrNot();
                            if (item.getItemId()==R.id.search_image){
                               mBinding.loadMoreClick.setVisibility(View.GONE);
                            }

                                if (mBinding.allClick.isSelected()){
                                    url="https://api.pexels.com/v1/curated/?page="+pagenumber+"&per_page=80";


                                    fetchwallpaper();
                                    mBinding.loadMoreClick.setVisibility(View.GONE);
                                }

                                if (mBinding.wildClick.isSelected()){
                                    url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Wild";


                                    fetchwallpaper();
                                    mBinding.loadMoreClick.setVisibility(View.GONE);

                                }
                            if (mBinding.snowClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Snow";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.loveClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"4k";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.oceanClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Ocean";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.mountainClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Mountain";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.darkClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Dark";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.natureClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Nature";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.amazingClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Amazing";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.coolClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Cool";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.abstractClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Abstract";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.galaxyClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Galaxy";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.flowerClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Flower";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.animalClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Animal";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.buildingClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Building";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.carsClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Cars";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.wildClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Wild";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            } if (mBinding.bikeClick.isSelected()){
                                url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Bike";


                                fetchwallpaper();
                                mBinding.loadMoreClick.setVisibility(View.GONE);

                            }

                        }
                    });
                }
            }
        });
        fetchwallpaper();




    }


    private void internetAvailableOrNot() {

        if (!checkInternet()){

            mProgressDialog.dismiss();
            mBinding.recyclerviewMain.setVisibility(View.GONE);
            mBinding.noInternetTv.setVisibility(View.VISIBLE);
            mBinding.noInternetIv.setVisibility(View.VISIBLE);
            mBinding.retry.setVisibility(View.VISIBLE);
        }else {

           if (checkInternet()){
               mProgressDialog.show();
               mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
               mBinding.noInternetTv.setVisibility(View.GONE);
               mBinding.noInternetIv.setVisibility(View.GONE);
               mBinding.retry.setVisibility(View.GONE);
           }
        }
    }

    private boolean checkInternet() {

        final ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }

        }
        return false;
    }



    private void setupToolbar() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(ImageActivity.this, mBinding.drawerLayout, mBinding.maintoolbar, R.string.app_name, R.string.app_name);
        mBinding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent=new Intent(ImageActivity.this,ImageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.share_click:

                        Intent sendIntent = new Intent();
//                        sendIntent.setAction(Intent.ACTION_SEND);
//                        sendIntent.putExtra(Intent.EXTRA_TEXT,
//                                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                        break;
                    case R.id.privacy_policy:
                        String url = "https://mzkwalllpaper78.blogspot.com/p/privacy-policy.html";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                    case R.id.rating:
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.setData(Uri.parse("market://details?id=" + getPackageName()));
                        startActivity(intent1);

                        break;
                }
                return false;
            }
        });
    }


//    private void setnonClicks() {
//
//
//    }
//
//    public class WrapContentLinearLayoutManager extends GridLayoutManager {
//        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//            super(context, attrs, defStyleAttr, defStyleRes);
//        }
//
//        public WrapContentLinearLayoutManager(Context context, int spanCount) {
//            super(context, spanCount);
//        }
//
//        public WrapContentLinearLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
//            super(context, spanCount, orientation, reverseLayout);
//        }
//
//        //... constructor
//        @Override
//        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//            try {
//                super.onLayoutChildren(recycler, state);
//            } catch (IndexOutOfBoundsException e) {
//                Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens");
//            }
//        }
//    }

    private void setClick() {

        mBinding.retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInternet()){
                    Toast.makeText(ImageActivity.this, "Check Your Network Connection...", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    mBinding.noInternetTv.setVisibility(View.VISIBLE);
                    mBinding.noInternetIv.setVisibility(View.VISIBLE);
                    mBinding.retry.setVisibility(View.VISIBLE);
                }else {

                    if (checkInternet()){
                        mProgressDialog.show();
                        Toast.makeText(ImageActivity.this, "Network Connected", Toast.LENGTH_SHORT).show();
                        list.clear();
                        Intent intent=new Intent(ImageActivity.this,ImageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        mBinding.recyclerviewMain.setVisibility(View.VISIBLE);

                        mBinding.noInternetTv.setVisibility(View.GONE);
                        mBinding.noInternetIv.setVisibility(View.GONE);
                        mBinding.retry.setVisibility(View.GONE);
                    }
                }

            }
        });
        mBinding.allClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!mBinding.allClick.isSelected()){
                    toggleButtonColor(mBinding.allClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url="https://api.pexels.com/v1/curated/?page="+pagenumber+"&per_page=80";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }
            }
        });
        mBinding.wildClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mBinding.wildClick.isSelected()){
                    toggleButtonColor(mBinding.wildClick);
                    item.setVisible(true);
                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Wild";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }
            }
        });
        mBinding.darkClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!mBinding.darkClick.isSelected()){
                    toggleButtonColor(mBinding.darkClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Dark";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }

            }
        });
        mBinding.natureClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.natureClick.isSelected()){
                    toggleButtonColor(mBinding.natureClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+"Nature";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }

            }
        });
        mBinding.amazingClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mBinding.amazingClick.isSelected()) {
                    toggleButtonColor(mBinding.amazingClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Amazing";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }

            }
        });
        mBinding.coolClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.coolClick.isSelected()) {
                    toggleButtonColor(mBinding.coolClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Cool";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }
            }
        });
        mBinding.loveClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.loveClick.isSelected()) {
                    toggleButtonColor(mBinding.loveClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "4k";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }

            }
        });
        mBinding.galaxyClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.galaxyClick.isSelected()) {
                    toggleButtonColor(mBinding.galaxyClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Galaxy";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }

            }
        });
        mBinding.flowerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.flowerClick.isSelected()) {
                    toggleButtonColor(mBinding.flowerClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Flower";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }

            }
        });
        mBinding.animalClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.animalClick.isSelected()) {
                    toggleButtonColor(mBinding.animalClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Animal";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }

            }
        });
        mBinding.abstractClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.abstractClick.isSelected()) {
                    toggleButtonColor(mBinding.abstractClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Abstract";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }
            }
        });
        mBinding.buildingClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.buildingClick.isSelected()) {
                    toggleButtonColor(mBinding.buildingClick);
                    item.setVisible(true);

                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Building";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }

            }
        });
        mBinding.carsClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.carsClick.isSelected()) {
                    toggleButtonColor(mBinding.carsClick);
                    item.setVisible(true);
                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Car";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }

            }
        });
        mBinding.bikeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.bikeClick.isSelected()) {
                    toggleButtonColor(mBinding.bikeClick);
                    item.setVisible(true);
                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Bike";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }


            }
        });
        mBinding.snowClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!mBinding.snowClick.isSelected()) {
                    toggleButtonColor(mBinding.snowClick);
                    item.setVisible(true);
                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Snow";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }


            }
        });
        mBinding.oceanClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.oceanClick.isSelected()) {
                    toggleButtonColor(mBinding.oceanClick);
                    item.setVisible(true);
                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Ocean";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }
            }
        });
        mBinding.mountainClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.mountainClick.isSelected()) {
                    toggleButtonColor(mBinding.mountainClick);
                    item.setVisible(true);
                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    url = "https://api.pexels.com/v1/search/?page=" + pagenumber + "&per_page=80&query=" + "Mountain";
                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();
                }


            }
        });

    }


    private void toggleButtonColor(Button clickedOnButton) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(mBinding.allClick);
        buttons.add(mBinding.wildClick);
        buttons.add(mBinding.snowClick);
        buttons.add(mBinding.oceanClick);
        buttons.add(mBinding.mountainClick);
        buttons.add(mBinding.darkClick);
        buttons.add(mBinding.natureClick);
        buttons.add(mBinding.amazingClick);
        buttons.add(mBinding.coolClick);
        buttons.add(mBinding.loveClick);
        buttons.add(mBinding.abstractClick);
        buttons.add(mBinding.galaxyClick);
        buttons.add(mBinding.flowerClick);
        buttons.add(mBinding.animalClick);
        buttons.add(mBinding.buildingClick);
        buttons.add(mBinding.carsClick);
        buttons.add(mBinding.bikeClick);
        for (Button btn:buttons){
            if (btn.getId()==clickedOnButton.getId()){
                btn.setSelected(true);
                internetAvailableOrNot();
                mBinding.loadMoreClick.setVisibility(View.GONE);
            }else {
                btn.setSelected(false);
            }
        }
    }

    public void fetchwallpaper(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.d(TAG, "onResponse"+jsonObject);
                    JSONArray jsonArray=jsonObject.getJSONArray("photos");

                    int length=jsonArray.length();
                    mProgressDialog.dismiss();
                    for (int i=0;i<length ;i++){
                        JSONObject object=jsonArray.getJSONObject(i);

                        int id =object.getInt("id");
                        String photographerurl=object.getString("photographer_url");
                        String imageurl=object.getString("url");
                        Log.d(TAG, "onResponse:"+id);
                        String photographerdetails=object.getString("photographer");
                        JSONObject objectimages=object.getJSONObject("src");

                        String orignalUrl=objectimages.getString("original");
                        String largex2=objectimages.getString("large2x");
                        String large =objectimages.getString("large");
                        String mediumUrl=objectimages.getString("medium");
                        String smallUrl=objectimages.getString("small");

                        ModelImage modelImage=new ModelImage(id,photographerdetails,photographerurl,imageurl,orignalUrl,large,largex2,mediumUrl,smallUrl);
                        list.add(modelImage);

                    }
                    adapter.notifyDataSetChanged();
                    pagenumber++;

                }catch (JSONException e){

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("Authorization","563492ad6f91700001000001a4b997993c914d82a91f8b517b90f0c2");

                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_images,menu);
         item = menu.findItem(R.id.search_image);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.search_image){
             alert=new AlertDialog.Builder(this);
            final EditText editText=new EditText(this);
            editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            alert.setMessage("Enter Category e.g. Nature");
            alert.setTitle("Search Wallpaper");
            alert.setView(editText);
            alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mProgressDialog.show();
                    mBinding.recyclerviewMain.setVisibility(View.GONE);
                    toggleButtonColor(mBinding.allClick);
                    query=editText.getText().toString();
                    url="https://api.pexels.com/v1/search/?page="+pagenumber+"&per_page=80&query="+query;

                    list.clear();
                    mBinding.recyclerviewMain.setVisibility(View.VISIBLE);
                    fetchwallpaper();

                }
            });

            alert.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPermissionsGranted() {

    }

    @Override
    protected void onPermissionsDenied() {
        showDenialBox();

    }
}