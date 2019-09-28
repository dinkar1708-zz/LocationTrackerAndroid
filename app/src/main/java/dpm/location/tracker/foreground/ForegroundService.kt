package dpm.location.tracker.foreground

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import dpm.location.tracker.LocationUpdatesComponent
import dpm.location.tracker.foreground.ForegroundServiceActivity.Companion.MESSENGER_INTENT_KEY


/**
 * location update service continues to running and getting location information
 */
class ForegroundService : Service(), LocationUpdatesComponent.ILocationProvider {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private var mActivityMessenger: Messenger? = null

    private lateinit var locationUpdatesComponent: LocationUpdatesComponent

    override fun onCreate() {
        Log.i(TAG, "created...............")

        locationUpdatesComponent = LocationUpdatesComponent(this)

        locationUpdatesComponent.onCreate(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand Service started")
        if (intent != null) {
            mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY)
        }
        //hey request for location updates
        locationUpdatesComponent.onStart()

        return START_STICKY
    }

    override fun onRebind(intent: Intent) {
        // Called when a client (BackgroundLocationActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.i(TAG, "Last client unbound from service")

        return true // Ensures onRebind() is called when a client re-binds.
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy....")
        locationUpdatesComponent.onStop()
    }

    /**
     * send message by using messenger
     *
     * @param messageID
     */
    private fun sendMessage(messageID: Int, location: Location?) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the BackgroundLocationActivity calls startService() with the callback in the Intent.
        Log.d(TAG, "Location - $location")
        Toast.makeText(applicationContext, "Location - $location", Toast.LENGTH_LONG).show()
        if (mActivityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.")
            return
        }
        val m = Message.obtain()
        m.what = messageID
        m.obj = location
        try {
            mActivityMessenger?.send(m)
        } catch (e: RemoteException) {
            Log.e(TAG, "Error passing service object back to activity.")
        }

    }

    override fun onLocationUpdate(location: Location?) {
        sendMessage(LOCATION_MESSAGE, location)
    }

    companion object {

        private val TAG = ForegroundService::class.java.simpleName
        const val LOCATION_MESSAGE = 9999
    }
}