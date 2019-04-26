package com.xhd.alive.app;


import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import com.xhd.alive.R;
import com.xhd.alive.component.AccountService;
import com.xhd.alive.component.KeepAliveService;
import com.xhd.alive.component.KeepJobService;
import com.xhd.alive.component.OnePixelActivity;

public class KeepAliveManager {

    private static KeepAliveManager instance;

    private KeepAliveManager() {
    }

    public static KeepAliveManager getInstance() {
        if(instance == null){
            synchronized (KeepAliveManager.class){
                if(instance == null){
                    instance = new KeepAliveManager();
                }
            }
        }
        return instance;
    }

    /*
    1. 利用锁屏广播 OnePixelActivity 提升 oom_adj 至 0-2
       适用场景：Android在检测到锁屏事件后一段时间内会杀死后台进程，已达到省电的目的问题。
     */
    
    private OnePixelActivity mOnePixelActivity;

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
     2. 利用 Notification 设置成前台服务将 oom_adj 从 4 提高到 1,2
     */

    private KeepAliveService mKeepAliveService;

    public static int NOTIFICATION_ID = 1; // 不能设置为0，不然不会显示通知

    public void startServiceForeground(KeepAliveService service){
        mKeepAliveService = service;

        Notification.Builder builder = new Notification.Builder(service);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText("")
                .setTicker("");
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            service.startForeground(NOTIFICATION_ID, notification);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
             // Android4.3-7.0，通知可以通过NotificationHideService来做到无感知
            Intent innerIntent = new Intent(service, NotificationHideService.class);
            service.startService(innerIntent);
            service.startForeground(NOTIFICATION_ID, notification);
        } else {
            // 现状：Android7.1以上通知栏会出现一条"正在运行"的通知消息
            service.startForeground(NOTIFICATION_ID, notification);
        }
    }

    public void stopServiceForeground() {
        if(mKeepAliveService != null) mKeepAliveService.stopForeground(true);
    }

    public static class NotificationHideService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(KeepAliveManager.NOTIFICATION_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    /*
   3. 利用 JobScheduler 对 Android5.0以上部分机型拉活
     */
    public void startJobScheduler(Context context){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            context.startService(new Intent(context, KeepJobService.class));
        }
    }

    /*
    4. 利用账户同步功能对 Android8.0以下部分机型拉活
     */
    public void syncAccount(Context context){
        context.startService(new Intent(context, AccountService.class));
    }

}
