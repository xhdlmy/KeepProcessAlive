package com.cl.cloud.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cl.cloud.service.AccountService;
import com.cl.cloud.service.KeepAliveService;
import com.cl.cloud.service.KeepJobService;

public class App extends Application {

    public static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getApplicationContext();
        sContext.startService(new Intent(sContext, KeepAliveService.class));
        sContext.startService(new Intent(sContext, AccountService.class));
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            sContext.startService(new Intent(sContext, KeepJobService.class));
//        }
    }

}
