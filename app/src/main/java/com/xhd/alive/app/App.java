package com.xhd.alive.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.xhd.alive.component.AccountService;
import com.xhd.alive.component.KeepAliveService;
import com.xhd.alive.component.KeepJobService;

public class App extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getApplicationContext();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Android8.0 同步机制手动同步后报错：
            // Not allowed to start service Intent { cmp=com.xhd.alive/.service.KeepAliveService }: app is in background uid
            // Android8.0 后台执行限制（为了省电），不允许在后台直接 startService 服务
            sContext.startForegroundService(new Intent(sContext, KeepAliveService.class));
        }else{
            sContext.startService(new Intent(sContext, KeepAliveService.class));
        }
        sContext.startService(new Intent(sContext, AccountService.class));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            sContext.startService(new Intent(sContext, KeepJobService.class));
        }
    }

}
