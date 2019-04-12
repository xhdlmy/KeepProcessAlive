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
            int jobid = 1;
            JobInfo.Builder builder = new JobInfo.Builder(jobid, new ComponentName(getPackageName(), KeepJobService.class.getName()));
            builder.setPeriodic(MILL_SECOND_FIVE);
            builder.setPersisted(true);
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if(jobScheduler == null) return;
            int errorCode = jobScheduler.schedule(builder.build());
            LogUtils.i(TAG, "jobScheduler errorCode:" + errorCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
