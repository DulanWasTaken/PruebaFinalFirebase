package es.udc.tfg.pruebafinalfirebase;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.CountDownTimer;
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
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.text.InputType;

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
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import es.situm.sdk.SitumSdk;
import es.situm.sdk.communication.CommunicationManager;
import es.situm.sdk.error.Error;
import es.situm.sdk.model.cartography.Building;
import es.situm.sdk.model.cartography.Floor;
import es.situm.sdk.model.location.Bounds;
import es.situm.sdk.model.location.Coordinate;
import es.situm.sdk.utils.Handler;
import es.udc.tfg.pruebafinalfirebase.Group.EditGroupFragment;
import es.udc.tfg.pruebafinalfirebase.Group.Group;
import es.udc.tfg.pruebafinalfirebase.Group.GroupsRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.Group.Groups_fragment;
import es.udc.tfg.pruebafinalfirebase.Indoor.IndoorFragment;
import es.udc.tfg.pruebafinalfirebase.Indoor.IndoorRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.Indoor.SitumAccount;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.DestinationPoint;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.InterestPoint;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.InterestPointFragment;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.Point;
import es.udc.tfg.pruebafinalfirebase.LevelPicker.LevelPickerFragment;
import es.udc.tfg.pruebafinalfirebase.LevelPicker.levelPickerRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.Messages.Message;
import es.udc.tfg.pruebafinalfirebase.Messages.MessagesFragment;
import es.udc.tfg.pruebafinalfirebase.Filter.FilterRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.Filter.Filter_fragment;
import es.udc.tfg.pruebafinalfirebase.Messages.QuickMsgFragment;
import es.udc.tfg.pruebafinalfirebase.Messages.msgRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.MultiPickContactActivity;
import es.udc.tfg.pruebafinalfirebase.Notifications.Notifications_fragment;
import es.udc.tfg.pruebafinalfirebase.Notifications.Request;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.metrics.AddTrace;

