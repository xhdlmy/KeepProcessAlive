package com.cl.cloud.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.cl.cloud.util.LogUtils;

/**
 * Created by computer on 2019/4/12.
 */
/*
    Android5.0 以后系统对 Native 进程等加强了管理
    Android5.0 以后
    由系统统一管理和调度
    JobScheduler是framework层里用来安排各种各样的将要执行在app自己进程里的任务的机制。
    我们需要创建各种Job的描述类JobInfo，并且通过JobScheduler传递给系统。
    当我们描述的条件或者标准满足了，系统将执行app的JobService。
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class KeepJobService extends JobService {

    private static final String TAG = KeepJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, KeepAliveService.class);
        startService(intent);

        startJobSheduler();
    }

    private static final int MILL_SECOND_FIVE = 5000;

    public void startJobSheduler() {
        try {
            // 1. 创建 JobInfo
            int jobid = 1;
            JobInfo.Builder builder = new JobInfo.Builder(jobid, new ComponentName(getPackageName(), KeepJobService.class.getName()));
            builder.setPeriodic(MILL_SECOND_FIVE);
            builder.setPersisted(true);
            JobInfo jobInfo = builder.build();
            // 2. 获取 JobScheduler
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if(jobScheduler == null) return;
            // 3. 将任务交由系统去调度
            int errorCode = jobScheduler.schedule(jobInfo);
            LogUtils.i(TAG, "jobScheduler errorCode:" + errorCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
