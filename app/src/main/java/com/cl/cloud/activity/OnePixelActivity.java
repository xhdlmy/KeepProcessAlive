package com.cl.cloud.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.cl.cloud.base.KeepAliveManager;

/*
    进程保活方案：1px Activity 可在屏幕锁屏时进程优先级由4提升为优先级0-2。
    适用场景：本方案主要解决第三方应用及系统管理工具在检测到锁屏事件后一段时间（一般为5分钟以内）内会杀死后台进程，已达到省电的目的问题。
    适用版本：适用于所有的 Android 版本。
 */

public class OnePixelActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        KeepAliveManager.getInstance().registerOnePixelActivity(this);
        Log.i(KeepAliveManager.TAG, "OnePixelActivity onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(KeepAliveManager.TAG, "OnePixelActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(KeepAliveManager.TAG, "OnePixelActivity onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(KeepAliveManager.TAG, "OnePixelActivity onDestroy");
    }
}
