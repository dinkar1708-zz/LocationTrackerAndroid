package dpm.location.tracker;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * utility class
 */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    /**
     * schedule job once
     *
     * @param context
     * @param cls
     */
    public static void scheduleJob(Context context, Class<?> cls) {
        ComponentName serviceComponent = new ComponentName(context, cls);

        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
//        builder.setPeriodic(5 * 1000);
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

        jobScheduler.schedule(builder.build());
    }

    /**
     * schedule job always in every 5 seconds, can be parametrized time - ie. pass time in parameter
     *
     * @param context
     * @param cls
     */
    public static void scheduleJob1(Context context, Class<?> cls) {
        ComponentName serviceComponent = new ComponentName(context, cls);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setPeriodic(5 * 1000);
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

}
