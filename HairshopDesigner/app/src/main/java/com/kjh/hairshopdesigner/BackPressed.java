package com.kjh.hairshopdesigner;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class BackPressed {

    private Activity activity;
    private Toast toast;
    int num = 3;

    public BackPressed(Activity context) {
        this.activity = context;
    }
    public void onBackPressed() {

        if(num != 3) {
            activity.finish();
        } else {
            toast.makeText(activity, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
        }

        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {

            sendEmptyMessageDelayed(0, 1000);

            if(num > 0) {
                --num;
            } else {
                num = 3;
                handler.removeMessages(0);
            }
        }
    };
}
