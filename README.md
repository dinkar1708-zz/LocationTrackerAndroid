
### Note need to handle the background limit - https://developer.android.com/about/versions/oreo/background-location-limits.html

### background package-
- runs always ie. in background thread, even when activity destroyed

### foreground package-
- runs foreground service - till activity is running

### LocationUpdatesComponent.kt - get the location
## WHAT [Get continuous location in android using background Service]
FusedLocationProviderClient.java - google client

#### DESCRIPTION
#### Permissions-
ACCESS_COARSE_LOCATION
ACCESS_FINE_LOCATION
FOREGROUND_SERVICE

#### Why FusedLocationProviderClient
Google’s Fused Location Services API
Most recommended API by everyone. The android official document recommends to use this way
FusedLocationProviderClient is inside LocationServices class and uses the feature
```
mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
```
https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API
Location updates should always be done using the FusedLocationProviderClient leveraging the LocationServices.API as shown above.
Do not use the older Location APIs which are much less reliable.
Even when using the correct FusedLocationApi, there are a lot of things that can go wrong.

##### Why not use GoogleApiClient?
   Reference - https://android-developers.googleblog.com/2017/06/reduce-friction-with-new-location-apis.html

#### OUTPUT
 Location continuously retrieved in service
 Location continuously sending on activity

#### FUTURE SCOPE
    Gps tracker
    Location tracker
    Foot print tracking
    all above typs of application can be created
    users current location can be shown on google map


##### References
https://developer.android.com/training/location/receive-location-updates.html
https://github.com/googlesamples/android-play-location
https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API

#### TODO
- for android 10 handling is remaining...
If your app targets Android 10 or higher, you must declare the ACCESS_BACKGROUND_LOCATION permission in your app's manifest file and receive user permission in order to receive regular location updates while your app is in the background.
https://developer.android.com/training/location/receive-location-updates.html

