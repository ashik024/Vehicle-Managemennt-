package com.example.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.createUserCar.CreateUsers;
import com.example.mbw.R;

public class ToastCustom {

    public static void ToastMsg(Context context,String msg){
        Toast toast = Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT);
        View view = toast.getView();
        //To change the Background of Toast #2C2C2C
      //  view.setBackgroundColor(Color.parseColor("#575656"));
        view.setBackgroundResource(R.drawable.toast_drawable);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        //Shadow of the Of the Text Color
        text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        text.setTextColor(Color.WHITE);
//        text.setTextSize(Integer.valueOf(getResources().getString(R.string.text_size)));
        toast.show();
    }
}
