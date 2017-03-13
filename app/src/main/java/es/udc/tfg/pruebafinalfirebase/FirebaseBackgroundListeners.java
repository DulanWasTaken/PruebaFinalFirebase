package es.udc.tfg.pruebafinalfirebase;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Group.Group;
import es.udc.tfg.pruebafinalfirebase.Messages.Message;
import es.udc.tfg.pruebafinalfirebase.Notifications.Request;

public class FirebaseBackgroundListeners extends Service implements DBManager.DBManagerInteractions,LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private String TAG = "MYSERVICE";
    public static final int RC_CHECK_SETTINGS = 33333;
    public static final int LOCATION_ENABLED_NOTIF = 1111;
    public static final int REQUEST_RECEIVED_NOTIF = 2222;
    private boolean running = false;
    private boolean bound = false;
    private boolean locationEnabled = false;

    private final IBinder mBinder = (IBinder) new LocalBinder();
    private OnServiceInteractionListener mListener;
    private SharedPreferences pref;
    private NotificationManager mNotifyMgr;
    private GoogleApiClient mGoogleLocateApiClient;
    private DBManager dbManager = DBManager.getInstance();

    public FirebaseBackgroundListeners() {
    }

    public class LocalBinder extends Binder {
        FirebaseBackgroundListeners getService() {
            // Return this instance of LocalService so clients can call public methods
            return FirebaseBackgroundListeners.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        bound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnBind");
        bound = false;
        dbManager.bindDBManager(FirebaseBackgroundListeners.this);
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        pref = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        pref.edit().putBoolean("serviceRunning",true).commit();

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mGoogleLocateApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleLocateApiClient.connect();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG, "onStartCommand... pendingRequests: " + pendingRequests);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        running = false;
        pref.edit().putBoolean("serviceRunning",false).commit();
        mGoogleLocateApiClient.disconnect();
        super.onDestroy();
    }

    public void registerClient(Context context) {
        Log.d(TAG,"onBind");
        mListener = (OnServiceInteractionListener) context;
        bound=true;

        mNotifyMgr.cancel(LOCATION_ENABLED_NOTIF);
    }

    public void disconnectService(){
        Log.d(TAG,"onUnBind");

        if(pref.getBoolean("locationEnabled",false)) {
            NotificationCompat.Builder locEnabledNotifBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                            .setContentTitle("Location")
                            .setContentText("You are sharing your location in background");
            mNotifyMgr.notify(LOCATION_ENABLED_NOTIF, locEnabledNotifBuilder.build());
        }

        bound=false;
    }

    public boolean enableLocation() {

        final LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleLocateApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates settingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        if (ActivityCompat.checkSelfPermission(FirebaseBackgroundListeners.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FirebaseBackgroundListeners.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleLocateApiClient, mLocationRequest, FirebaseBackgroundListeners.this);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("locationEnabled",true);
                        editor.commit();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        if (bound)
                            mListener.startResolution(status);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });

        return true;
    }

    public boolean disableLocation(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleLocateApiClient, this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("locationEnabled",false);
        editor.commit();
        dbManager.disableMyLocation();
        if(mListener!=null)
            mListener.onMyLocationChanged(new es.udc.tfg.pruebafinalfirebase.Location(0,0,0,false));
        return false;
    }

    public interface OnServiceInteractionListener{
        public void onMyLocationChanged(es.udc.tfg.pruebafinalfirebase.Location location);
        public void startResolution(Status status);
    }

    /*************************** GOOGLE LOCATION METHODS *********************************/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"LOCATION ENABLED: "+pref.getBoolean("locationEnabled",false));
        if(pref.getBoolean("locationEnabled",false))
            enableLocation();
        else
            disableLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"connection suspended: "+i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"connection failed: "+connectionResult);
    }

    @Override
    public void onLocationChanged(Location location) {
        es.udc.tfg.pruebafinalfirebase.Location myLocation = new es.udc.tfg.pruebafinalfirebase.Location(location.getLatitude(),location.getLongitude(),location.getAccuracy(),true);
        dbManager.setLocation(myLocation);
        if(bound)
            mListener.onMyLocationChanged(myLocation);
    }

    /*************************** DB MANAGER METHODS *********************************/

    @Override
    public void signedIn() {

    }

    @Override
    public void signedOut() {

    }

    @Override
    public void groupChanged(Group group) {

    }

    @Override
    public void locationReceived(String userId,String nick, String groupId, es.udc.tfg.pruebafinalfirebase.Location location) {

    }

    @Override
    public void messageReceived(String groupId, Message msg) {

    }

    @Override
    public void requestReceived(Request request) {

    }

    @Override
    public void requestRemoved(){

    }

    @Override
    public void noProfileAvailable() {

    }

    @Override
    public void initMsgList(String groupId, ArrayList<Message> messages) {

    }
}
