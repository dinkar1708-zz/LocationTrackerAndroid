package dpm.location.tracker

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*


/**
 * stand alone component for location updates
 */
class LocationUpdatesComponent(private var iLocationProvider: ILocationProvider) {

    private lateinit var mLocationRequest: LocationRequest

    /**
     * Provides access to the Fused Location Provider API.
     */
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null

    /**
     * The current location.
     */
    private var mLocation: Location? = null

    /**
     * create first time to initialize the location components
     *
     * @param context
     */
    fun onCreate(context: Context) {
        Log.i(TAG, "created...............")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                Log.i(TAG, "onCreate...onLocationResult...............loc " + locationResult!!.lastLocation)

                onNewLocation(locationResult.lastLocation)
            }
        }
        // create location request
        createLocationRequest()
        // get last known location
        getLastLocation()
    }

    /**
     * start location updates
     */
    fun onStart() {
        Log.i(TAG, "onStart ")
        //hey request for location updates
        requestLocationUpdates()
    }

    /**
     * remove location updates
     */
    fun onStop() {
        Log.i(TAG, "onStop....")
        removeLocationUpdates()
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    private fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback!!, Looper.getMainLooper())
        } catch (err: Exception) {
            Log.e(TAG, "Lost location permission. Could not request updates. $err")
        }

    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    private fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback!!)
        } catch (err: Exception) {
            //            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. $err")
        }

    }

    /**
     * get last location
     */
    private fun getLastLocation() {
        try {
            mFusedLocationClient.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            mLocation = task.result
                            Log.i(TAG, "getLastLocation " + mLocation!!)
                            //                                Toast.makeText(getApplicationContext(), "" + mLocation, Toast.LENGTH_SHORT).show();
                            onNewLocation(mLocation)
                        } else {
                            Log.w(TAG, "Failed to get location.")
                        }
                    }
        } catch (e: Exception) {
            Log.e(TAG, "Lost location permission.$e")
        }

    }

    private fun onNewLocation(location: Location?) {
        Log.i(TAG, "New location: " + location!!)
        //        Toast.makeText(getApplicationContext(), "onNewLocation " + location, Toast.LENGTH_LONG).show();

        mLocation = location
        this.iLocationProvider.onLocationUpdate(mLocation)
    }

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * implements this interface to get call back of location changes
     */
    interface ILocationProvider {
        fun onLocationUpdate(location: Location?)
    }

    companion object {

        private val TAG = LocationUpdatesComponent::class.java.simpleName

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = (6 * 1000).toLong()

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }
}