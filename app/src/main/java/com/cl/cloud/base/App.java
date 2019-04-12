package com.cl.cloud.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.cl.cloud.service.KeepAliveService;
import com.cl.cloud.service.KeepJobService;

/**
 * Created by computer on 2018/10/30.
 */

public class App extends Application {

    public static Context sContext;

    public static Context getContext(){
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getApplicationContext();
        sContext.startService(new Intent(sContext, KeepAliveService.class));
        sContext.startService(new Intent(sContext, KeepJobService.class));
        KeepAliveManager.getInstance().registerAliveReceiver(sContext);
    }

}
