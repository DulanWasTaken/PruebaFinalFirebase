package es.udc.tfg.pruebafinalfirebase;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
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
import es.udc.tfg.pruebafinalfirebase.InterestPoint.InterestPoint;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.Point;
import es.udc.tfg.pruebafinalfirebase.Messages.Message;
import es.udc.tfg.pruebafinalfirebase.Notifications.Request;

public class mService extends Service implements DBManager.DBManagerInteractions,LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public static final String TAG = "MYSERVICE";
    public static final int RC_CHECK_SETTINGS = 33333;
    public static final int LOCATION_ENABLED_NOTIF = 1111;
    public static final int REQUEST_RECEIVED_NOTIF = 2222;
    public static final int NOTIF_ID_MSG_RECEIVED = 1234;
    public static final int NOTIF_ID_REQ_RECEIVED = 9874;
    public static final int NEW_MSG_CODE = 5678;
    public static final int NEW_REQ_CODE = 7985;
    private boolean running = false;
    private boolean bound = false;
    private boolean locationEnabled = false;

    private final IBinder mBinder = (IBinder) new LocalBinder();
    private OnServiceInteractionListener mListener;
    private SharedPreferences pref;
    private long lastTimeForeground = 0;
    private NotificationManager mNotifyMgr;
    private SharedPreferences appPreferences;
    private GoogleApiClient mGoogleLocateApiClient;
    private DBManager dbManager = DBManager.getInstance();
    private ArrayList<Message> pendingMsg = new ArrayList<>();

    public mService() {
    }

    public class LocalBinder extends Binder {
        mService getService() {
            // Return this instance of LocalService so clients can call public methods
            return mService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        bound = true;
        pendingMsg.clear();
        mNotifyMgr.cancel(NOTIF_ID_MSG_RECEIVED);
        mNotifyMgr.cancel(NOTIF_ID_REQ_RECEIVED);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnBind");
        bound = false;
        dbManager.bindDBManager(mService.this,DBManager.MODE_APPEND);
        long aux = System.currentTimeMillis();
        pref.edit().putLong("lastTimeForeground",aux).commit();
        lastTimeForeground = aux;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        appPreferences = PreferenceManager.getDefaultSharedPreferences(mService.this);
        pref = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        pref.edit().putBoolean("serviceRunning",true).commit();
        lastTimeForeground = pref.getLong("lastTimeForeground",0);

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mGoogleLocateApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleLocateApiClient.connect();

        if(!dbManager.getDbManagerListenerContext().equals(MainActivity.TAG))
            dbManager.bindDBManager(mService.this,DBManager.MODE_APPEND);
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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePI);
        super.onTaskRemoved(rootIntent);
    }

    public void registerClient(Context context) {
        Log.d(TAG,"onBind");
        mListener = (OnServiceInteractionListener) context;
        bound=true;

        pendingMsg.clear();
        mNotifyMgr.cancel(NOTIF_ID_MSG_RECEIVED);
        mNotifyMgr.cancel(NOTIF_ID_REQ_RECEIVED);
    }

    public void disconnectService(){
        Log.d(TAG,"onUnBind");

        if(pref.getBoolean("locationEnabled",false)) {
            Intent msgIntent = new Intent(this, MainActivity.class);
            PendingIntent msgPIntent = PendingIntent.getActivity(this, NEW_MSG_CODE, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder locEnabledNotifBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                            .setContentTitle("Location")
                            .setContentText("You are sharing your location in background")
                            .setContentIntent(msgPIntent);
            mNotifyMgr.notify(LOCATION_ENABLED_NOTIF, locEnabledNotifBuilder.build());
        }

        bound=false;
    }

    public boolean enableLocation() {
        Log.d(TAG,"ENABLE LOCATION");
        int i;
        String prior = appPreferences.getString(SettingsFragment.KEY_GPS,"");
        if(prior.equals(getString(R.string.preference_high_accuracy))){
            i = LocationRequest.PRIORITY_HIGH_ACCURACY;
        }else if(prior.equals(getString(R.string.preference_balance_accuracy))){
            i = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        }else if(prior.equals(getString(R.string.preference_low_power))){
            i = LocationRequest.PRIORITY_LOW_POWER;
        } else {
            return false;
        }
        final LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(i);

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
                        if (ActivityCompat.checkSelfPermission(mService.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mService.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleLocateApiClient, mLocationRequest, mService.this);
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
            mListener.onMyLocationChanged(new es.udc.tfg.pruebafinalfirebase.Location(0,0,0,0,false));
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
        Log.d(TAG,"gps changing position...  bearing = "+location.getBearing());
        es.udc.tfg.pruebafinalfirebase.Location myLocation = new es.udc.tfg.pruebafinalfirebase.Location(location.getLatitude(),location.getLongitude(),location.getAccuracy(),location.getBearing(),true);
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
        lastTimeForeground = pref.getLong("lastTimeForeground",0);
        if(msg.getTime()>lastTimeForeground){

            pendingMsg.add(msg);
            Intent msgIntent = new Intent(this, MainActivity.class);
            PendingIntent msgPIntent = PendingIntent.getActivity(this, NEW_MSG_CODE, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder mBuilder = new Notification.Builder(this) // builder notification
                    .setSmallIcon(R.drawable.ic_notif_wru) // Icon to show in the Status Bar
                    .setContentTitle("Message received") // Title to show in the Status Bar
                    .setContentIntent(msgPIntent);
            Notification.InboxStyle is = new Notification.InboxStyle();
            for(Message m : pendingMsg){
                is.addLine("["+dbManager.findGroupById(groupId).getName()+"]"+m.getSender().getNick()+": "+m.getMsg());
            }
            String summ = pendingMsg.size()>1? "new messages":"new message";
            is.setSummaryText(pendingMsg.size()+" "+summ);
            mBuilder.setStyle(is);
            if(!bound)
                mNotifyMgr.notify(NOTIF_ID_MSG_RECEIVED,mBuilder.build());

        }
    }

    @Override
    public void requestReceived(Request request) {
        Intent requestIntent = new Intent(this, MainActivity.class);
        PendingIntent requestPIntent = PendingIntent.getActivity(this, NEW_REQ_CODE, requestIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder mBuilder = new Notification.Builder(this) // builder notification
                .setSmallIcon(R.mipmap.main_ic_launch) // Icon to show in the Status Bar
                .setContentTitle("Invitation received") // Title to show in the Status Bar
                .setContentIntent(requestPIntent);
        Notification.InboxStyle is = new Notification.InboxStyle();
        for(Request r : DBManager.pendingRequests){
            is.addLine("Invitation to group ["+dbManager.findGroupByRequest(r.getIdGroup()).getName()+"]");
        }
        String summ = DBManager.pendingRequests.size()>1? "new requests":"new request";
        is.setSummaryText(DBManager.pendingRequests.size()+" "+summ);
        mBuilder.setStyle(is);
        mNotifyMgr.notify(NOTIF_ID_REQ_RECEIVED,mBuilder.build());
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

    @Override
    public void updateFilter(){

    }

    @Override
    public void initInterestPoint(InterestPoint interestPoint, String userId, String ipId) {

    }

    @Override
    public void interestPointAdded(InterestPoint ip) {

    }

    @Override
    public void interestPointRemoved(InterestPoint ip) {

    }

    @Override
    public void destinationPointAdded(Point p, String groupId) {

    }

    @Override
    public void destinationPointChanged(Point p, String groupId) {

    }

    @Override
    public void destinationPointRemoved(Point p, String groupId) {

    }
}
