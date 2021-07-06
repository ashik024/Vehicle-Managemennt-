package com.example.splash_page;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.example.api_helper.ApiInterface;
import com.example.api_helper.BaseUrl;
import com.example.common_class.CommonClass;
import com.example.login.LoginActivity;
import com.example.login.model.User;
import com.example.mbw.R;
import com.google.firebase.FirebaseApp;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class SplashPage extends AppCompatActivity {

    int SPLASH_DISPLAY_LENGTH = 1500;
    int temp=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);

//        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);
        Animation animation = AnimationUtils.loadAnimation(SplashPage.this, R.anim.blink);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(700);
        final ImageView splash = findViewById(R.id.ivGlove);
        splash.startAnimation(animation);

        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {

                if(isConnected)
                {
                    if(temp==0) {
                        getVersion();
                       // Log.e("Version", "onConnectivityChanged: true");
                        temp=1;
                    }
                }
                else
                {
                     // Log.e("Version", "onConnectivityChanged: false");
                    gotoNextPage();
                }
            }
        });
    }

    private void gotoNextPage() {

        final User user = RealmHelper.getUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
             if (user != null) {
                    Intent intent = new Intent(SplashPage.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashPage.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    void getVersion()
    {
        // Log.e("Version", "onConnectivityChanged: seje bose ace");
        final TextView textView=findViewById(R.id.tvVersion);


      BaseUrl.getClient().create(ApiInterface.class).getVersion().enqueue(new Callback<Version>() {
            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {
                if(response.isSuccessful())
                {


                    double versionCode=0.0;
                    try {
                        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        versionCode = Double.valueOf(packageInfo.versionName);
                        CommonClass.version=String.valueOf(versionCode);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                  //  Log.e("Version", "onConnectivityChanged: "+response.body()+"  "+versionCode);

                    if(!response.body().getError())
                    {
                        //   Log.e("Version", "onConnectivityChanged: "+response.body().getVersionNo()+"  "+versionCode);
                        textView.setText(""+response.body().getVersionNo());
                        if(response.body().getVersionNo()!=versionCode)
                        {
                            LayoutInflater li = LayoutInflater.from(SplashPage.this);
                            View promptsView = li.inflate(R.layout.save_update_alert_dialoge, null);
                            android.app.AlertDialog.Builder alertDialogBuilder = new
                                    android.app.AlertDialog.Builder(SplashPage.this);
                            alertDialogBuilder.setView(promptsView);
                            alertDialogBuilder.setCancelable(true);
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                        else
                        {
                            gotoNextPage();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<Version> call, Throwable t) {
                gotoNextPage();
            }
        });
    }

}
