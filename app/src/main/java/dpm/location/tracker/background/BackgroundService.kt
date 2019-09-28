package dpm.location.tracker.background

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import dpm.location.tracker.LocationUpdatesComponent
import dpm.location.tracker.R
import dpm.location.tracker.foreground.ForegroundServiceActivity.Companion.MESSENGER_INTENT_KEY

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * helper methods.
 */
class BackgroundService : IntentService("BackgroundService"), LocationUpdatesComponent.ILocationProvider {

    private var locationUpdatesComponent: LocationUpdatesComponent? = null
    private var mActivityMessenger: Messenger? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate ")

        locationUpdatesComponent = LocationUpdatesComponent(this)
        locationUpdatesComponent!!.onCreate(this)

        val notification = createNotification()
        startForeground(1, notification)
    }

    // this makes service running continuously,,commenting this start command method service runs only once
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand Service started....")
        if (intent != null) {
            mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY)
        }

        if (intent != null) {
            val action = intent.action
            Log.i(TAG, "onStartCommand action $action")
            when (action) {
                ServiceStatus.START.name -> startService()
                ServiceStatus.STOP.name -> stopService()
            }
        }
        return START_STICKY
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.i(TAG, "onHandleIntent $intent")
    }

    /**
     * send message by using messenger
     *
     * @param messageID
     */
    private fun sendMessage(messageID: Int, location: Location) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the BackgroundLocationActivity calls startService() with the callback in the Intent.
        if (mActivityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.")
            return
        }
        val m = Message.obtain()
        m.what = messageID
        m.obj = location
        try {
            mActivityMessenger!!.send(m)
            Toast.makeText(applicationContext, "$location", Toast.LENGTH_SHORT).show()
        } catch (e: RemoteException) {
            Log.e(TAG, "Error passing service object back to activity.")
        }

    }

    override fun onLocationUpdate(location: Location?) {
        location?.let { sendMessage(LOCATION_MESSAGE, it) }
    }

    private fun createNotification(): Notification {
        val notificationChannelId = BackgroundService::class.java.simpleName

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                    notificationChannelId,
                    "Notifications Channel",
                    NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Background location service is getting location..."
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, BackgroundLocationActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
        ) else Notification.Builder(this)

        return builder
                .setContentTitle("Background Service")
                .setContentText("Background location service is getting location...")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Ticker text")
                .build()
    }

    private fun startService() {
        //hey request for location updates
        locationUpdatesComponent?.onStart()
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
    }

    private fun stopService() {
        locationUpdatesComponent?.onStop()
        stopForeground(true)
        stopSelf()
    }

    companion object {
        private val TAG = BackgroundService::class.java.simpleName
        const val LOCATION_MESSAGE = 999
    }
}