public class MainActivity extends AppCompatActivity implements levelPickerRecyclerViewAdapter.onLevelPickerAdapterInteractionListener,IndoorRecyclerViewAdapter.OnIndoorAdapterInteractionListener,IndoorFragment.OnIndoorFragmentInteractionListener,FilterRecyclerViewAdapter.OnFilterFragmentAdapterInteractionListener,msgRecyclerViewAdapter.OnMsgAdapterInteractionListener,GoogleMap.OnInfoWindowClickListener,infoWindowRecyclerViewAdapter.onMapChatAdapterInteractionListener,InterestPointFragment.OnInterestPointFragmentInteractionListener,Filter_fragment.OnFilterFragmentInteractionListener,Groups_fragment.OnGroupsFragmentInteractionListener,LoginFragment.OnLoginFragmentInteractionListener,QuickMsgFragment.OnQuickMsgFragmentInteractionListener,DBManager.DBManagerInteractions,EditGroupFragment.OnEditGroupFragmentInteractionListener,GoogleMap.OnMapLongClickListener,GroupsRecyclerViewAdapter.OnGroupsAdapterInteractionListener,GoogleMap.OnMapLoadedCallback,OnMapReadyCallback, es.udc.tfg.pruebafinalfirebase.mService.OnServiceInteractionListener,GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    public static final String TAG = "MainActiv";
    public static final String NOTIF_FRAGMENT_TAG = "NOTIF_FRAGMENT_TAG";
    public static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT_TAG";
    public static final String GROUPS_FRAGMENT_TAG = "GROUPS_FRAGMENT_TAG";
    public static final String FILTER_FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";
    public static final String EDIT_GROUP_FRAGMENT_TAG = "EDIT_GROUP_FRAGMENT_TAG";
    public static final String MESSAGES_FRAGMENT_TAG = "MESSAGES_FRAGMENT_TAG";
    public static final String INDOOR_FRAGMENT_TAG = "INDOOR_FRAGMENT_TAG";
    public static final String LEVEL_FRAGMENT_TAG = "LEVEL_FRAGMENT_TAG";
    public static final String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT_TAG";
    public static final String QUICKMSG_FRAGMENT_TAG = "QUICKMSG_FRAGMENT_TAG";
    public static final String LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT_TAG";
    public static final String IP_FRAGMENT_TAG = "IP_FRAGMENT_TAG";
    public static final String NO_ACC = "NO_ACC";
    public static final int RC_SIGN_IN = 777;
    public static final int RC_PHONE_CONTACTS = 888;
    public static final int RC_EMAIL_CONTACTS = 999;
    public static final int RC_KEY_CONTACTS = 555;
    public static final int RC_CHECK_SETTINGS = 666;
    public static final int RC_CREATE_GROUP = 1;
    public static final int RC_ADD_MEMBER = 2;
    public static final int NO_LEVEL = 9999;
    public static final int PERMISSION_REQUEST_READ_CONTACTS = 333;
    public static final int PERMISSION_REQUEST_LOCATION = 222;
    public static final String MARKER_TAG_CHAT_TEXT = "chat/text";
    public static final String MARKER_TAG_CHAT_IP = "chat/ip";
    public static final String MARKER_TAG_IP = "ip/main";
    public static final String MARKER_TAG_LOCATION = "location/main";
    public boolean myLocationEnabled = false;
    public boolean mapLoaded=false;
    public boolean myProfileCreated = false;
    public boolean myServiceRunning = false;
    public boolean autoZoomEnabled = false;

    /*public ArrayList<String> myFilteredGroups = new ArrayList<>();
    public HashMap<String,Marker> markersHM = new HashMap<String,Marker>();*/
    private ArrayList<Marker> otherIps = new ArrayList<>();
    private ArrayList<Marker> ipMapMarkers = new ArrayList<>();
    private Bundle bundle;

    private LatLng dragInitPos;
    private boolean ipState = false;
    private long lastTimeBackPressed = 0;
    private CameraPosition cameraPosition;

    private RelativeLayout main_content;
    private FloatingActionButton locationFab,autoZoomFab;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private MenuItem menuItemShare,menuItemNotif,menuItemFilter,menuItemQuickmsg,menuItemQuickmap,menuItemIndoor;
    private Button notifications;
    private Switch indoor_switch;
    private Menu menu;
    private Dialog pb;
    private AlertDialog noProfileDialog;
    private ActionBar ab;

    public FragmentManager fragmentManager;
    private LocationManager locationManager;
    public SharedPreferences pref;
    public SharedPreferences appPreferences;

    private es.udc.tfg.pruebafinalfirebase.mService mService;
    private ServiceConnection mConnection;
    private boolean mBound;
    private CommunicationManager situmCommunicationManager;
    private String currentBuildingId = "";
    private Building currentBuilding = null;
    private Collection<Floor> currentFloors = null;

    private GoogleApiClient mGoogleAuthApiClient;
    private GoogleMapOptions mapOptions;
    private GoogleSignInOptions gso;
    private GoogleMap mGoogleMap;
    private String drawerFlag = "";

    private DBManager dbManager = DBManager.getInstance();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    @AddTrace(name = "onCreateTrace", enabled = true/*Optional*/)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
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



        /************************** INICIALIZAR VARIABLES ***************************/
        //SitumSdk.init(this);
        fragmentManager = getSupportFragmentManager();
        locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        PreferenceManager.setDefaultValues(MainActivity.this,R.xml.preferences,false);
        appPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        pref = getSharedPreferences("MYSERVICE", Context.MODE_PRIVATE);
        myLocationEnabled = pref.getBoolean("locationEnabled",false);
        myProfileCreated = pref.getBoolean("profileCreated",false);
        myServiceRunning = pref.getBoolean("serviceRunning",false);
        autoZoomEnabled = pref.getBoolean("autoZoomState",false);



        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                es.udc.tfg.pruebafinalfirebase.mService.LocalBinder binder = (es.udc.tfg.pruebafinalfirebase.mService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                mService.registerClient(MainActivity.this);
                situmCommunicationManager = SitumSdk.communicationManager();
                locationFab.setEnabled(true);
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

        locationFab = (FloatingActionButton) findViewById(R.id.location_fab);
        autoZoomFab = (FloatingActionButton) findViewById(R.id.auto_view_fab);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        main_content = (RelativeLayout) findViewById(R.id.main_content);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        TextView sakdfñ = (TextView) navigationView.getHeaderView(0).findViewById(R.id.account_name);

        /*********************** INICILIZAR LOS ONCLICKLISTENERS **************************/

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "drawerButton");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ActionBarButton");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                removeSecondaryViews();
                switch (menuItem.getItemId()){
                    case R.id.drawer_map:
                        setMapFragment();
                        drawerFlag = MAP_FRAGMENT_TAG;
                        break;
                    case R.id.drawer_groups:
                        setGroupsFragment();
                        drawerFlag = GROUPS_FRAGMENT_TAG;
                        break;
                    case R.id.drawer_indoor:
                        setIndoorFragment(pref.getString("situmAccount",NO_ACC));
                        drawerFlag = INDOOR_FRAGMENT_TAG;
                        break;
                    case R.id.drawer_settings:
                        setSettingsFragment();
                        drawerFlag = SETTINGS_FRAGMENT_TAG;
                        break;
                    case R.id.drawer_logout:
                        logout();
                        drawerFlag = LOGIN_FRAGMENT_TAG;
                        break;
                }
                Log.d(TAG,"MAP FRAGMENT IS "+drawerFlag);
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
                if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    Snackbar.make(getCurrentFocus(),"Enable GPS",Snackbar.LENGTH_SHORT).setAction("settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).show();
                } else{

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
            }
        });

        if(autoZoomEnabled)
            autoZoomFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_gray)));
        autoZoomFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(appPreferences.getString(SettingsFragment.KEY_AUTOZOOM,getString(R.string.preference_autozoom_button)).equals(getString(R.string.preference_autozoom_switch))){
                    autoZoomEnabled = !autoZoomEnabled;
                    pref.edit().putBoolean("autoZoomState",autoZoomEnabled).commit();
                    if(autoZoomEnabled)
                        autoZoomFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.divider_gray)));
                    else
                        autoZoomFab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

                } else if(appPreferences.getString(SettingsFragment.KEY_AUTOZOOM,getString(R.string.preference_autozoom_button)).equals(getString(R.string.preference_autozoom_button))) {
                    boolean empty = true;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (MapMarker m : DBManager.mapMarkers){
                        final Marker marker = m.getMarker();
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
            }
        });

        /***********************  COMPROBAR ESTADO DE AUTENTICACIÓN **************************/
        if(!myServiceRunning) {
            startService(new Intent(MainActivity.this, es.udc.tfg.pruebafinalfirebase.mService.class));
            Log.d(TAG,"STARTING SERVICE");
        }

        dbManager.bindDBManager(MainActivity.this,DBManager.MODE_CREATE);

        MapsInitializer.initialize(getApplicationContext());
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this, es.udc.tfg.pruebafinalfirebase.mService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if(!dbManager.getDbManagerListenerContext().equals(TAG))
            dbManager.bindDBManager(MainActivity.this,DBManager.MODE_APPEND);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mService!=null)
            mService.disconnectService();
        if(mBound)
            unbindService(mConnection);
        mBound=false;

        long aux = System.currentTimeMillis();
        pref.edit().putLong("lastTimeForeground",aux).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            this.bundle = bundle;
            String groupId = bundle.getString("msgGroupId","");
            if(!groupId.equals(""))
                dbManager.setFilter(dbManager.findGroupById(groupId),true);
        }

    }

    private void initView(){
        if(dbManager.isAuthenticated()){
            TextView account_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.account_name);
            TextView account_phone = (TextView) navigationView.getHeaderView(0).findViewById(R.id.account_phone);
            account_name.setText(dbManager.getNick());
            account_phone.setText(dbManager.getProfile().getPhoneNumber());
            setMapFragment();
        }else{
            setLoginFragment();
        }
    }

    private void setLoginFragment(){
        ab.hide();

        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);

        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.map_fragment_content,loginFragment,LOGIN_FRAGMENT_TAG);
        Fragment filter = fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
        if(filter!=null)
            transaction.remove(filter);
        transaction.commit();
        fragmentManager.executePendingTransactions();
        drawerFlag = LOGIN_FRAGMENT_TAG;
        pb.cancel();
    }

    private void setMapFragment(){
        Log.d(TAG,"SET MAP");

        //Location location = dbManager.getProfile().getLocation();
        mapOptions = new GoogleMapOptions();
        mapOptions.camera(cameraPosition);

        navigationView.setCheckedItem(R.id.drawer_map);

        if (menu != null){
            menuItemShare.setEnabled(true);
            menuItemNotif.setEnabled(true);
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemFilter.setVisible(true);
            menuItemQuickmsg.setEnabled(true);
            menuItemQuickmsg.setVisible(true);
            menuItemQuickmap.setEnabled(false);
            menuItemQuickmap.setVisible(false);
            menuItemIndoor.setVisible(false);
        }
        locationFab.setVisibility(View.VISIBLE);
        autoZoomFab.setVisibility(View.VISIBLE);
        if(DBManager.pendingRequests.size()!=0 && menuItemNotif!=null)
            menuItemNotif.setVisible(true);

        ab.show();
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("WrU");

        if(!drawerFlag.equals(MAP_FRAGMENT_TAG)) {
            SupportMapFragment mMapFragment = SupportMapFragment.newInstance(mapOptions);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.map_fragment_content, mMapFragment, MAP_FRAGMENT_TAG);
            //fragmentTransaction.commit();
            fragmentTransaction.commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
            mMapFragment.getMapAsync(MainActivity.this);
            drawerFlag = MAP_FRAGMENT_TAG;
        }

        pb.cancel();
    }

    private void setGroupsFragment(){
        navigationView.setCheckedItem(R.id.drawer_groups);
        currentBuildingId = "";

        if(drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap != null){
            cameraPosition = mGoogleMap.getCameraPosition();
        }

        if (menu != null){
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemFilter.setVisible(false);
            menuItemQuickmsg.setEnabled(false);
            menuItemQuickmsg.setVisible(false);
            menuItemQuickmap.setEnabled(false);
            menuItemQuickmap.setVisible(false);
            menuItemIndoor.setVisible(false);
        }
        locationFab.setVisibility(View.GONE);
        autoZoomFab.setVisibility(View.GONE);
        if(DBManager.pendingRequests.size()!=0)
            menuItemNotif.setVisible(true);

        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Groups");

        if(!drawerFlag.equals(GROUPS_FRAGMENT_TAG)) {
            Groups_fragment groupsFragment = new Groups_fragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.map_fragment_content, groupsFragment, GROUPS_FRAGMENT_TAG);
            Fragment filter = fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
            if(filter!=null)
                fragmentTransaction.remove(filter);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
            drawerFlag = GROUPS_FRAGMENT_TAG;
        }

        pb.cancel();
    }

    private void setEditGroupFragment(String groupId){
        currentBuildingId = "";
        if(drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap != null){
            cameraPosition = mGoogleMap.getCameraPosition();
        }

        if (menu != null){
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemFilter.setVisible(false);
            menuItemQuickmsg.setEnabled(false);
            menuItemQuickmsg.setVisible(false);
            menuItemQuickmap.setEnabled(false);
            menuItemQuickmap.setVisible(false);
            menuItemIndoor.setVisible(false);
        }
        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);

        ab.setDefaultDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back);
        ab.setTitle(dbManager.findGroupById(groupId).getName());

        EditGroupFragment editGroupFragment = EditGroupFragment.newInstance(groupId);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_content, editGroupFragment, EDIT_GROUP_FRAGMENT_TAG+groupId);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();

        drawerFlag = EDIT_GROUP_FRAGMENT_TAG;

        pb.cancel();
    }

    private void setMessagesFragment(String groupId){
        currentBuildingId = "";
        if(drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap != null){
            cameraPosition = mGoogleMap.getCameraPosition();
        }

        if (menu != null){
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemFilter.setVisible(false);
            menuItemQuickmsg.setEnabled(false);
            menuItemQuickmsg.setVisible(false);
            menuItemQuickmap.setEnabled(true);
            menuItemQuickmap.setVisible(true);
            menuItemIndoor.setVisible(false);
        }
        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);

        ab.setDefaultDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back);
        ab.setTitle(dbManager.findGroupById(groupId).getName());

        MessagesFragment messagesFragment = MessagesFragment.newInstance(groupId);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_content, messagesFragment, MESSAGES_FRAGMENT_TAG+groupId);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();

        drawerFlag = MESSAGES_FRAGMENT_TAG;

        pb.cancel();
    }

    private void setIpFragment(String userId, String ipId){
        currentBuildingId = "";
        if(drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap != null){
            cameraPosition = mGoogleMap.getCameraPosition();
        }

        if (menu != null){
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemFilter.setVisible(false);
            menuItemQuickmsg.setEnabled(false);
            menuItemQuickmsg.setVisible(false);
            menuItemQuickmap.setEnabled(true);
            menuItemQuickmap.setVisible(true);
            menuItemIndoor.setVisible(false);
        }
        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);

        ab.setDefaultDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back);

        Log.d(TAG,"Seting ip fragment with user id = "+userId+" and ip id = "+ipId);

        InterestPointFragment ipFragment = InterestPointFragment.newInstance(ipId,userId);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.map_fragment_content, ipFragment, IP_FRAGMENT_TAG+userId+ipId);
        ft.addToBackStack(null);
        ft.commit();
        //fragmentManager.executePendingTransactions();

        drawerFlag = IP_FRAGMENT_TAG;

        pb.cancel();
    }

    private void setSettingsFragment(){
        currentBuildingId = "";
        navigationView.setCheckedItem(R.id.drawer_settings);

        if(drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap != null){
            cameraPosition = mGoogleMap.getCameraPosition();
        }

        if (menu != null){
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemFilter.setVisible(false);
            menuItemQuickmsg.setEnabled(false);
            menuItemQuickmsg.setVisible(false);
            menuItemQuickmap.setEnabled(false);
            menuItemQuickmap.setVisible(false);
            menuItemIndoor.setVisible(false);
        }
        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Settings");


        SettingsFragment settingsFragment = new SettingsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_content, settingsFragment, SETTINGS_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();

        drawerFlag = SETTINGS_FRAGMENT_TAG;

        pb.cancel();
    }

    private void setIndoorFragment(String acc){
        currentBuildingId = "";
        navigationView.setCheckedItem(R.id.drawer_indoor);

        if(drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap != null){
            cameraPosition = mGoogleMap.getCameraPosition();
        }

        if (menu != null){
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemFilter.setVisible(false);
            menuItemQuickmsg.setEnabled(false);
            menuItemQuickmsg.setVisible(false);
            menuItemQuickmap.setEnabled(false);
            menuItemQuickmap.setVisible(false);
            menuItemIndoor.setVisible(true);
        }
        locationFab.setVisibility(View.GONE);
        autoZoomFab.setVisibility(View.GONE);

        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Indoor");

        if(!drawerFlag.equals(INDOOR_FRAGMENT_TAG)) {
            IndoorFragment indoorFragment = IndoorFragment.newInstance(acc);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.map_fragment_content, indoorFragment, INDOOR_FRAGMENT_TAG);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
            drawerFlag = INDOOR_FRAGMENT_TAG;
        }

        pb.cancel();
    }

    private void removeSecondaryViews(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment filter = fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
        Fragment quickMsg = fragmentManager.findFragmentByTag(QUICKMSG_FRAGMENT_TAG);
        Fragment requests = fragmentManager.findFragmentByTag(NOTIF_FRAGMENT_TAG);
        Fragment level = fragmentManager.findFragmentByTag(LEVEL_FRAGMENT_TAG);
        if(filter!=null)
            transaction.remove(filter);
        if(quickMsg!=null)
            transaction.remove(quickMsg);
        if(requests!=null)
            transaction.remove(requests);
        if(level!=null)
            transaction.remove(level);
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    private void logout(){
        pb.show();
        Log.d(TAG,"logout");
        disableMyLocation();
        dbManager.signOut();
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

    private void checkPermissions(final int extraParam){
        /****************RUN-ITME PERMISSIONS FOR ANDROID 6+***************/
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS}, extraParam);
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
                        startActivityForResult(intent,RC_PHONE_CONTACTS+extraParam);
                    }else if(item == 1){
                        Intent intent = new Intent(MainActivity.this,MultiPickContactActivity.class);
                        intent.putExtra("rc",RC_EMAIL_CONTACTS);
                        startActivityForResult(intent,RC_EMAIL_CONTACTS+extraParam);
                    }else if(item == 2){
                        /*Intent intent = new Intent(MainActivity.this,MultiPickContactActivity.class);
                        intent.putExtra("rc",RC_KEY_CONTACTS);
                        startActivityForResult(intent,RC_KEY_CONTACTS+extraParam);*/
                        Toast.makeText(MainActivity.this, "Not available yet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
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

    private int findLevelFromFloor(String floorId){
        if(currentFloors!=null)
            for(Floor floor : currentFloors){
            if(floor.getIdentifier().equals(floorId))
                return floor.getLevel();
        }
        return NO_LEVEL;
    }

    private void locationChanged(Location location, String userId,String nick, String groupId){
        Log.d(TAG,"position: "+location.getLat()+","+location.getLng()+"         "+"   ACTIVE?"+location.isActive());

        int index = dbManager.findMarker(userId,groupId);

        final MarkerOptions options;
        final Bitmap icon;

        if(!userId.equals(dbManager.getId())) {
            if(location.getBuildingId()!=null && location.getFloorId()!=null && location.isIndoor())
                icon = Utils.overlay(BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_marker), BitmapFactory.decodeResource(getResources(), R.drawable.level_badge_background), userId, location.getBuildingId().equals(currentBuildingId) ? findLevelFromFloor(location.getFloorId()) : NO_LEVEL, getResources().getDisplayMetrics().density, false);
            else
                icon = Utils.overlay(BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_marker), BitmapFactory.decodeResource(getResources(), R.drawable.level_badge_background), userId, NO_LEVEL, getResources().getDisplayMetrics().density, false);


            options = new MarkerOptions()
                        .position(new LatLng(location.getLat(), location.getLng()))
                        .title(nick)
                        .anchor((float) 0.5, (float) 1)
                        .icon(BitmapDescriptorFactory.fromBitmap(icon));

        }else {

            icon = BitmapFactory.decodeResource(getResources(),location.getBearing()==(float)0.0?R.drawable.ic_mylocation_nobearing:R.drawable.ic_mylocation);
            options = new MarkerOptions()
                    .position(new LatLng(location.getLat(), location.getLng()))
                    .rotation(location.getBearing())
                    .title("Me")
                    .anchor((float)0.5,(float)0.5)
                    .infoWindowAnchor((float)0.5,(float)0.5)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(icon));

           /* options = new MarkerOptions()
                    .position(new LatLng(location.getLat(), location.getLng()))
                    .rotation(location.getBearing())
                    .title("Me")
                    .anchor((float)0.5,(float)0.5)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.overlay(BitmapFactory.decodeResource(getResources(),R.drawable.ic_map_marker),BitmapFactory.decodeResource(getResources(),R.drawable.level_badge_background),"alsdkfñebueh",-3,getResources().getDisplayMetrics().density)));
*/
        }


        if(index==-1){
            MapMarker mapMarker = new MapMarker(options,userId,groupId,MapMarker.LOCATION_MARKER,location.isActive(),null);
            if(drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap != null){
                Marker marker = mGoogleMap.addMarker(options);
                marker.setVisible(location.isActive() && dbManager.isFiltered(groupId));
                if(userId!=dbManager.getId())
                    marker.setTag(mapMarker.getMessages());
                mapMarker.setMarker(marker);
            }
            DBManager.mapMarkers.add(mapMarker);
        }else if(index >= 0 && index < DBManager.mapMarkers.size()){
            MapMarker mm = DBManager.mapMarkers.get(index);
            mm.setMarkerOptions(options);
            mm.setActive(location.isActive());
            Marker m = mm.getMarker();
            if(m!=null && drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap!=null){
                m.setPosition(new LatLng(location.getLat(),location.getLng()));
                m.setVisible(location.isActive() && dbManager.isFiltered(groupId));
                m.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                if(userId!=dbManager.getId())
                    m.setTag(mm.getMessages());
                else
                    m.setRotation(location.getBearing());
            }else if (drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap!=null){
                Marker newM = mGoogleMap.addMarker(options);
                newM.setVisible(location.isActive() && dbManager.isFiltered(groupId));
                if(userId!=dbManager.getId())
                    newM.setTag(mm.getMessages());
                mm.setMarker(newM);
            }
        }

        if(appPreferences.getString(SettingsFragment.KEY_AUTOZOOM,getString(R.string.preference_autozoom_button)).equals(getString(R.string.preference_autozoom_switch)) && autoZoomEnabled){
            boolean empty = true;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MapMarker m : DBManager.mapMarkers){
                final Marker marker = m.getMarker();
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
        /*if (index >= 0){
            Log.d(TAG,"position: "+ "existe0");
            MapMarker m = mapMarkers.get(index);
            Marker marker = m.getMarker();
            m.setMarkerOptions(options);
            m.setActive(location.isActive());

            if (marker!= null && drawerFlag.equals(MAP_FRAGMENT_TAG)){
                marker.setPosition(new LatLng(location.getLat(), location.getLng()));
                marker.setVisible(m.isActive()&& dbManager.isFiltered(groupId));
                Log.d(TAG,"position: "+ "existe1");

            }else if (drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap!=null){
                Marker newMarker = mGoogleMap.addMarker(options);
                newMarker.setVisible(m.isActive()&& dbManager.isFiltered(groupId));
                m.setMarker(newMarker);
                Log.d(TAG,"position: "+ "existe2");

            }

        }else{
            Log.d(TAG,"position: "+ "NO existe");

            if(mGoogleMap!=null && drawerFlag.equals(MAP_FRAGMENT_TAG)){
                Marker newMarker = mGoogleMap.addMarker(options);
                newMarker.setVisible(location.isActive() && dbManager.isFiltered(groupId));
                mapMarkers.add(new MapMarker(newMarker,options, Id, groupId, MapMarker.LOCATION_MARKER,location.isActive()));
            }else{
                mapMarkers.add(new MapMarker(options, Id, groupId, MapMarker.LOCATION_MARKER,location.isActive()));
            }
        }
*/
    }

    private String groupIdMarker(String markerId){
        for (MapMarker m : DBManager.mapMarkers){
            if(m.getId()!=null)
                if (m.getId().equals(markerId)){
                    return m.getGroupId();
                }
        }
        return null;
    }

    /******************************** LOGIN FRAGMENT METHODS ************************************/

    @Override
    public void signInButtonClicked(){
        login();
    }

    /******************************* FILTER FRAGMENT METHODS ***********************************/

    @Override
    public void ipStateChanged() {
        ipState = !ipState;
        for(Marker m : ipMapMarkers){
            m.setVisible(ipState);
        }
    }

    /*************************** QUICK MESSAGE FRAGMENT METHODS ********************************/

    @Override
    public void quickMsgSent(String groups){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if(groups.equals(""))
            Snackbar.make(view, "No groups selected", Snackbar.LENGTH_LONG).show();
        else{
            Snackbar.make(view, "Message sent to "+groups, Snackbar.LENGTH_LONG).show();
        }

        removeSecondaryViews();
    }

    /****************************** MESSAGES ADAPTER METHODS ***********************************/

    @Override
    public void ipClicked(Message msg) {
        setIpFragment(msg.getUserIp(),msg.getIpId());
    }

    /******************************* GROUPS FRAGMENT METHODS ***********************************/

    @Override
    public void addGroup(){
        removeSecondaryViews();
        checkPermissions(RC_CREATE_GROUP);
    }

    /************************** GROUPS RECYCLER VIEW ADAPTER METHODS ***************************/
    @Override
    public void groupSelected(String groupId){
        setMessagesFragment(groupId);
    }

    @Override
    public void groupLongClick(String groupId){
        setEditGroupFragment(groupId);
    }

    /************************** EDIT GROUP FRAGMENT METHODS **********************************/

    @Override
    public void addGroupMember(){
        checkPermissions(RC_ADD_MEMBER);
    }

    @Override
    public void deleteGroup() {
        onBackPressed();
    }

    @Override
    public void saveChanges() {
        onBackPressed();
    }

    /****************************** SERVICE METHODS *****************************************/

    @Override
    public void onMyLocationChanged(final Location location){
        if(location.isIndoor() && drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap!=null){
            LevelPickerFragment lpf = (LevelPickerFragment) fragmentManager.findFragmentByTag(LEVEL_FRAGMENT_TAG);
            if(lpf!=null)
                lpf.setLevelLocation(location.getFloorId());
            if(!currentBuildingId.equals(location.getBuildingId())){
                currentBuildingId = location.getBuildingId();
                //pb.show();

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                final LevelPickerFragment levelPickerFragment = new LevelPickerFragment();
                transaction.add(R.id.level_fragment_content,levelPickerFragment,LEVEL_FRAGMENT_TAG);
                transaction.commit();
                fragmentManager.executePendingTransactions();

                situmCommunicationManager.fetchBuildings(new Handler<Collection<Building>>() {
                    @Override
                    public void onSuccess(Collection<Building> buildings) {
                        for(Building building:buildings){
                            if(building.getIdentifier().equals(currentBuildingId)){
                                currentBuilding = building;

                                situmCommunicationManager.fetchFloorsFromBuilding(building, new Handler<Collection<Floor>>() {
                                    @Override
                                    public void onSuccess(Collection<Floor> floors) {
                                        currentFloors = floors;
                                        ArrayList<Floor> floorParam = new ArrayList<Floor>(floors);
                                        LevelPickerFragment levelPickerFragment1 = (LevelPickerFragment)fragmentManager.findFragmentByTag(LEVEL_FRAGMENT_TAG);
                                        if(levelPickerFragment!=null)
                                            levelPickerFragment.initFloorList(floorParam,location.getFloorId());
                                    }

                                    @Override
                                    public void onFailure(Error error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Error error) {

                    }
                });
            }
        } else {
            Fragment fragment = fragmentManager.findFragmentByTag(LEVEL_FRAGMENT_TAG);
            if(fragment!=null){
                fragmentManager.beginTransaction().remove(fragment).commit();
                fragmentManager.executePendingTransactions();
            }
        }
        locationChanged(location,dbManager.getId(),"","");
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

    @Override
    public void indoorEnabled(SitumAccount acc) {
        Snackbar.make(MainActivity.this.getCurrentFocus(),"Connected to Situm account: "+acc.getPublicName(),Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void indoorFailed(Error error) {
        Snackbar.make(MainActivity.this.getCurrentFocus(),"Situm account refused",Snackbar.LENGTH_SHORT).show();
    }

    /****************************** INTEREST POINT FRAGMENT METHODS *****************************************/

    @Override
    public void exitIpFragment(int mode) {
        onBackPressed();
        if(mode == 1) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            Snackbar.make(view, "Point deleted", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void copyInterestPoint(InterestPoint ip) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        Snackbar.make(view, "Point copied into your profile", Snackbar.LENGTH_LONG).show();
    }

    /****************************** INFOWINDOW CHAT ADAPTER METHODS *****************************************/

    @Override
    public void onInterestPointClicked(Message msg) {
        setIpFragment(msg.getUserIp(),msg.getIpId());
    }

    /******************************* INDOOR FRAGMENT LISTENER ************************************/

    @Override
    public void onSitumButtonClicked() {
        final LinearLayout ll = new LinearLayout(MainActivity.this);
        ll.setOrientation(LinearLayout.VERTICAL);
        final EditText et = new EditText(MainActivity.this);
        final EditText et2 = new EditText(MainActivity.this);
        final EditText et3 = new EditText(MainActivity.this);
        et.setHint("Situm account email");
        et2.setHint("Situm password");
        et3.setHint("Public name");
        et2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et2.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ll.addView(et);
        ll.addView(et2);
        ll.addView(et3);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Situm account")
                .setMessage("You are making this account a public Situm account. We recommend not to do it with your personal account.")
                .setView(ll)
                .setCancelable(false)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String email = et.getText().toString();
                        final String pwd = et2.getText().toString();
                        final String publicName = et3.getText().toString();
                        if (!email.equals("")&&!pwd.equals("")){
                            SitumSdk.init(MainActivity.this);
                            SitumSdk.configuration().setUserPass(email, pwd);
                            SitumSdk.communicationManager().validateUserCredentials(new Handler<Object>() {
                                @Override
                                public void onSuccess(Object o) {
                                    Snackbar.make(MainActivity.this.getCurrentFocus(),"Situm account accepted",Snackbar.LENGTH_SHORT).show();
                                    dbManager.addSitumAccount(email,pwd,publicName);
                                }

                                @Override
                                public void onFailure(Error error) {
                                    Snackbar.make(MainActivity.this.getCurrentFocus(),"Incorrect user or password",Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Snackbar.make(MainActivity.this.getCurrentFocus(),"email or password can't be empty",Snackbar.LENGTH_SHORT).show();
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

    @Override
    public void accountSelected(SitumAccount account) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("situmAccount",account.getEmail());
        editor.commit();

        if(pref.getBoolean("locationEnabled",false))
            locationFab.performClick();

        if(pref.getBoolean("IndoorLocation",false))
            mService.setIndoorState(true);
    }

    @Override
    public void levelPicked(Floor floor) {
        situmCommunicationManager.fetchMapFromFloor(floor, new Handler<Bitmap>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                Bounds drawBounds = currentBuilding.getBounds();
                Coordinate coordinateNE = drawBounds.getNorthEast();
                Coordinate coordinateSW = drawBounds.getSouthWest();
                LatLngBounds latLngBounds = new LatLngBounds(
                        new LatLng(coordinateSW.getLatitude(), coordinateSW.getLongitude()),
                        new LatLng(coordinateNE.getLatitude(), coordinateNE.getLongitude()));

                mGoogleMap.addGroundOverlay(new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .bearing((float) currentBuilding.getRotation().degrees())
                        .positionFromBounds(latLngBounds));
            }

            @Override
            public void onFailure(Error error) {

            }
        });
    }

    /***************************** DBMANAGER METHODS ****************************************/

    @Override
    public void signedIn(Location lastLocation) {
        Log.d(TAG,"DBMANAGER: signed in:  "+dbManager.isAuthenticated());
        pref.edit().putBoolean("profileCreated",true);
        if(!mBound){
            Intent intent = new Intent(this, es.udc.tfg.pruebafinalfirebase.mService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        cameraPosition = new CameraPosition(new LatLng(lastLocation.getLat(),lastLocation.getLng()),10,0,0);

        initView();



    }

    @Override
    public void signedOut() {
        Log.d(TAG,"DBMANAGER: signed out");
        initView();
    }

    @Override
    public void groupChanged(Group group) {
        Log.d(TAG,"DBMANAGER: group changed");
        Filter_fragment filterFragment = (Filter_fragment) fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
        Groups_fragment groupsFragment = (Groups_fragment) fragmentManager.findFragmentByTag(GROUPS_FRAGMENT_TAG);
        EditGroupFragment editGroupFragment = null;
        if(group!=null)
            editGroupFragment = (EditGroupFragment) fragmentManager.findFragmentByTag(EDIT_GROUP_FRAGMENT_TAG+group.getId());
        if(filterFragment!=null)
            filterFragment.updateFilter();
        if(groupsFragment!=null)
            groupsFragment.updateGroupList();
        if(editGroupFragment != null)
            editGroupFragment.setUI();

    }

    @Override
    public void noUserFound(String contact) {
        View view = this.getCurrentFocus();
        Snackbar.make(view, "User "+contact+" not found", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void locationReceived(String userId,String nick, String groupId, Location location) {
        Log.d(TAG,"DBMANAGER: location received");
        locationChanged(location,userId,nick,groupId);
    }

    @Override
    public void messageReceived(final String groupId, Message msg) {

        MessagesFragment messagesFragment = (MessagesFragment) fragmentManager.findFragmentByTag(MESSAGES_FRAGMENT_TAG+groupId);
        if(messagesFragment != null)
            messagesFragment.onMsgReceived(msg);

        if(msg.getSender().getMemberId().equals(dbManager.getId()))
            return;

        final String userId = msg.getSender().getMemberId();
        final int position = dbManager.findMarker(userId,groupId);
        final Marker m;

        if(position==-1){
            final MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(0, 0))
                    .title(msg.getSender().getNick())
                    .icon(BitmapDescriptorFactory.defaultMarker(Utils.stringToHueColor(groupId)));

            MapMarker mapMarker = new MapMarker(options,userId,groupId,MapMarker.LOCATION_MARKER,false,msg);
            if(drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap!=null){
                Marker marker = mGoogleMap.addMarker(options);
                marker.setVisible(false);
                marker.setTag(mapMarker.getMessages());
                mapMarker.setMarker(marker);
            }
            DBManager.mapMarkers.add(mapMarker);
        }else if(position >=0 && position<DBManager.mapMarkers.size()){
            MapMarker mm = DBManager.mapMarkers.get(position);
            mm.addMessage(msg);
            m = mm.getMarker();
            if(m!=null){
                m.setTag(mm.getMessages());
                m.showInfoWindow();

                long time;
                String infowindow_pref = appPreferences.getString(SettingsFragment.KEY_INFOWINDOW,"");
                if(infowindow_pref.equals(getString(R.string.preference_infowindow_5))){
                    time = 5000;
                    new CountDownTimer(time,time){

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            m.hideInfoWindow();
                        }
                    };
                } else if (infowindow_pref.equals(getString(R.string.preference_infowindow_10))){
                    time = 10000;
                    new CountDownTimer(time,time){

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            m.hideInfoWindow();
                        }
                    };
                }else if (infowindow_pref.equals(getString(R.string.preference_infowindow_15))){
                    time = 15000;
                    new CountDownTimer(time,time){

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            m.hideInfoWindow();
                        }
                    };
                }else if (infowindow_pref.equals(getString(R.string.preference_infowindow_indef))){

                } else {

                }


            }
        }

        if(mGoogleMap!=null && drawerFlag.equals(MAP_FRAGMENT_TAG) && msg.getType()== Message.TYPE_IP){
            InterestPoint ip = new InterestPoint(msg.getIpLat(),msg.getIpLng(),null,null,msg.getUserIp(),msg.getIpId());
            Marker mark = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(msg.getIpLat(),msg.getIpLng()))
                    .title(msg.getMsg()+"("+msg.getSender().getNick()+")")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pushpin_trimed2))
                    .anchor((float)0.3,1)
                    .visible(dbManager.isUserFiltered(msg.getUserIp())));
            mark.setTag(ip);
            otherIps.add(mark);
        }

        /*Log.d(TAG,"DBMANAGER: message received");
        final String Id = msg.getSender().getMemberId();
        if(drawerFlag.equals(MAP_FRAGMENT_TAG)){
            int position = findMarker(Id,groupId);
            if (position < mapMarkers.size()) {
                if (position >= 0) {
                    final Marker marker = mapMarkers.get(position).getMarker();
                    marker.setSnippet(msg.getMsg());
                    marker.setTag(msg);
                    marker.showInfoWindow();

                    new CountDownTimer(9000, 9000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            marker.hideInfoWindow();
                        }
                    }.start();
                }

                else{

                    final MarkerOptions options = new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .title(msg.getSender().getNick())
                            .icon(BitmapDescriptorFactory.defaultMarker(Utils.stringToHueColor(groupId)));

                    if(mGoogleMap!=null && drawerFlag.equals(MAP_FRAGMENT_TAG)){
                        Marker newMarker = mGoogleMap.addMarker(options);
                        newMarker.setVisible(false && dbManager.isFiltered(groupId));
                        mapMarkers.add(new MapMarker(newMarker,options, Id, groupId, MapMarker.LOCATION_MARKER,false,msg));
                    }else{
                        mapMarkers.add(new MapMarker(options, Id, groupId, MapMarker.LOCATION_MARKER,false,msg));
                    }

                }
            }
        }*/

    }

    @Override
    public void requestReceived(Request request) {
        Log.d(TAG,"DBMANAGER: request received");
        int n = dbManager.getPendingRequests().size();
        Fragment mainFragment = fragmentManager.findFragmentById(R.id.map_fragment_content);
        if(n!=0 && (mainFragment instanceof Groups_fragment || mainFragment instanceof SupportMapFragment))
            menuItemNotif.setVisible(true);
        notifications.setText(n+"");
        Notifications_fragment fragment = (Notifications_fragment) fragmentManager.findFragmentByTag(NOTIF_FRAGMENT_TAG);
        if(fragment!=null)
            fragment.updateNotifs();
    }

    @Override
    public void requestRemoved(){
        Log.d(TAG,"DBMANAGER: request removed");
        int n = dbManager.getPendingRequests().size();
        notifications.setText(n+"");
        Notifications_fragment fragment = (Notifications_fragment) fragmentManager.findFragmentByTag(NOTIF_FRAGMENT_TAG);
        if(fragment!=null)
            fragment.updateNotifs();
        if(n==0){
            fragmentManager.beginTransaction().remove(fragment).commit();
            fragmentManager.executePendingTransactions();
            menuItemNotif.setVisible(false);
        }

    }

    @Override
    public void noProfileAvailable() {
        pb.cancel();
        Log.d(TAG,"DBMANAGER: no profile available");
        final LinearLayout ll = new LinearLayout(MainActivity.this);
        ll.setOrientation(LinearLayout.VERTICAL);
        final EditText et = new EditText(MainActivity.this);
        final EditText et2 = new EditText(MainActivity.this);
        et.setHint("Phone number");
        et2.setHint("Nick");
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        ll.addView(et);
        ll.addView(et2);
        if(noProfileDialog == null)
            noProfileDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Phone Number & Nick")
                    .setView(ll)
                    .setCancelable(false)
                    .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG,"Next in dialog");
                            String phoneNumber = Utils.generateValidPhoneNumber(et.getText().toString());
                            String nick = et2.getText().toString();
                            if (!phoneNumber.equals("")&&!nick.equals("")){
                                dbManager.createProfile(phoneNumber,nick);
                            } else {
                                Toast.makeText(MainActivity.this,"Nick or phone number can't be empty",Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .create();
        if(!noProfileDialog.isShowing()){
            noProfileDialog.show();
        }

    }

    @Override
    public void initMsgList(String groupId, ArrayList<Message> messages) {
        MessagesFragment fragment = (MessagesFragment) fragmentManager.findFragmentByTag(MESSAGES_FRAGMENT_TAG+groupId);
        if (fragment!=null){
            fragment.initList(messages);
        }
    }

    @Override
    public void initInterestPoint(InterestPoint interestPoint, String userId, String ipId) {
        Log.d(TAG,"INICIANDO PUNTO DE INTERES");
        InterestPointFragment fragment = (InterestPointFragment) fragmentManager.findFragmentByTag(IP_FRAGMENT_TAG+userId+ipId);
        if(fragment!=null)
            fragment.onInterestPointReceived(interestPoint);
    }

    @Override
    public void interestPointAdded(InterestPoint ip) {
        Marker m = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(ip.getLat(), ip.getLng()))
                .title(ip.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pushpin_trimed))
                .draggable(true)
                .anchor((float)0.3,1)
                .visible(ipState));
        m.setTag(ip);
        ipMapMarkers.add(m);
    }

    @Override
    public void interestPointRemoved(InterestPoint ip) {
        for(Marker marker : ipMapMarkers){
            Object tag = marker.getTag();
            if(tag!=null) {
                InterestPoint interestPoint = (InterestPoint) tag;
                if(interestPoint.equals(ip)){
                    marker.remove();
                    ipMapMarkers.remove(marker);
                }
            }
        }
    }

    @Override
    public void destinationPointAdded(DestinationPoint p, String groupId) {
        int index = dbManager.findMarker(p.getIpId(),groupId);

        final MarkerOptions options = new MarkerOptions()
                .position(new LatLng(p.getLat(), p.getLng()))
                .title(p.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
                .draggable(true)
                .anchor((float)0.3,(float)0.9);

        if(index == -1){
            MapMarker mapMarker = new MapMarker(options,p.getIpId(),groupId,p.getDestinationTime(),MapMarker.DESTINATION_MARKER);
            if(mGoogleMap!=null && drawerFlag.equals(MAP_FRAGMENT_TAG)){
                Marker marker = mGoogleMap.addMarker(options);
                marker.setVisible(dbManager.isFiltered(groupId));
                marker.setTag(p);
                mapMarker.setMarker(marker);
            }
            DBManager.mapMarkers.add(mapMarker);
        }
    }

    @Override
    public void destinationPointChanged(DestinationPoint p, String groupId) {
        int index = dbManager.findMarker(p.getIpId(),groupId);

        final MarkerOptions options = new MarkerOptions()
                .position(new LatLng(p.getLat(), p.getLng()))
                .title(p.getName())
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
                .anchor((float)0.3,(float)0.9);

        if(index >=0 && index < DBManager.mapMarkers.size()){
            MapMarker mapMarker = DBManager.mapMarkers.get(index);
            mapMarker.setMarkerOptions(options);
            if(mGoogleMap!=null && drawerFlag.equals(MAP_FRAGMENT_TAG)){
                mapMarker.getMarker().setPosition(new LatLng(p.getLat(),p.getLng()));
                mapMarker.getMarker().setTitle(p.getName());
                mapMarker.getMarker().setTag(p);
                mapMarker.getMarker().hideInfoWindow();
                mapMarker.getMarker().showInfoWindow();
            }
        }
    }

    @Override
    public void destinationPointRemoved(DestinationPoint p, String groupId) {
        int index = dbManager.findMarker(p.getIpId(),groupId);

        if(index >=0 && index < DBManager.mapMarkers.size()){
            MapMarker mapMarker = DBManager.mapMarkers.get(index);
            if(mGoogleMap!=null && drawerFlag.equals(MAP_FRAGMENT_TAG)){
                mapMarker.getMarker().remove();
            }
            DBManager.mapMarkers.remove(index);
        }

    }

    @Override
    public void updateFilter() {

        for(MapMarker marker : DBManager.mapMarkers){
            Marker aux = marker.getMarker();
            if(aux!=null && !marker.getGroupId().equals("")) {
                Boolean bool = dbManager.isFiltered(marker.getGroupId());
                aux.setVisible(bool && marker.isActive());
                //marker.setMarker(aux);
            }
        }

        for(Marker marker : otherIps){
            InterestPoint ip = (InterestPoint) marker.getTag();
            Boolean bool = dbManager.isUserFiltered(ip.getUserId());
            marker.setVisible(bool);
        }

        Filter_fragment fragment = (Filter_fragment) fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
        if(fragment!=null){
            fragment.onResume();
        }
    }

    @Override
    public void initSitumAccountList(ArrayList<SitumAccount> situmAccounts) {
        IndoorFragment indoorFragment = (IndoorFragment) fragmentManager.findFragmentByTag(INDOOR_FRAGMENT_TAG);
        if(indoorFragment!=null){
            indoorFragment.setList(situmAccounts);
        }
    }

    @Override
    public void enableIndoor(SitumAccount account) {
        mService.enableIndoor(account);
    }

    /****************************** GOOGLE MAPS METHODS **************************************/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapLoadedCallback(this);
        googleMap.setInfoWindowAdapter(new mInfoWindowAdapter(getLayoutInflater(),getApplicationContext()));
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googleMap.setIndoorEnabled(false);
        googleMap.setOnInfoWindowClickListener(this);
        mGoogleMap = googleMap;
    }

    @Override
    public void onMapLoaded() {
        Log.d(TAG,"position: "+"map loaded");
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMarkerDragListener(this);
        mapLoaded = true;
        autoZoomFab.setEnabled(true);
        locationFab.setEnabled(true);


        for(MapMarker m: DBManager.mapMarkers){
            if(m!=null){
                MarkerOptions options = m.getMarkerOptions();
                Marker marker = mGoogleMap.addMarker(options);
                Log.d(TAG,m.getGroupId());
                marker.setVisible(dbManager.isFiltered(m.getGroupId())&& m.isActive());
                if(m.getType() == MapMarker.LOCATION_MARKER)
                    marker.setTag(m.getMessages());
                else if (m.getType() == MapMarker.DESTINATION_MARKER)
                    marker.setTag(new DestinationPoint(options.getPosition().latitude,options.getPosition().longitude,options.getTitle(),m.getId(),m.getDpHour()));

                m.setMarker(marker);
            }
        }

        User profile = dbManager.getProfile();
        if(profile.getInterestPoints()!=null)
            for(InterestPoint ip : dbManager.getProfile().getInterestPoints().values()){
                Marker m = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(ip.getLat(), ip.getLng()))
                        .title(ip.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pushpin_trimed))
                        .draggable(true)
                        .anchor((float)0.3,1)
                        .visible(ipState));
                m.setTag(ip);
                ipMapMarkers.add(m);
            }


        if(bundle!=null){
            String userId = bundle.getString("msgUserId","");
            String groupId = bundle.getString("msgGroupId","");
            Log.d("NOTIFICATIONLALA", userId + "    "+groupId);
            if(!userId.equals("") && !groupId.equals("")) {
                int i = dbManager.findMarker(userId,groupId);
                onMarkerClick(DBManager.mapMarkers.get(i).getMarker());
            }
            bundle = null;
        }
    }

    @Override
    public void onMapLongClick(final LatLng point){
        /*locationChanged(new Location(point.latitude,point.longitude,0),dbManager.getId(),dbManager.getNick(),"");*/
        //Toast.makeText(MainActivity.this,"lat: "+point.latitude+" lng: "+point.longitude,Toast.LENGTH_SHORT).show();

        final CharSequence[] items = {
                "Private interest point", "Destination point", "..."
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Create");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0){
                    final LinearLayout ll = new LinearLayout(MainActivity.this);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(7,7,7,7);
                    final EditText et = new EditText(MainActivity.this);
                    final EditText et2 = new EditText(MainActivity.this);
                    et.setHint("Name");
                    et2.setHint("Description");
                    ll.addView(et);
                    ll.addView(et2);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Creating interest point")
                            .setView(ll)
                            .setCancelable(true)
                            .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    InterestPoint ip = null;
                                    String name = et.getText().toString();
                                    String description = et2.getText().toString();
                                    if (!name.equals("")){
                                        ip = dbManager.createInterestPoint(name,description,point.latitude,point.longitude);
                                    } else {
                                        Snackbar.make(MainActivity.this.getCurrentFocus(),"Name field can't be empty",Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .show();
                }else if(item == 1){
                    final LinearLayout ll = new LinearLayout(MainActivity.this);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(7,7,7,7);
                    final EditText et = new EditText(MainActivity.this);
                    et.setHint("Name");
                    final EditText tv = new EditText(MainActivity.this);
                    tv.setHint("Select hour");
                    tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(hasFocus){
                                Calendar mcurrentTime = Calendar.getInstance();
                                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                                int minute = mcurrentTime.get(Calendar.MINUTE);
                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String h = String.valueOf(selectedHour);
                                        String m = String.valueOf(selectedMinute);
                                        if(selectedHour<10)
                                            h = "0"+h;
                                        if(selectedMinute<10)
                                            m = "0"+m;

                                        tv.setText( h + ":" + m);
                                        tv.clearFocus();
                                    }
                                }, hour, minute, true);
                                mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        tv.clearFocus();
                                    }
                                });
                                mTimePicker.setTitle("Select Time");
                                mTimePicker.show();
                            }
                        }
                    });
                    ll.addView(et);
                    ll.addView(tv);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Creating destination point")
                            .setView(ll)
                            .setCancelable(true)
                            .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    InterestPoint ip = null;
                                    String name = et.getText().toString();
                                    if (!name.equals("")){
                                        for(Map.Entry<Group,Boolean> entry : DBManager.mGroups.entrySet()){
                                            if(entry.getValue())
                                                dbManager.createDestinationPoint(name,entry.getKey().getId(),point.latitude,point.longitude,tv.getText().toString());
                                        }
                                    } else {
                                        Snackbar.make(MainActivity.this.getCurrentFocus(),"Name field can't be empty",Snackbar.LENGTH_SHORT).show();
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
                }else if(item == 2){
                    Toast.makeText(MainActivity.this, "Not available yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();
        Object tag = marker.getTag();
        if(tag instanceof ArrayList){
            for(Marker m : otherIps){
                m.remove();
            }
            otherIps.clear();

            ArrayList<Message> msgs = (ArrayList<Message>) tag;
            for(Message msg : msgs){
                if(msg.getType()==Message.TYPE_IP){
                    InterestPoint ip = new InterestPoint(msg.getIpLat(),msg.getIpLng(),null,null,msg.getUserIp(),msg.getIpId());
                    Marker mark = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(msg.getIpLat(),msg.getIpLng()))
                            .title(msg.getMsg()+"("+msg.getSender().getNick()+")")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pushpin_trimed2))
                            .anchor((float)0.3,1)
                            .visible(true));
                    mark.setTag(ip);
                    otherIps.add(mark);
                }

            }
        }
        if(tag instanceof InterestPoint) {

            QuickMsgFragment quickMsgFragment = (QuickMsgFragment) fragmentManager.findFragmentByTag(QUICKMSG_FRAGMENT_TAG);
            if (quickMsgFragment != null) {
                quickMsgFragment.addIp((InterestPoint)marker.getTag());
            }
        }

        long time;
        String infowindow_pref = appPreferences.getString(SettingsFragment.KEY_INFOWINDOW,"");
        if(infowindow_pref.equals(getString(R.string.preference_infowindow_5))){
            time = 5000;
        } else if (infowindow_pref.equals(getString(R.string.preference_infowindow_10))){
            time = 10000;
        }else if (infowindow_pref.equals(getString(R.string.preference_infowindow_15))){
            time = 15000;
        }else if (infowindow_pref.equals(getString(R.string.preference_infowindow_indef))){
            return true;
        } else {
            return true;
        }
        new CountDownTimer(time,time){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                marker.hideInfoWindow();
            }
        };
        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        dragInitPos = marker.getPosition();
        Log.d("MARKERDRAG","START");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d("MARKERDRAG","WHILE");
    }

    @Override
    public void onMarkerDragEnd(final Marker marker) {
        Log.d("MARKERDRAG","END");
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Do you want to change the point position?")
                .setCancelable(false)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Object tag = marker.getTag();
                        if(tag instanceof InterestPoint){
                            InterestPoint ip = (InterestPoint) marker.getTag();
                            ip.setLat(marker.getPosition().latitude);
                            ip.setLng(marker.getPosition().longitude);
                            dbManager.saveInterestPoint(ip.getIpId(),ip);
                        }else if(tag instanceof DestinationPoint){
                            DestinationPoint p = (DestinationPoint) marker.getTag();
                            p.setLat(marker.getPosition().latitude);
                            p.setLng(marker.getPosition().longitude);
                            dbManager.editDestinationPoint(groupIdMarker(p.getIpId()),p.getIpId(),p.getName(),p.getLat(),p.getLng(),p.getDestinationTime());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marker.setPosition(dragInitPos);
                        dialog.cancel();
                    }
                }).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Object tag = marker.getTag();
        removeSecondaryViews();
        if(tag instanceof ArrayList){

        } else if (tag instanceof InterestPoint){
            InterestPoint ip = (InterestPoint) tag;
            setIpFragment(ip.getUserId(),ip.getIpId());
        } else if (tag instanceof DestinationPoint){

            final DestinationPoint p = (DestinationPoint) tag;
            final LinearLayout ll = new LinearLayout(MainActivity.this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setPadding(7,7,7,7);
            final EditText et = new EditText(MainActivity.this);
            et.setText(p.getName());
            final EditText tv = new EditText(MainActivity.this);
            tv.setText(p.getDestinationTime());

            tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String h = String.valueOf(selectedHour);
                                String m = String.valueOf(selectedMinute);
                                if(selectedHour<10)
                                    h = "0"+h;
                                if(selectedMinute<10)
                                    m = "0"+m;

                                tv.setText( h + ":" + m);
                                tv.clearFocus();
                            }
                        }, hour, minute, true);
                        mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                tv.clearFocus();
                            }
                        });
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                }
            });

            ll.addView(et);
            ll.addView(tv);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Edit destination point name")
                    .setView(ll)
                    .setCancelable(true)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = et.getText().toString();
                            if (!name.equals("")){
                                dbManager.editDestinationPoint(groupIdMarker(p.getIpId()),p.getIpId(),name,p.getLat(),p.getLng(),tv.getText().toString());
                            } else {
                                Snackbar.make(MainActivity.this.getCurrentFocus(),"Name field can't be empty",Snackbar.LENGTH_SHORT).show();
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

    /********************************* ANDROID METHODS ****************************************/

    @Override
    public void onBackPressed() {
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if(backStackCount == 0){
            if(System.currentTimeMillis()-lastTimeBackPressed<3000)
                super.onBackPressed();
            else{
                lastTimeBackPressed = System.currentTimeMillis();
                Toast.makeText(MainActivity.this,"Press back again to exit",Toast.LENGTH_LONG).show();
            }
        } else {
            super.onBackPressed();

            Fragment frag = fragmentManager.findFragmentById(R.id.map_fragment_content);

            if(frag instanceof Groups_fragment)
                setGroupsFragment();
            else if(frag instanceof SupportMapFragment)
                setMapFragment();


            //ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"requestCode = "+requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        switch (requestCode){
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    Log.d(TAG,"recibo autenticación de google");
                    GoogleSignInAccount account2 = result.getSignInAccount();
                    dbManager.signIn(account2);
                } else {
                    // Google Sign In failed, update UI appropriately
                    // ...
                    pb.cancel();
                    Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
                break;

            case RC_PHONE_CONTACTS+RC_CREATE_GROUP:
            case RC_EMAIL_CONTACTS+RC_CREATE_GROUP:
            case RC_KEY_CONTACTS+RC_CREATE_GROUP:
                if(resultCode==RESULT_OK) {
                    final ArrayList<String> selectedContacts = data.getStringArrayListExtra("selectedContacts");

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
                                        dbManager.createGroup(name,selectedContacts);
                                    } else {
                                        Snackbar.make(MainActivity.this.getCurrentFocus(),"Name field can't be empty",Snackbar.LENGTH_SHORT).show();
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

                break;
            case RC_PHONE_CONTACTS+RC_ADD_MEMBER:
            case RC_EMAIL_CONTACTS+RC_ADD_MEMBER:
            case RC_KEY_CONTACTS+RC_ADD_MEMBER:
                if(resultCode==RESULT_OK) {
                    final ArrayList<String> selectedContacts = data.getStringArrayListExtra("selectedContacts");
                    EditGroupFragment fragment = (EditGroupFragment) fragmentManager.findFragmentById(R.id.map_fragment_content);
                    if (fragment != null)
                        fragment.membersAdded(selectedContacts);
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
            case RC_ADD_MEMBER:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions(RC_ADD_MEMBER);
                } else {
                    Toast.makeText(MainActivity.this,"Permissions denied",Toast.LENGTH_SHORT).show();
                }
                break;
            case RC_CREATE_GROUP:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions(RC_CREATE_GROUP);
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
        menuItemShare = menu.findItem(R.id.action_share);
        menuItemNotif = menu.findItem(R.id.action_notifications);
        menuItemIndoor = menu.findItem(R.id.action_indoor_switch);
        menuItemFilter = menu.findItem(R.id.action_filter);
        menuItemQuickmsg = menu.findItem(R.id.action_quickmsg);
        menuItemQuickmap = menu.findItem(R.id.action_quickMap);
        menuItemQuickmap.setVisible(false);
        menuItemNotif.setVisible(false);
        menuItemShare.setVisible(false);

        /*if(dbManager.isAuthenticated())

        else {
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemFilter.setVisible(false);
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
        }*/

        MenuItemCompat.setActionView(menu.findItem(R.id.action_notifications), R.layout.notification_badge);
        View count = menu.findItem(R.id.action_notifications).getActionView();
        notifications = (Button) count.findViewById(R.id.notif);
        int i = dbManager.getPendingRequests().size();
        if(i!=0)
            menuItemNotif.setVisible(true);
        if(notifications!=null)
            notifications.setText(i+"");
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
                    removeSecondaryViews();
                    notifications.setBackground(getResources().getDrawable(R.drawable.shape_notifs_clicked));
                    Notifications_fragment fragment = new Notifications_fragment();
                    transaction.replace(R.id.notif_fragment_content,fragment,NOTIF_FRAGMENT_TAG);
                    transaction.addToBackStack(NOTIF_FRAGMENT_TAG);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.commit();
                }
            }
        });

        MenuItemCompat.setActionView(menu.findItem(R.id.action_indoor_switch), R.layout.switch_actionbar);
        View aux = menu.findItem(R.id.action_indoor_switch).getActionView();
        indoor_switch = (Switch) aux.findViewById(R.id.switch_actionbar);
        indoor_switch.setChecked(pref.getBoolean("IndoorLocation",false));
        menuItemIndoor.setVisible(false);
        indoor_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(pref.getString("situmAccount",NO_ACC).equals(NO_ACC) && isChecked){
                    buttonView.setChecked(false);
                    Snackbar.make(MainActivity.this.getCurrentFocus(),"Select a Situm account before enable indoor location",Snackbar.LENGTH_LONG).show();
                } else {
                    if(pref.getBoolean("locationEnabled",false))
                        locationFab.performClick();
                    mService.setIndoorState(isChecked);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_log:
                pb.show();
                if(menuItemLog.getTitle().toString().equals("Sign in"))
                    login();
                else if(menuItemLog.getTitle().toString().equals("Sign out"))
                    logout();
                return true;*/
            case R.id.action_share:
                checkPermissions(RC_CREATE_GROUP);
                return true;
            case android.R.id.home:
                Fragment fragment1 = fragmentManager.findFragmentById(R.id.map_fragment_content);
                if(fragment1 instanceof EditGroupFragment || fragment1 instanceof MessagesFragment || fragment1 instanceof InterestPointFragment)
                    onBackPressed();
                else
                    mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_filter:
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Filter_fragment existFragment = (Filter_fragment) fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
                QuickMsgFragment quickMsgFragment = (QuickMsgFragment) fragmentManager.findFragmentByTag(QUICKMSG_FRAGMENT_TAG);
                if (existFragment != null){
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.remove(existFragment);
                    if(quickMsgFragment!=null)
                        transaction.remove(quickMsgFragment);
                    transaction.commit();
                    //fragmentManager.popBackStack();
                }else{
                    Filter_fragment fragment = Filter_fragment.newInstance(ipState);
                    transaction.add(R.id.filter_fragment_content,fragment,FILTER_FRAGMENT_TAG);
                    ///transaction.addToBackStack(FILTER_FRAGMENT_TAG);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();
                }
                fragmentManager.executePendingTransactions();
                return true;
            case R.id.action_quickmsg:
                FragmentTransaction qmTransaction = fragmentManager.beginTransaction();
                qmTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                Filter_fragment existingFilter = (Filter_fragment) fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
                QuickMsgFragment existingQuickmsg = (QuickMsgFragment) fragmentManager.findFragmentByTag(QUICKMSG_FRAGMENT_TAG);
                if(existingQuickmsg != null) {
                    Log.d(TAG,"remove QM");
                    qmTransaction.remove(existingQuickmsg);
                    if (existingFilter!=null){
                        Log.d(TAG,"remove F");
                        qmTransaction.remove(existingFilter);
                    }
                    //fragmentManager.popBackStack();
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } else {
                    if (existingFilter==null){
                        Log.d(TAG,"add F");
                        Filter_fragment newFilter = Filter_fragment.newInstance(ipState);
                        qmTransaction.add(R.id.filter_fragment_content,newFilter,FILTER_FRAGMENT_TAG);
                    }
                    Log.d(TAG,"add QM");
                    QuickMsgFragment newQuickMsg = new QuickMsgFragment();
                    qmTransaction.add(R.id.quickMsg_fragment_content,newQuickMsg,QUICKMSG_FRAGMENT_TAG);
                    //qmTransaction.addToBackStack(QUICKMSG_FRAGMENT_TAG);
                }
                qmTransaction.commit();
                fragmentManager.executePendingTransactions();
                return true;
            case R.id.action_quickMap:
                fragmentManager.popBackStack();
                setMapFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            if(autoZoomFab!=null)
                autoZoomFab.setVisibility(View.GONE);
            if(locationFab!=null)
                locationFab.setVisibility(View.GONE);
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            if(autoZoomFab!=null&&drawerFlag.equals(MAP_FRAGMENT_TAG))
                autoZoomFab.setVisibility(View.VISIBLE);
            if(locationFab!=null&&drawerFlag.equals(MAP_FRAGMENT_TAG))
                locationFab.setVisibility(View.VISIBLE);
        }
    }

}
