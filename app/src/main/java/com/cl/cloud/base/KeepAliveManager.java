package com.cl.cloud.base;


import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Keep;

import com.cl.cloud.R;
import com.cl.cloud.activity.OnePixelActivity;
import com.cl.cloud.service.KeepAliveService;
import com.cl.cloud.util.ServiceUtils;

import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;

public class KeepAliveManager {

    private static KeepAliveManager instance;
    public static final String TAG = KeepAliveManager.class.getSimpleName();

    private KeepAliveManager() {
    }

    public static synchronized KeepAliveManager getInstance() {
        if(instance == null){
            instance = new KeepAliveManager();
        }
        return instance;
    }

    private BroadcastReceiver mAliveReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null) return;
            String action = intent.getAction();
            if(action == null) return;
            switch (action) {
                case RECEIVE_BOOT_COMPLETED: // 开机广播
                case ConnectivityManager.CONNECTIVITY_ACTION: // 网络变化广播
                case Intent.ACTION_SCREEN_OFF: // 屏幕亮灭
                case Intent.ACTION_SCREEN_ON: // 屏幕亮灭
                    boolean isRunning = ServiceUtils.isServiceRunning(context, KeepAliveService.class.getName());
                    if(!isRunning) ServiceUtils.startService(context, KeepAliveService.class);
                    break;
            }
        }
    };

    /*
    OnePixelActivity 相关
     */

    private OnePixelActivity mOnePixelActivity;

    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
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
            }
        }
    };

    public void registerOnePixelActivity(OnePixelActivity activity){
        mOnePixelActivity = activity;
    }

    public void startOnePixelActivity(Context context){
        Intent intent = new Intent(context, OnePixelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void finishOnePixelActivity(){
        if (mOnePixelActivity != null) mOnePixelActivity.finish();
    }

    public void registerScreenReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(mScreenReceiver, filter);
    }

    public void unregisterScreenReceiver(Context context) {
        context.unregisterReceiver(mScreenReceiver);
    }

    /*
     设置前台服务
     */

    private KeepAliveService mKeepAliveService;

    // 不能设置为0，不然不会显示通知
    public static int FOREGROUND_SERVICE_ID = 1; // 保证两个 Service 的一致即可

    public void registerKeepAliveService(KeepAliveService service){
        mKeepAliveService = service;
    }

    // oom_adj 从 4 提高到 2，测试机型，华为
    public void setServiceForeground(KeepAliveService service){
        Notification.Builder builder = new Notification.Builder(service);
        builder.setSmallIcon(R.mipmap.ic_launcher) // 没有这个，就不会出现通知，进程优先级貌似也不会变成 2
                .setContentTitle("ForegroundService")
                .setContentText("KeepAlive")
                .setTicker("Just don't remove it!");
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            service.startForeground(FOREGROUND_SERVICE_ID, notification);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
             // Android4.3-7.0，通知可以通过NotificationHideService来做到无感知
            Intent innerIntent = new Intent(service, NotificationHideService.class);
            service.startService(innerIntent);
            service.startForeground(FOREGROUND_SERVICE_ID, notification);
        } else {
            // 现状：Android7.1以上通知栏会出现一条"正在运行"的通知消息
            service.startForeground(FOREGROUND_SERVICE_ID, notification);
        }
    }

    public void stopServiceForeground() {
        mKeepAliveService.stopForeground(true);
    }

    // 这个也需要注册到清单文件中，否则 onStartCommand 方法不会调用
    public static class NotificationHideService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(KeepAliveManager.FOREGROUND_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
