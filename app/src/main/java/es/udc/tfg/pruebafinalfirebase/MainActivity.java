package es.udc.tfg.pruebafinalfirebase;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import es.udc.tfg.pruebafinalfirebase.EditGroupFragment.EditGroupFragment;
import es.udc.tfg.pruebafinalfirebase.EditGroupFragment.GroupMemberRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.EditGroupFragment.GroupsRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.EditGroupFragment.Groups_fragment;
import es.udc.tfg.pruebafinalfirebase.filterFragment.FilterRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.filterFragment.Filter_fragment;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.MultiPickContactActivity;
import es.udc.tfg.pruebafinalfirebase.notificationsFragment.NotifRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.notificationsFragment.Notifications_fragment;

public class MainActivity extends AppCompatActivity implements GroupMemberRecyclerViewAdapter.OnGroupMemberAdapterInteractionListener,EditGroupFragment.OnEditGroupFragmentInteractionListener,GoogleMap.OnMapLongClickListener,GroupsRecyclerViewAdapter.OnGroupsAdapterInteractionListener,Groups_fragment.OnGroupsFragmentInteractionListener,Filter_fragment.OnFilterFragmentInteractionListener,FilterRecyclerViewAdapter.OnFilterAdapterInteractionListener,GoogleMap.OnMapLoadedCallback,OnMapReadyCallback,FirebaseBackgroundListeners.OnServiceInteractionListener,Notifications_fragment.OnNotifFragmentInteractionListener,NotifRecyclerViewAdapter.OnNotifAdapterInteractionListener {

    private String TAG = "MainActiv";
    public static final String NOTIF_FRAGMENT_TAG = "NOTIF_FRAGMENT_TAG";
    public static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT_TAG";
    public static final String GROUPS_FRAGMENT_TAG = "GROUPS_FRAGMENT_TAG";
    public static final String FILTER_FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";
    public static final String EDIT_GROUP_FRAGMENT_TAG = "EDIT_GROUP_FRAGMENT_TAG";
    public static final int RC_SIGN_IN = 777;
    public static final int RC_PHONE_CONTACTS = 888;
    public static final int RC_EMAIL_CONTACTS = 999;
    public static final int RC_KEY_CONTACTS = 6;
    public static final int RC_CHECK_SETTINGS = 666;
    public static final int PERMISSION_REQUEST_READ_CONTACTS = 333;
    public static final int PERMISSION_REQUEST_LOCATION = 222;
    public boolean initListenersFinished = false;
    public boolean myLocationEnabled = false;
    public boolean mapLoaded=false;
    public boolean myProfileCreated = false;

    public ArrayList<String> myActiveGroups = new ArrayList<>();
    public ArrayList<String> myFilteredGroups = new ArrayList<>();
    public HashMap<String,Marker> markersHM = new HashMap<String,Marker>();

    private RelativeLayout main_content;
    private FloatingActionButton locationFab,autoZoomFab;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private MenuItem menuItemLog,menuItemShare,menuItemNotif,menuItemFilter;
    private Button notifications;
    private Menu menu;
    private Dialog pb;
    private ActionBar ab;

    public FragmentManager fragmentManager;
    public SharedPreferences pref;

    private FirebaseBackgroundListeners mService;
    private ServiceConnection mConnection;
    private boolean mBound;

    private GoogleApiClient mGoogleAuthApiClient;
    private GoogleSignInOptions gso;
    private GoogleMap mGoogleMap;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private User mProfile;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    public DatabaseReference myProfileRef;
    public DatabaseReference myRequestsRef;
    public DatabaseReference publicIdsRef;
    public DatabaseReference groupsRef;
    private ValueEventListener nameGroupListener;
    private ValueEventListener stateGroupListener;
    private ChildEventListener membersGroupListener;
    private ValueEventListener locationValueListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /************************ PANTALLA DE CARGA ************************************/
        pb = new Dialog(this, android.R.style.Theme_Black);
        View view = LayoutInflater.from(this).inflate(R.layout.progress_bar, null);
        pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pb.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pb.setContentView(view);
        pb.setCancelable(false);
        pb.show();
        /************************ INICILIZAR GOOGLE API ********************************/
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleAuthApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG,"google api client conection failed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppInvite.API)
                .build();

