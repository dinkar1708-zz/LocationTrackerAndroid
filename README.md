
## WHAT [Get continuous location in android using background Service]
App module - API - LocationUpdatesService.java - min API - Android 5.0	21	LOLLIPOP - since job service arises in this api
GpsTracker module - min API 15
FusedLocationProviderClient.java - google client
#### DESCRIPTION
#### Permissions-
ACCESS_COARSE_LOCATION
ACCESS_FINE_LOCATION
#### Why JobScheduler
JobScheduler is guaranteed to get your job done, but since it operates at the system level.
https://medium.com/google-developers/scheduling-jobs-like-a-pro-with-jobscheduler-286ef8510129
JobScheduler is becoming the go-to answer for performing background work in Android. Android Nougat introduced several background optimizations,
for which JobScheduler is the best practice solution. So, if you haven’t already, it’s time to jump on the JobScheduler train.
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

