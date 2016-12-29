package es.udc.tfg.pruebafinalfirebase;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseBackgroundListeners extends Service {

    private String TAG = "MYSERVICE";
    private boolean running = false;
    private boolean bound = false;
    private ArrayList<Request> pendingRequests;

    private final IBinder mBinder = (IBinder) new LocalBinder();

    private DatabaseReference ref;
    ChildEventListener listener;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth mAuth;

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
        Log.d(TAG,"onBind");
        bound=true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnBind");
        bound = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG,"onCreate");
        running = true;
        pendingRequests = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mAuth = firebaseAuth;
                if (firebaseAuth.getCurrentUser()!= null){
                    Log.d(TAG,"ENTRO AQUÍ");
                    listener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Request request = dataSnapshot.getValue(Request.class);
                            Log.d(TAG,"adding request "+dataSnapshot);
                            pendingRequests.add(request);
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
                            ref = FirebaseDatabase.getInstance().getReference().child("requests").child(user.getEmail()+user.getPhoneNumber()+user.getKey());
                            Log.d(TAG,"ref to listen: "+ref.toString()+" LISTENER: "+listener.toString());
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
        Log.d(TAG,"onStartCommand... pendingRequests: "+pendingRequests);
        return START_STICKY;
    }


/*    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        running = false;
        if(ref!=null)
            ref.removeEventListener(listener);
        if(mAuth!=null)
            mAuth.removeAuthStateListener(mAuthListener);
        super.onDestroy();
    }*/

    public ArrayList<Request> getPendingRequests(){
        Log.d(TAG,"getRequests: "+pendingRequests);
        return pendingRequests;
    }
}