        /*********************** INICILIZAR LAS VARIABLES FIREBASE **************************/
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        publicIdsRef = database.getReference().child("publicIds");
        groupsRef = database.getReference().child("groups");
        /************************** INICIALIZAR OTRAS VARIABLES ***************************/
        fragmentManager = getSupportFragmentManager();
        pref = getSharedPreferences("MYSERVICE", Context.MODE_PRIVATE);
        myLocationEnabled = pref.getBoolean("locationEnabled",false);
        myProfileCreated = pref.getBoolean("profileCreated",false);

        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                FirebaseBackgroundListeners.LocalBinder binder = (FirebaseBackgroundListeners.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                if(notifications!=null)
                    notifications.setText(mService.registerClient(MainActivity.this)+"");

                locationFab.setEnabled(true);
                if(mBound && initListenersFinished)
                    enableButtons();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mService.disconnectService();
                locationFab.setEnabled(false);
                mBound = false;
            }
        };
        /**************************** INICILIZAR LAS VIEWS ********************************/
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setTitle("W'U");

        locationFab = (FloatingActionButton) findViewById(R.id.location_fab);
        autoZoomFab = (FloatingActionButton) findViewById(R.id.auto_view_fab);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        main_content = (RelativeLayout) findViewById(R.id.main_content);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        /*********************** INICILIZAR LOS ONCLICKLISTENERS **************************/

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                SupportMapFragment mapFragment = (SupportMapFragment)fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG);
                switch (menuItem.getItemId()){
                    case R.id.drawer_map:
                        setMapFragmentActionBar();
                        if(fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG)==null){
                            SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.map_fragment_content, mMapFragment,MAP_FRAGMENT_TAG);
                            fragmentTransaction.commit();
                            mMapFragment.getMapAsync(MainActivity.this);

                            Toast.makeText(MainActivity.this,"Map Fragment",Toast.LENGTH_SHORT).show();
                        }else if(fragmentManager.findFragmentByTag(GROUPS_FRAGMENT_TAG)!=null){
                            Fragment groupFrag = fragmentManager.findFragmentByTag(GROUPS_FRAGMENT_TAG);
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.remove(groupFrag);
                            fragmentTransaction.commit();
                        }
                        mapFragment.getView().setVisibility(View.VISIBLE);
                        break;
                    case R.id.drawer_groups:
                        setGroupsFragmentActionBar();
                        if(fragmentManager.findFragmentByTag(GROUPS_FRAGMENT_TAG)==null){
                            mapFragment.getView().setVisibility(View.GONE);
                            Fragment groupFrag = new Groups_fragment();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.map_fragment_content, groupFrag,GROUPS_FRAGMENT_TAG);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                            Toast.makeText(MainActivity.this,"Group Fragment",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });

        locationFab.setEnabled(false);
        autoZoomFab.setEnabled(false);
        if(myLocationEnabled)
            locationFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentDark)));
        locationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myLocationEnabled) {
                    locationFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    disableMyLocation();
                    Snackbar.make(v, "Location disabled", Snackbar.LENGTH_LONG).show();
                }else{
                    locationFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentDark)));
                    enableMyLocation();
                    Snackbar.make(v, "Location enabled", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        autoZoomFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean empty = true;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (HashMap.Entry<String, Marker> pair : markersHM.entrySet()) {
                    Marker marker = (Marker) pair.getValue();
                    if (marker.isVisible()) {
                        empty=false;
                        builder.include(marker.getPosition());
                    }
                }
                if(!empty) {
                    LatLngBounds bounds = builder.build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
                }

            }
        });

        /***********************  COMPROBAR ESTADO DE AUTENTICACIÓN **************************/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mAuth = firebaseAuth;
                mUser = mAuth.getCurrentUser();
                if(mUser!=null){
                    if(menuItemLog !=null)
                        menuItemLog.setTitle("Sign out");
                    myProfileRef = database.getReference().child("users").child(mUser.getUid());
                    //CHECK IF THE PROFILE ALREADY EXISTS IN DB
                    final ValueEventListener profileListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                //ASK FOR PHONE NUMBER AND NICK
                                final LinearLayout ll = new LinearLayout(MainActivity.this);
                                ll.setOrientation(LinearLayout.VERTICAL);
                                final EditText et = new EditText(MainActivity.this);
                                final EditText et2 = new EditText(MainActivity.this);
                                et.setHint("Phone number");
                                et2.setHint("Nick");
                                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                                ll.addView(et);
                                ll.addView(et2);
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Phone Number & Nick")
                                        .setView(ll)
                                        .setCancelable(false)
                                        .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String phoneNumber = et.getText().toString();
                                                String nick = et2.getText().toString();
                                                if (!phoneNumber.equals("")&&!nick.equals("")){
                                                    createProfile(phoneNumber,nick);
                                                }
                                            }
                                        })
                                        .show();
                            }else{
                                myProfileCreated = true;
                                pref.edit().putBoolean("profileCreated",true).commit();
                                mProfile=dataSnapshot.getValue(User.class);
                                initListeners();
                                myProfileRef.removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG,"Ref: "+myProfileRef.toString()+"  database error: "+databaseError);
                        }
                    };
                    myProfileRef.addValueEventListener(profileListener);
                } else{
                    disableButtons();
                    if(menuItemLog !=null)
                        menuItemLog.setTitle("Sign in");
                }

            }
        };
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG,myProfileCreated+" adksñlfjasñklfjña");
        if (myProfileCreated) {
            Intent intent = new Intent(this, FirebaseBackgroundListeners.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mService.disconnectService();
        if(mBound)
            unbindService(mConnection);
        mBound=false;
    }

    private void enableButtons(){
        if (menu != null){
            menuItemLog.setEnabled(true);
            menuItemShare.setEnabled(true);
            menuItemNotif.setEnabled(true);
            menuItemShare.setVisible(true);
            menuItemNotif.setVisible(true);
            menuItemFilter.setVisible(true);
        }
        locationFab.setVisibility(View.VISIBLE);
        autoZoomFab.setVisibility(View.VISIBLE);

        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_content, mMapFragment,MAP_FRAGMENT_TAG);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(MainActivity.this);

        pb.cancel();
    }

    private void disableButtons(){
        if (menu != null) {
            menuItemLog.setEnabled(true);
            menuItemNotif.setVisible(false);
            menuItemShare.setVisible(false);
            menuItemFilter.setVisible(false);
        }
        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);
        ab.setDisplayHomeAsUpEnabled(false);
        SupportMapFragment map = (SupportMapFragment) fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG);
        if(map!=null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(map);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }
        pb.cancel();
    }

    private void setGroupsFragmentActionBar(){
        if (menu != null){
            menuItemLog.setEnabled(true);
            menuItemShare.setEnabled(true);
            menuItemNotif.setEnabled(true);
            menuItemShare.setVisible(true);
            menuItemNotif.setVisible(true);
            menuItemFilter.setVisible(false);
        }
        locationFab.setVisibility(View.GONE);
        autoZoomFab.setVisibility(View.GONE);
    }

    private void setMapFragmentActionBar(){
        if (menu != null){
            menuItemLog.setEnabled(true);
            menuItemShare.setEnabled(true);
            menuItemNotif.setEnabled(true);
            menuItemShare.setVisible(true);
            menuItemNotif.setVisible(true);
            menuItemFilter.setVisible(true);
        }
        locationFab.setVisibility(View.VISIBLE);
        autoZoomFab.setVisibility(View.VISIBLE);
    }

    private void logout(){
        pb.show();
        Log.d(TAG,"logout");
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleAuthApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }
    private void login(){
        pb.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleAuthApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void createProfile(String phoneNumber, String nick){
        String email = Utils.generateValidEmail(mUser.getEmail());
        String requestsId = email+phoneNumber+"";
        database.getReference().child("publicIds").child(email).setValue(requestsId);
        database.getReference().child("publicIds").child(phoneNumber).setValue(requestsId);

        //database.getReference().child("publicIds").child(mProfile.getKey()).setValue(requestsId);

        User mProfile = new User(email,"",mUser.getUid(),phoneNumber,nick,new Location(),new ArrayList<InterestPoint>(),"",new ArrayList<String>(),requestsId);
        myProfileRef.setValue(mProfile);
    }

    private void initListeners(){
        Log.d(TAG,"init listeners");
        startService(new Intent(MainActivity.this,FirebaseBackgroundListeners.class));

        myRequestsRef = database.getReference().child("requests").child(mProfile.getEmail()+mProfile.getPhoneNumber());

        myProfileRef.child("groupsId").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String groupId = dataSnapshot.getValue(String.class);
                initGroupListeners(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String groupId = dataSnapshot.getValue(String.class);
                groupsRef.child(groupId).child("name").removeEventListener(nameGroupListener);
                groupsRef.child(groupId).child("state").removeEventListener(stateGroupListener);
                groupsRef.child(groupId).child("membersId").removeEventListener(membersGroupListener);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Intent intent = new Intent(this, FirebaseBackgroundListeners.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        initListenersFinished = true;
        if(mBound && initListenersFinished)
            enableButtons();
    }

    private void initGroupListeners(final String groupId){
        nameGroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupNameChanged(dataSnapshot.getValue(String.class),groupId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        stateGroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stateGroupChanged(dataSnapshot.getValue(int.class),groupId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        membersGroupListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final GroupMember member = dataSnapshot.getValue(GroupMember.class);
                locationValueListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        locationChanged(dataSnapshot.getValue(Location.class),member.getMemberId(),member.getNick(),groupId);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                if(member.getState()==Group.GROUP_STATE_ACTIVE)
                    myProfileRef.getParent().child(member.getMemberId()).child("location").addValueEventListener(locationValueListener);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final GroupMember member = dataSnapshot.getValue(GroupMember.class);
                if(member.getState()==Group.GROUP_STATE_ACTIVE)
                    myProfileRef.getParent().child(member.getMemberId()).child("location").addValueEventListener(locationValueListener);
                else if(member.getState()==Group.GROUP_STATE_STOPPED){
                    myProfileRef.getParent().child(member.getMemberId()).child("location").removeEventListener(locationValueListener);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final GroupMember member = dataSnapshot.getValue(GroupMember.class);
                myProfileRef.getParent().child(member.getMemberId()).child("location").removeEventListener(locationValueListener);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        groupsRef.child(groupId).child("name").addValueEventListener(nameGroupListener);
        groupsRef.child(groupId).child("state").addValueEventListener(stateGroupListener);
        groupsRef.child(groupId).child("membersId").addChildEventListener(membersGroupListener);
    }

    private void checkPermissions(){
        /****************RUN-ITME PERMISSIONS FOR ANDROID 6+***************/
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }else{
            final CharSequence[] items = {
                    "Phone number", "Email", "Key"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Share with");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0){
                        Intent intent = new Intent(MainActivity.this,MultiPickContactActivity.class);
                        intent.putExtra("rc",RC_PHONE_CONTACTS);
                        startActivityForResult(intent,RC_PHONE_CONTACTS);
                    }else if(item == 1){
                        Intent intent = new Intent(MainActivity.this,MultiPickContactActivity.class);
                        intent.putExtra("rc",RC_EMAIL_CONTACTS);
                        startActivityForResult(intent,RC_EMAIL_CONTACTS);
                    }else if(item == 2){
                        /*Intent intent = new Intent(MainActivity.this,MultiPickContactActivity.class);
                        intent.putExtra("rc",RC_KEY_CONTACTS);
                        startActivityForResult(intent,RC_KEY_CONTACTS);*/
                        Toast.makeText(MainActivity.this, "Not available yet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    /***************************** HANDLE BD CHANGES METHODS *****************************/

    private void createGroup(String name, ArrayList<String> selectedContacts){

        ArrayList<GroupMember> members = new ArrayList<>();
        members.add(new GroupMember(Group.GROUP_STATE_ACTIVE,mUser.getUid(),mProfile.getNick()));
        final String groupId = mUser.getUid()+System.currentTimeMillis();
        groupsRef.child(groupId).setValue(new Group(name,members));
        myProfileRef.child("groupsId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> groups = dataSnapshot.getValue(t);
                if (groups == null)
                    groups = new ArrayList<String>();
                Log.d(TAG,"datasnapshot: "+dataSnapshot+" groups: "+groups);
                groups.add(groupId);
                myProfileRef.child("groupsId").setValue(groups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for (String contact: selectedContacts){
            ValueEventListener listener;
            Log.d(TAG, ""+contact);
            listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference requestRef = myRequestsRef.getParent().child(dataSnapshot.getValue(String.class)).push();
                    String id = requestRef.getKey();
                    requestRef.setValue(new Request(groupId,Request.REQUEST_TYPE_GROUP,id));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG,"Cancelled: publicIds "+databaseError);
                }
            };
            publicIdsRef.child(contact).addListenerForSingleValueEvent(listener);
        }

    }

    private void groupNameChanged(String name, String groupId){
    }

    private void stateGroupChanged(int state,String groupId){
        Log.d(TAG,"STATE CHANGED TO: "+state+" OF GROUP: "+groupId);
        switch (state){
            case Group.GROUP_STATE_ACTIVE:
                if(!myActiveGroups.contains(groupId)){
                    myActiveGroups.add(groupId);
                    myFilteredGroups.add(groupId);
                }
                break;
            case Group.GROUP_STATE_STOPPED:
                if(myActiveGroups.contains(groupId)){
                    myActiveGroups.remove(groupId);
                    myFilteredGroups.remove(groupId);
                }
                break;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Filter_fragment existFragment = (Filter_fragment) fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
        if (existFragment != null){
            existFragment.onResume();
        }
    }

    private void locationChanged(Location location, String userId,String nick, String groupId){
        Log.d(TAG,"position: "+location.getLat()+","+location.getLng()+"         "+userId+"       "+groupId);

        if(mapLoaded && mGoogleMap!=null){
            if(!markersHM.containsKey(userId+"/"+groupId)){
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLat(),location.getLng()))
                    .title(nick));
                markersHM.put(userId+"/"+groupId,marker);
            }else{
                Marker marker = markersHM.get(userId+"/"+groupId);
                marker.setPosition(new LatLng(location.getLat(),location.getLng()));
            }

        }
    }


    /********************************************************************************************/

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"requestCode = "+requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        switch (requestCode){
            case RC_SIGN_IN:
                Log.d(TAG,"recibo autenticación de google");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    // Google Sign In failed, update UI appropriately
                    // ...
                }
                break;

            case RC_PHONE_CONTACTS:
            case RC_EMAIL_CONTACTS:
            case RC_KEY_CONTACTS:
                if(resultCode==RESULT_OK) {
                    final ArrayList<String> selectedContacts = data.getStringArrayListExtra("selectedContacts");
                    if(fragmentManager.findFragmentByTag(EDIT_GROUP_FRAGMENT_TAG)!=null){
                        EditGroupFragment frag = (EditGroupFragment) fragmentManager.findFragmentByTag(EDIT_GROUP_FRAGMENT_TAG);
                        frag.membersAdded(selectedContacts);
                    }else{
                        final EditText et = (EditText) new EditText(MainActivity.this);
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Group name")
                                .setMessage("Enter a name for your group")
                                .setView(et)
                                .setCancelable(true)
                                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String name = et.getText().toString();
                                        if (!name.equals("")) {
                                            createGroup(name, selectedContacts);
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
                }

                break;
            case RC_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        if(mBound)
                            mService.enableLocation();
                        break;
                    case RESULT_CANCELED:
                        locationFab.performClick();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CONTACTS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                } else {
                    Toast.makeText(MainActivity.this,"Permissions denied",Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                } else {
                    Toast.makeText(MainActivity.this,"Permissions denied",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu,menu);
        menuItemLog = menu.findItem(R.id.action_log);
        menuItemShare = menu.findItem(R.id.action_share);
        menuItemNotif = menu.findItem(R.id.action_notifications);
        menuItemFilter = menu.findItem(R.id.action_filter);
        if(mUser!=null)
            menuItemLog.setTitle("Sign out");
        else {
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemFilter.setVisible(false);
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
        }
        MenuItemCompat.setActionView(menu.findItem(R.id.action_notifications), R.layout.notification_badge);
        View count = menu.findItem(R.id.action_notifications).getActionView();
        notifications = (Button) count.findViewById(R.id.notif);
        if(mBound)
            notifications.setText(mService.registerClient(MainActivity.this)+"");
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Notifications_fragment existFragment = (Notifications_fragment) fragmentManager.findFragmentByTag(NOTIF_FRAGMENT_TAG);
                if (existFragment != null){
                    notifications.setBackground(getResources().getDrawable(R.drawable.shape_notifs));
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    transaction.remove(existFragment);
                    transaction.commit();
                    fragmentManager.popBackStack();
                }else{
                    notifications.setBackground(getResources().getDrawable(R.drawable.shape_notifs_clicked));
                    Notifications_fragment fragment = new Notifications_fragment();
                    transaction.replace(R.id.notif_fragment_content,fragment,NOTIF_FRAGMENT_TAG);
                    transaction.addToBackStack(NOTIF_FRAGMENT_TAG);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.commit();
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log:
                //pb.show();
                if(menuItemLog.getTitle().toString().equals("Sign in"))
                    login();
                else if(menuItemLog.getTitle().toString().equals("Sign out"))
                    logout();
                return true;
            case R.id.action_share:
                checkPermissions();
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_filter:
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Filter_fragment existFragment = (Filter_fragment) fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
                if (existFragment != null){
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.remove(existFragment);
                    transaction.commit();
                    fragmentManager.popBackStack();
                }else{
                    Filter_fragment fragment = new Filter_fragment();
                    transaction.add(R.id.filter_fragment_content,fragment,FILTER_FRAGMENT_TAG);
                    transaction.addToBackStack(FILTER_FRAGMENT_TAG);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /************************** INTERACTIONS WITH INTERFACES **************************/

    @Override
    public ArrayList<Request> getRequests() {
        Log.d(TAG,"getRequests... bound = "+mBound);
        if (mBound)
            return mService.getPendingRequests();
        return new ArrayList<>();
    }


    @Override
    public void acceptRequest(String requestId) {
        Log.d(TAG,requestId);
        myRequestsRef.child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Request request = dataSnapshot.getValue(Request.class);
                Log.d(TAG,"group id: "+request.getIdGroup());
                groupsRef.child(request.getIdGroup()).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Group grupo = mutableData.getValue(Group.class);
                        Log.d(TAG,"mutabledata "+mutableData+" group "+grupo);
                        if (grupo==null)
                            return Transaction.success(mutableData);
                        else {
                            Log.d(TAG, "NO NULO: " + mutableData);
                            ArrayList<GroupMember> aux = grupo.getMembersId();
                            GroupMember aux2 = new GroupMember(Group.GROUP_STATE_ACTIVE,mUser.getUid(),mProfile.getNick());
                            if (!aux.contains(aux2))
                                aux.add(aux2);
                            grupo.setMembersId(aux);
                            mutableData.setValue(grupo);
                            Log.d(TAG, "FINAL  "+mutableData);
                            return Transaction.success(mutableData);
                        }
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
                myProfileRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        User user = mutableData.getValue(User.class);
                        if (user == null)
                            return Transaction.success(mutableData);
                        user.addGroup(request.getIdGroup());
                        mutableData.setValue(user);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void cancelRequest(String requestId){
        myRequestsRef.child(requestId).removeValue();
    }

    @Override
    public void onRequestReceived(String requestId) {
        int aux = Integer.parseInt(notifications.getText().toString());
        aux++;
        notifications.setText(String.valueOf(aux));

        Notifications_fragment existFragment = (Notifications_fragment) fragmentManager.findFragmentByTag(NOTIF_FRAGMENT_TAG);
        if (existFragment != null){
            existFragment.onResume();
        }
    }

    @Override
    public void onRequestRemoved() {
        int aux = Integer.parseInt(notifications.getText().toString());
        aux--;
        notifications.setText(String.valueOf(aux));
    }

    @Override
    public void addFilteredGroup(String groupId) {
        myFilteredGroups.add(groupId);

        for(HashMap.Entry<String,Marker> pair : markersHM.entrySet()){
            if(pair.getKey().endsWith(groupId))
                pair.getValue().setVisible(true);
        }
    }

    @Override
    public void removeFilteredGroup(String groupId) {
        myFilteredGroups.remove(groupId);

        for(HashMap.Entry<String,Marker> pair : markersHM.entrySet()){
            if(pair.getKey().endsWith(groupId))
                pair.getValue().setVisible(false);
        }
    }

    @Override
    public ArrayList<String> getActiveGroups() {
        Log.d(TAG,"ñlkdsjfklasjdfañldsjkfña ACTIVE: "+ myActiveGroups.toString());
        return myActiveGroups;
    }

    @Override
    public ArrayList<String> getFilteredGroups() {
        Log.d(TAG,"KALÑDJFASÑJDFKLAJÑDF FILTERED: "+myFilteredGroups);
        return myFilteredGroups;
    }

    @Override
    public ArrayList<String> getGroups(){
        return myActiveGroups;
    }

    @Override
    public void groupSelected(String groupId){
        Fragment editGroupFragment = EditGroupFragment.newInstance(groupId,mUser.getUid());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_content, editGroupFragment,EDIT_GROUP_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void addGroupMember(){
        checkPermissions();
    }

    @Override
    public void removeMember(String id){
        EditGroupFragment frag = (EditGroupFragment) fragmentManager.findFragmentByTag(EDIT_GROUP_FRAGMENT_TAG);
        if(frag!=null){
            frag.removeMember(id);
        }
    }

    /******************************* GOOGLE MAPS/LOCATION CALLBACKS ****************************/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setOnMapLoadedCallback(this);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

    }

    private void enableMyLocation(){
        if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        } else {
            if(mBound)
                myLocationEnabled = mService.enableLocation();
        }
    }

    private void disableMyLocation(){
        if(mBound)
            myLocationEnabled = mService.disableLocation();
    }

    @Override
    public void onMapLoaded() {
        mGoogleMap.setOnMapLongClickListener(this);
        mapLoaded = true;
        autoZoomFab.setEnabled(true);
    }

    @Override
    public void onMapLongClick(LatLng point){
        locationChanged(new Location(point.latitude,point.longitude,0),mUser.getUid(),mProfile.getNick(),"");
        Toast.makeText(MainActivity.this,"lat: "+point.latitude+" lng: "+point.longitude,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location, String userId){
        if(mProfile != null)
            locationChanged(location,userId,mProfile.getNick(),"");
    }

    @Override
    public void startResolution(Status status){
        try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            status.startResolutionForResult(
                    MainActivity.this,
                    RC_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            // Ignore the error.
        }
    }

}
