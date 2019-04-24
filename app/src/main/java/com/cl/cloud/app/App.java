package com.cl.cloud.app;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.cl.cloud.base.KeepAliveManager;
import com.cl.cloud.service.KeepAliveService;
import com.cl.cloud.service.KeepJobService;
import com.cl.cloud.util.LogUtils;

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
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            startJobSheduler();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startJobSheduler() {
        try {
            // 1. 创建 JobInfo
            int jobid = 1;
            JobInfo.Builder builder = new JobInfo.Builder(jobid, new ComponentName(getPackageName(), KeepJobService.class.getName()));
//            builder.setPeriodic(5000);
//            builder.setPersisted(true);
            JobInfo jobInfo = builder.build();
            // 2. 获取 JobScheduler
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if(jobScheduler == null) return;
            // 3. 将任务交由系统去调度
            int errorCode = jobScheduler.schedule(jobInfo);
            LogUtils.i("KeepJobService", "jobScheduler errorCode:" + errorCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
