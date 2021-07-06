package com.example.takeComplain.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.api_helper.ApiInterface;
import com.example.api_helper.BaseUrl;
import com.example.api_helper.CommonResponseModel;
import com.example.common_class.CommonClass;
import com.example.login.model.User;
import com.example.mbw.R;
import com.example.takeComplain.TakeComplains;
import com.example.takeComplain.model.ComplainModel;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplainService extends Service {

    User user;
    ComplainModel complainModel;
    ComplainInterface complainInterface;

    NotificationManager notificationManager;
    int CHANNELID;

    public ComplainService() {
      /*  this.user=user;
        this.complainModel=complainModel;
        this .complainInterface=complainInterface;*/
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent intent2 = new Intent(this, TakeComplains.class);
            showNotification(this,"Complain upload to server"
                    , "", intent2, new Random().nextInt());


        sentComplain();

        return START_NOT_STICKY;

    }

    public void onDestroy() {
        super.onDestroy();


    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        //    SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(context);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = String.valueOf(reqCode);// The id of the channel.
         CHANNELID = reqCode;// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
               .setAutoCancel(true)
              //  .setProgress(0, 0, true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MBW App";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

      //  Log.d("showNotification", "showNotification: " + reqCode);
    }

    public void sentComplain(){

        complainModel =CommonClass.complainModel;
         user= CommonClass.user;
        BaseUrl.getClient().create(ApiInterface.class).
                complainEntry(user.getApiKey(),user.getId().toString(),complainModel).enqueue(new Callback<CommonResponseModel>() {
            @Override
            public void onResponse(Call<CommonResponseModel> call, Response<CommonResponseModel> response) {

              //  Log.e("requestComplainCar"," : " + response.body());
                if (response.isSuccessful()) {
                    if (!response.body().isError()) {
                     //   Log.e("requestComplainCar222"," : " +response.body().getMsg().toString());


                        Intent intent = new Intent(TakeComplains.RECEIVER_INTENT);
                        intent.putExtra(TakeComplains.RECEIVER_MESSAGE, "" +response.body().getMsg().toString());
                        intent.putExtra(TakeComplains.RECEIVER_ISERROR, false);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                       stopSelf();
                    } else {

                        Intent intent = new Intent(TakeComplains.RECEIVER_INTENT);
                        intent.putExtra(TakeComplains.RECEIVER_MESSAGE, "" +response.body().getMsg().toString());
                        intent.putExtra(TakeComplains.RECEIVER_ISERROR, true);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                        stopSelf();
                    }

                } else {

                    Intent intent = new Intent(TakeComplains.RECEIVER_INTENT);
                    intent.putExtra(TakeComplains.RECEIVER_MESSAGE, "" +response.body().getMsg().toString());
                    intent.putExtra(TakeComplains.RECEIVER_ISERROR, true);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                  stopSelf();
                }
            }

            @Override
            public void onFailure(Call<CommonResponseModel> call, Throwable t) {
               // Log.e("requestComplainCar"," : " + t.toString());
                Intent intent = new Intent(TakeComplains.RECEIVER_INTENT);
                intent.putExtra(TakeComplains.RECEIVER_MESSAGE, "" +t.toString());
                intent.putExtra(TakeComplains.RECEIVER_ISERROR, true);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
              stopSelf();
            }
        });
    }



    public interface ComplainInterface {
        void onComplainSuccess(String msg);

        void onComplainFailed(String error);
    }

}