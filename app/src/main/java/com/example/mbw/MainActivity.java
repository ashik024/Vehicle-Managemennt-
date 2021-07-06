package com.example.mbw;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;


import com.example.api_helper.ApiInterface;
import com.example.api_helper.BaseUrl;
import com.example.createUserCar.AddMoreCar.CuCarAddMoreActivity;
import com.example.createUserCar.CreateUsers;
import com.example.login.LoginActivity;
import com.example.login.model.User;
import com.example.mbw.model.CustomerCarInfoResponse;
import com.example.mbw.model.MainLocalResponseModel;
import com.example.takeComplain.MoreComplain.AddMoreComplainActivity;
import com.example.takeComplain.TakeComplains;
import com.example.utils.Timber;
import com.example.mbw.databinding.ActivityMainBinding;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private FirebaseAnalytics mFirebaseAnalytics;

    ActivityMainBinding mainBinding;
    CardView carEntry, complain,cvAddCars,cvAddMoreComplain;

    Toolbar toolbar;

    Timber timber;
    Realm realm;
    User user;

    ImageView sysncImg;
    TextView  username;

    RotateAnimation rotate = new RotateAnimation(
            0, 360,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
    );

   final Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  Objects.requireNonNull(getSupportActionBar()).hide();

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sysncImg= findViewById(R.id.syncdata);
        username= findViewById(R.id.HeaderCustomername);
        cvAddCars= findViewById(R.id.cvAddCars);
        realm = Realm.getDefaultInstance();
        user = RealmHelper.getUser();

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        timber = new Timber();
        carEntry = findViewById(R.id.cvOrder);
        complain = findViewById(R.id.takeComplain);
        cvAddMoreComplain = findViewById(R.id.cvAddMoreComplain);

        carEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateUsers.class);
         //       Intent intent = new Intent(MainActivity.this, SignatureActivity.class);
                //  Intent intent = new Intent(MainActivity.this, CamActivity.class);
                startActivity(intent);
            }
        });

        sysncImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pullAllData();

            }
        });

        complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TakeComplains.class);
                startActivity(intent);
            }
        });

        cvAddCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, CuCarAddMoreActivity.class);
                startActivity(intent);

            }
        });

        cvAddMoreComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddMoreComplainActivity.class);
                startActivity(intent);

            }
        });


    }


    public void startAnimationFromBackgroundThread() {
        new Thread(){
            @Override
            public void run() {


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mainBinding.spinKit.setVisibility(View.VISIBLE);
                        rotate.setDuration(900);
                        rotate.setRepeatCount(Animation.INFINITE);
                        sysncImg.startAnimation(rotate);
                    }
                });

            }
        }.start();

    }
    private void pullAllData() {

     //   startAnimationFromBackgroundThread();

        mainBinding.spinKit.setVisibility(View.VISIBLE);
        rotate.setDuration(900);
        rotate.setRepeatCount(Animation.INFINITE);
        sysncImg.startAnimation(rotate);


        BaseUrl.getClient().create(ApiInterface.class).getBrandEngineLookUp(user.getApiKey(),user.getId().toString()).enqueue(new Callback<MainLocalResponseModel>() {
            @Override
            public void onResponse(Call<MainLocalResponseModel> call, Response<MainLocalResponseModel> response) {

                if (response.isSuccessful()) {
                    if (!response.body().getError()) {
                        //  timber.e("TAG", "getBrands: " + response.body());

                        if (response.body().getBrands() != null) {
                            RealmHelper.saveBrands(response.body().getBrands());
                        }
                        //  timber.e("TAG", "getBrands: " + response.body().getComplainSubCat().size());

                        if (response.body().getEngineTypes() != null) {
                            RealmHelper.saveEngineTypes(response.body().getEngineTypes());
                        }

                        if (response.body().getModelTypes() != null) {
                            RealmHelper.saveModelType(response.body().getModelTypes());
                        }

                        if (response.body().getLookUp() != null) {
                            RealmHelper.saveLookUp(response.body().getLookUp());
                        }

                        if (response.body().getComplainHeads() != null) {
                            RealmHelper.saveComplainHeads(response.body().getComplainHeads());
                        }
                        if (response.body().getComplainCat() != null) {
                            RealmHelper.saveComplainCat(response.body().getComplainCat());
                        }
                        if (response.body().getComplainSubCat() != null) {
                            RealmHelper.saveComplainSubCat(response.body().getComplainSubCat());
                        }
                        if (response.body().getPriorities() != null) {
                            RealmHelper.savePriorities(response.body().getPriorities());
                        }


                        BaseUrl.getClient().create(ApiInterface.class).getComplainSrvcCustomerCar(user.getApiKey(),user.getId().toString()).enqueue(new Callback<CustomerCarInfoResponse>() {
                            @Override
                            public void onResponse(Call<CustomerCarInfoResponse> call, Response<CustomerCarInfoResponse> response) {
                                if (response.body().getCustomerCarInfos() != null) {
                                    RealmHelper.saveCustomerCarInfo(response.body().getCustomerCarInfos() ,response.body().getComplainCustomerInfomation());
                                    mainBinding.spinKit.setVisibility(View.GONE);
                                    rotate.cancel();
                                    Toast.makeText(MainActivity.this, "Data Synced", Toast.LENGTH_SHORT).show();
//                                    sysncImg.clearAnimation();
                                }
                            }

                            @Override
                            public void onFailure(Call<CustomerCarInfoResponse> call, Throwable t) {
                                mainBinding.spinKit.setVisibility(View.GONE);
                                rotate.cancel();
                                Toast.makeText(MainActivity.this, "Data Sync failed", Toast.LENGTH_SHORT).show();
//                                sysncImg.clearAnimation();
                            }
                        });


                    } else {
                        mainBinding.spinKit.setVisibility(View.GONE);
                        rotate.cancel();

//                        sysncImg.clearAnimation();
                    }

                } else {
                    mainBinding.spinKit.setVisibility(View.GONE);
                    rotate.cancel();

//                    sysncImg.clearAnimation();
                }




//                sysncImg.clearAnimation();
            }

            @Override
            public void onFailure(Call<MainLocalResponseModel> call, Throwable t) {
                mainBinding.spinKit.setVisibility(View.GONE);
                rotate.cancel();

//                sysncImg.clearAnimation();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout:
                //  timber.e("TAG", "item: 2");
//                RealmHelper.formatRealm();
                RealmHelper.deleteUser();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                break;
            case R.id.action_sync:
//                mainBinding.spinKit.setVisibility(View.VISIBLE);
                //   timber.e("TAG", "item: 3");
                pullAllData();
                break;
            //   case R.id.action_software:
            //  timber.e("TAG", "item: 4");
//                Intent software = new Intent(MainActivity.this, SoftwareAccess.class);
//                startActivity(software);
            //    break;
            //  case R.id.action_notification:
            // timber.e("TAG", "item: 5");
//                Intent notification = new Intent(MainActivity.this, NotificationActivity.class);
//                startActivity(notification);
            //  break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);

        //   getMenuInflater().inflate(R.menu.menu_main, menu);
        //  return true;
    }
}