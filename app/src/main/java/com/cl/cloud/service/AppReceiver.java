package com.cl.cloud.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cl.cloud.base.KeepAliveManager;

/*
    Android8.0 targetSDK>=26 禁止了隐式广播的静态注册(显式广播静态注册没问题，隐式广播动态注册页没问题)
        静态注册：应用尚未启动便可接收相应的系统广播
        动态注册：应用启动后通过 context.registerReceiver() 来注册监听广播
        隐式广播：通过 IntentFilter 来筛选 （系统广播大多是隐式广播）
        显式广播：显式指明具体广播组件
        ps:Android7.0 只是静态注册网络变化监听无效，Android8.0 更上一层楼，至保留了极个别的（如开机广播等）可以监听，其他都监听无效。

 */

public class AppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null) return;
        String action = intent.getAction();
        if(action == null) return;
        switch (action) {
            // 锁屏广播
            case Intent.ACTION_SCREEN_OFF:
                KeepAliveManager.getInstance().startOnePixelActivity(context);
                break;
            // 激活设备广播（在解锁屏幕密码后）
            case Intent.ACTION_USER_PRESENT:
                KeepAliveManager.getInstance().finishOnePixelActivity();
                break;
            default:

                break;
        }
    }

}
