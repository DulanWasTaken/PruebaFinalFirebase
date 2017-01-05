package es.udc.tfg.pruebafinalfirebase;

import android.*;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseBackgroundListeners extends Service implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private String TAG = "MYSERVICE";
    private boolean running = false;
    private boolean bound = false;
    private boolean locationEnabled = false;
    private ArrayList<Request> pendingRequests;

    private final IBinder mBinder = (IBinder) new LocalBinder();
    private OnServiceInteractionListener mListener;
    private SharedPreferences pref;

    private DatabaseReference ref;
    private ChildEventListener listener;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleLocateApiClient;

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
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        running = true;
        pendingRequests = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        mGoogleLocateApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleLocateApiClient.connect();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mAuth = firebaseAuth;
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.d(TAG, "ENTRO AQUÍ");
                    listener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Request request = dataSnapshot.getValue(Request.class);
                            Log.d(TAG, "adding request " + dataSnapshot);
                            pendingRequests.add(request);
                            if (bound && mListener != null)
                                mListener.onRequestReceived(request.getId());
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(android.R.drawable.ic_menu_share)
                                            .setContentTitle("My notification")
                                            .setContentText("new request " + dataSnapshot.getValue().toString());
                            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            mNotifyMgr.notify(1, mBuilder.build());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            Request request = dataSnapshot.getValue(Request.class);
                            pendingRequests.remove(request);
                            if (bound && mListener != null)
                                mListener.onRequestRemoved();
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            ref = FirebaseDatabase.getInstance().getReference().child("requests").child(user.getEmail() + user.getPhoneNumber() + user.getKey());
                            Log.d(TAG, "ref to listen: " + ref.toString() + " LISTENER: " + listener.toString());
                            ref.addChildEventListener(listener);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand... pendingRequests: " + pendingRequests);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        running = false;
        mGoogleLocateApiClient.disconnect();
        super.onDestroy();
    }

    public ArrayList<Request> getPendingRequests() {
        Log.d(TAG, "getRequests: " + pendingRequests);
        return pendingRequests;
    }

    public int registerClient(Context context) {
        mListener = (OnServiceInteractionListener) context;
        return pendingRequests.size();
    }

    public boolean enableLocation() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleLocateApiClient, mLocationRequest, this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("locationEnabled",true);
        editor.commit();
        return true;
    }

    public boolean disableLocation(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleLocateApiClient, this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("locationEnabled",false);
        editor.commit();
        return false;
    }

    public interface OnServiceInteractionListener{
        public void onRequestReceived(String requestId);
        public void onRequestRemoved();
        public void onLocationChanged(es.udc.tfg.pruebafinalfirebase.Location location, String userId);
    }

    /*************************** GOOGLE LOCATION METHODS *********************************/

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        pref = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Log.d(TAG,"AAKSDLJFLSKA    LOCATION ENABLED: "+pref.getBoolean("locationEnabled",false));
        if(pref.getBoolean("locationEnabled",false))
            enableLocation();
        else
            disableLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        es.udc.tfg.pruebafinalfirebase.Location myLocation = new es.udc.tfg.pruebafinalfirebase.Location(location.getLatitude(),location.getLongitude(),location.getAccuracy());
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("location").setValue(myLocation);
        mListener.onLocationChanged(myLocation,mAuth.getCurrentUser().getUid());
    }
}
