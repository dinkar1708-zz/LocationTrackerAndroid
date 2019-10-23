package dpm.location.tracker.foreground

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import dpm.location.tracker.R
import dpm.location.tracker.background.BackgroundLocationActivity
import kotlinx.android.synthetic.main.activity_foreground_service.*
import java.text.DateFormat
import java.util.*


class ForegroundServiceActivity : AppCompatActivity() {

    private lateinit var locationMsg: TextView

    // as google doc says
    // Handler for incoming messages from the service.
    private var mHandler: IncomingMessageHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foreground_service)

        locationMsg = findViewById(R.id.location)

        mHandler = IncomingMessageHandler()

        requestPermissions()

        background_demo.setOnClickListener {
            startActivity(Intent(this, BackgroundLocationActivity::class.java))
            finish()
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult")
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
                val startServiceIntent = Intent(this, ForegroundService::class.java)
                val messengerIncoming = Messenger(mHandler)
                startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
                startService(startServiceIntent)
            } else {
                // Permission denied.
                finish()
            }
        }
    }

    override fun onDestroy() {
        mHandler = null
        super.onDestroy()
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            // Request permission
            ActivityCompat.requestPermissions(this@ForegroundServiceActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@ForegroundServiceActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }


    internal inner class IncomingMessageHandler : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(TAG, "handleMessage...$msg")

            super.handleMessage(msg)

            when (msg.what) {
                ForegroundService.LOCATION_MESSAGE -> {
                    val obj = msg.obj as Location
                    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                    locationMsg.text = "LAT :  " + obj.latitude + "\nLNG : " + obj.longitude + "\n\n" + obj.toString() + " \n\n\nLast updated- " + currentDateTimeString
                }
            }
        }
    }

    companion object {
        private val TAG = ForegroundServiceActivity::class.java.simpleName

        /**
         * Code used in requesting runtime permissions.
         */
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

        const val MESSENGER_INTENT_KEY = "msg-intent-key"
    }
}
