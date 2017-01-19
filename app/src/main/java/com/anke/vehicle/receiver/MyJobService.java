package com.anke.vehicle.receiver;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.anke.vehicle.activity.MainActivity;

/**
 * Created by Administrator on 2017/1/13 0013.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    @Override
    public void onCreate() {

        super.onCreate();
            Intent dialogIntent = new Intent(getBaseContext(), MainActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialogIntent.addCategory(Intent.CATEGORY_HOME);
            getApplication().startActivity(dialogIntent);
            startJobSheduler();
    }

    public void startJobSheduler() {
        try {
            int id = 1;
            JobInfo.Builder builder = new JobInfo.Builder(id,
                    new ComponentName(getPackageName(), MyJobService.class.getName()));
            builder.setPeriodic(1000 * 30);  //间隔500毫秒调用onStartJob函数， 500只是为了验证
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            int ret = jobScheduler.schedule(builder.build());
//             Android24版本才有scheduleAsPackage方法， 期待中
//Class clz = Class.forName("android.app.job.JobScheduler");
//Method[] methods = clz.getMethods();
//Method method = clz.getMethod("scheduleAsPackage", JobInfo.class , String.class, Integer.class, String.class);
//Object obj = method.invoke(jobScheduler, builder.build(), "com.brycegao.autostart", "brycegao", "test");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
//        Log.e("brycegao", "onStartJob alive");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
//        Log.e("brycegao", "onStopJob");
        return false;
    }
}
