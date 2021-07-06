package com.example.utils;

import android.content.Context;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.example.mbw.R;

public class EmptyCheckEditText {
    
    public static boolean isEmpty(EditText editText, String msg, Context context){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.shake_error);

        if (editText.getText().toString().trim().equals("") || editText.getText().toString().trim().length() == 0) {
            editText.setError("PLease Type Car Number");
            editText.getParent().requestChildFocus(editText, editText);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.startAnimation(animation);
                }
            }, 1000);

            return false;
        }
        return true;
        
    }
}
