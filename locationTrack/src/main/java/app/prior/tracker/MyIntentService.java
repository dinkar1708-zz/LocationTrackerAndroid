package app.prior.tracker;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import static app.prior.tracker.MainActivity.MESSENGER_INTENT_KEY;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class MyIntentService extends IntentService implements LocationUpdatesComponent.ILocationProvider {
    private static final String TAG = MyIntentService.class.getSimpleName();
    public static final int LOCATION_MESSAGE = 999;

    private LocationUpdatesComponent locationUpdatesComponent;
    private Messenger mActivityMessenger;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate ");

        locationUpdatesComponent = new LocationUpdatesComponent(this);
        locationUpdatesComponent.onCreate(this);
    }

    // this makes service running continuously,,commenting this start command method service runs only once
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand Service started....");
        if (intent != null) {
            mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY);
        }

        //hey request for location updates
        locationUpdatesComponent.onStart();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy...");

        locationUpdatesComponent.onStop();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent" + intent);
        if (intent != null) {
            final String action = intent.getAction();
        }
    }

    /**
     * send message by using messenger
     *
     * @param messageID
     */
    private void sendMessage(int messageID, Location location) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.
        if (mActivityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.");
            return;
        }
        Message m = Message.obtain();
        m.what = messageID;
        m.obj = location;
        try {
            mActivityMessenger.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object back to activity.");
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        sendMessage(LOCATION_MESSAGE, location);
    }
}
