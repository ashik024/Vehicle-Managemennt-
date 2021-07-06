package com.example.utils;

import android.util.Log;

import com.example.mbw.BuildConfig;


public class Timber {


   public void d(String TAG,String value){
       if (BuildConfig.DEBUG) {
           Log.d(TAG, value);
       }
   }

   public void e(String TAG,String value){
       if (BuildConfig.DEBUG) {
           Log.e(TAG, value);
       }
   }

   public void i(String TAG,String value){
       if (BuildConfig.DEBUG) {
           Log.i(TAG, value);
       }
   }


}
