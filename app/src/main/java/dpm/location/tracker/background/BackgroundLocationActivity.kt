package dpm.location.tracker.background

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.util.Log
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import dpm.location.tracker.R
import kotlinx.android.synthetic.main.activity_background_location.*
import java.text.DateFormat
import java.util.*

class BackgroundLocationActivity : AppCompatActivity() {

    // as google doc says
    // Handler for incoming messages from the service.
    private var mHandler: IncomingMessageHandler? = null
    private lateinit var locationMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_location)

        locationMsg = findViewById(R.id.location)

        start.setOnClickListener {
            // check permission and then start
            requestPermissions()

        }

        stop.setOnClickListener {
            stopService()
        }

        mHandler = IncomingMessageHandler()

        startService()
    }

    /**
     * start service
     */
    private fun startService() {
        startStopServiceCommand(ServiceStatus.START)

    }

    /**
     * stop service
     */
    private fun stopService() {
        startStopServiceCommand(ServiceStatus.STOP)

    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            // Request permission
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult grantResults $grantResults")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
                finish()
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // can be schedule in this way also
                //  Utils.scheduleJob(this, ForegroundService.class);
                //doing this way to communicate via messenger
                // Start service and provide it a way to communicate with this class.
                startService()
            } else {
                // Permission denied.
                finish()
            }
        }
    }

    internal inner class IncomingMessageHandler : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(TAG, "handleMessage..." + msg.toString())

            super.handleMessage(msg)

            when (msg.what) {
                BackgroundService.LOCATION_MESSAGE -> {
                    val obj = msg.obj as Location
                    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                    locationMsg.text = "LAT :  " + obj.latitude + "\nLNG : " + obj.longitude + "\n\n" + obj.toString() + " \n\n\nLast updated- " + currentDateTimeString
//                    Toast.makeText(applicationContext, locationMsg.text.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startStopServiceCommand(action: ServiceStatus) {
        Intent(this, BackgroundService::class.java).also {
            it.action = action.name
            if (action == ServiceStatus.START) {
                val messengerIncoming = Messenger(mHandler)
                it.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
                return
            }
            startService(it)
        }
    }

    companion object {

        private val TAG = BackgroundLocationActivity::class.java.simpleName

        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        const val MESSENGER_INTENT_KEY = "msg-intent-key"
    }


}
