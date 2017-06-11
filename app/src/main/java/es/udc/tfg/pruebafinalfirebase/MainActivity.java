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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

import es.udc.tfg.pruebafinalfirebase.Group.EditGroupFragment;
import es.udc.tfg.pruebafinalfirebase.Group.Group;
import es.udc.tfg.pruebafinalfirebase.Group.GroupsRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.Group.Groups_fragment;
import es.udc.tfg.pruebafinalfirebase.Messages.Message;
import es.udc.tfg.pruebafinalfirebase.Messages.MessagesFragment;
import es.udc.tfg.pruebafinalfirebase.Filter.FilterRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.Filter.Filter_fragment;
import es.udc.tfg.pruebafinalfirebase.Messages.QuickMsgFragment;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.MultiPickContactActivity;
import es.udc.tfg.pruebafinalfirebase.Notifications.Notifications_fragment;
import es.udc.tfg.pruebafinalfirebase.Notifications.Request;

public class MainActivity extends AppCompatActivity implements Filter_fragment.OnFilterFragmentInteractionListener,Groups_fragment.OnGroupsFragmentInteractionListener,LoginFragment.OnLoginFragmentInteractionListener,QuickMsgFragment.OnQuickMsgFragmentInteractionListener,DBManager.DBManagerInteractions,EditGroupFragment.OnEditGroupFragmentInteractionListener,GoogleMap.OnMapLongClickListener,GroupsRecyclerViewAdapter.OnGroupsAdapterInteractionListener,FilterRecyclerViewAdapter.OnFilterAdapterInteractionListener,GoogleMap.OnMapLoadedCallback,OnMapReadyCallback, es.udc.tfg.pruebafinalfirebase.mService.OnServiceInteractionListener, GoogleMap.OnMarkerClickListener {

    private String TAG = "MainActiv";
    public static final String NOTIF_FRAGMENT_TAG = "NOTIF_FRAGMENT_TAG";
    public static final String MAP_FRAGMENT_TAG = "MAP_FRAGMENT_TAG";
    public static final String GROUPS_FRAGMENT_TAG = "GROUPS_FRAGMENT_TAG";
    public static final String FILTER_FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";
    public static final String EDIT_GROUP_FRAGMENT_TAG = "EDIT_GROUP_FRAGMENT_TAG";
    public static final String MESSAGES_FRAGMENT_TAG = "MESSAGES_FRAGMENT_TAG";
    public static final String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT_TAG";
    public static final String QUICKMSG_FRAGMENT_TAG = "QUICKMSG_FRAGMENT_TAG";
    public static final String LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT_TAG";
    public static final String IP_FRAGMENT_TAG = "IP_FRAGMENT_TAG";
    public static final int RC_SIGN_IN = 777;
    public static final int RC_PHONE_CONTACTS = 888;
    public static final int RC_EMAIL_CONTACTS = 999;
    public static final int RC_KEY_CONTACTS = 555;
    public static final int RC_CHECK_SETTINGS = 666;
    public static final int RC_CREATE_GROUP = 1;
    public static final int RC_ADD_MEMBER = 2;
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

    /*public ArrayList<String> myFilteredGroups = new ArrayList<>();
    public HashMap<String,Marker> markersHM = new HashMap<String,Marker>();*/
    private ArrayList<MapMarker> mapMarkers = new ArrayList<>();
    private ArrayList<Marker> ipMapMarkers = new ArrayList<>();
    private boolean ipState = false;

    private RelativeLayout main_content;
    private FloatingActionButton locationFab,autoZoomFab;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private MenuItem menuItemShare,menuItemNotif,menuItemFilter,menuItemQuickmsg,menuItemQuickmap;
    private Button notifications;
    private Menu menu;
    private Dialog pb;
    private ActionBar ab;

    public FragmentManager fragmentManager;
    public SharedPreferences pref;

    private es.udc.tfg.pruebafinalfirebase.mService mService;
    private ServiceConnection mConnection;
    private boolean mBound;

    private GoogleApiClient mGoogleAuthApiClient;
    private GoogleSignInOptions gso;
    private GoogleMap mGoogleMap;
    private String drawerFlag = "";

    private DBManager dbManager = DBManager.getInstance();

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


        /************************** INICIALIZAR VARIABLES ***************************/
        fragmentManager = getSupportFragmentManager();
        pref = getSharedPreferences("MYSERVICE", Context.MODE_PRIVATE);
        myLocationEnabled = pref.getBoolean("locationEnabled",false);
        myProfileCreated = pref.getBoolean("profileCreated",false);
        myServiceRunning = pref.getBoolean("serviceRunning",false);



        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                es.udc.tfg.pruebafinalfirebase.mService.LocalBinder binder = (es.udc.tfg.pruebafinalfirebase.mService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                mService.registerClient(MainActivity.this);

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

        /*********************** INICILIZAR LOS ONCLICKLISTENERS **************************/

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
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
                for (MapMarker m : mapMarkers){
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
        });

        /***********************  COMPROBAR ESTADO DE AUTENTICACIÓN **************************/
        if(!myServiceRunning)
            startService(new Intent(MainActivity.this, es.udc.tfg.pruebafinalfirebase.mService.class));
        dbManager.bindDBManager(MainActivity.this);

        MapsInitializer.initialize(getApplicationContext());
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this, es.udc.tfg.pruebafinalfirebase.mService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mService!=null)
            mService.disconnectService();
        if(mBound)
            unbindService(mConnection);
        mBound=false;
    }

    private void initView(){
        if(dbManager.isAuthenticated()){
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
        pb.cancel();
    }

    private void setMapFragment(){
        if (menu != null){
            menuItemShare.setEnabled(true);
            menuItemNotif.setEnabled(true);
            menuItemShare.setVisible(true);
            menuItemNotif.setVisible(true);
            menuItemFilter.setVisible(true);
            menuItemQuickmsg.setEnabled(true);
            menuItemQuickmsg.setVisible(true);
            menuItemQuickmap.setEnabled(false);
            menuItemQuickmap.setVisible(false);
        }
        locationFab.setVisibility(View.VISIBLE);
        autoZoomFab.setVisibility(View.VISIBLE);

        ab.show();
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("W'U");

        if(!drawerFlag.equals(MAP_FRAGMENT_TAG)) {
            SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.map_fragment_content, mMapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
            mMapFragment.getMapAsync(MainActivity.this);
        }

        pb.cancel();
    }

    private void setGroupsFragment(){
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
        }
        locationFab.setVisibility(View.GONE);
        autoZoomFab.setVisibility(View.GONE);

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
        }

        pb.cancel();
    }

    private void setEditGroupFragment(String groupId){
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
        }
        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);

        ab.setDefaultDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back);

        EditGroupFragment editGroupFragment = EditGroupFragment.newInstance(groupId);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_content, editGroupFragment, EDIT_GROUP_FRAGMENT_TAG+groupId);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        pb.cancel();
    }

    private void setMessagesFragment(String groupId){
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
        }
        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);

        ab.setDefaultDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back);

        MessagesFragment messagesFragment = MessagesFragment.newInstance(groupId);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_content, messagesFragment, MESSAGES_FRAGMENT_TAG+groupId);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        pb.cancel();
    }

    private void setIpFragment(String userId, String ipId){
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
        }
        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);

        ab.setDefaultDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back);

        InterestPointFragment ipFragment = InterestPointFragment.newInstance(ipId,userId);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_content, ipFragment, IP_FRAGMENT_TAG+userId+ipId);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        pb.cancel();
    }

    private void setSettingsFragment(){
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
        }
        locationFab.setVisibility(View.INVISIBLE);
        autoZoomFab.setVisibility(View.INVISIBLE);

        ab.setDisplayHomeAsUpEnabled(true);

        SettingsFragment settingsFragment = new SettingsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_content, settingsFragment, SETTINGS_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        pb.cancel();
    }

    private void removeSecondaryViews(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment filter = fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
        Fragment quickMsg = fragmentManager.findFragmentByTag(QUICKMSG_FRAGMENT_TAG);
        Fragment requests = fragmentManager.findFragmentByTag(NOTIF_FRAGMENT_TAG);
        if(filter!=null)
            transaction.remove(filter);
        if(quickMsg!=null)
            transaction.remove(quickMsg);
        if(requests!=null)
            transaction.remove(requests);
    }


    private void logout(){
        pb.show();
        Log.d(TAG,"logout");
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

    private void locationChanged(Location location, String userId,String nick, String groupId){
        Log.d(TAG,"position: "+location.getLat()+","+location.getLng()+"         "+userId+"       "+groupId+"   ACTIVE?"+location.isActive());

        int index = findMarker(userId,groupId);

        final MarkerOptions options = new MarkerOptions()
                .position(new LatLng(location.getLat(), location.getLng()))
                .title(nick)
                .icon(BitmapDescriptorFactory.defaultMarker(Utils.stringToHueColor(groupId)));


        if (index >= 0){
            MapMarker m = mapMarkers.get(index);
            Marker marker = m.getMarker();
            m.setMarkerOptions(options);
            m.setActive(location.isActive());
            if (marker!= null){
                marker.setPosition(new LatLng(location.getLat(), location.getLng()));
                marker.setVisible(m.isActive()&& dbManager.isFiltered(groupId));
            }else if (drawerFlag.equals(MAP_FRAGMENT_TAG) && mGoogleMap!=null){
                Marker newMarker = mGoogleMap.addMarker(options);
                newMarker.setVisible(m.isActive()&& dbManager.isFiltered(groupId));
                m.setMarker(newMarker);
            }

        }else{
            if(mGoogleMap!=null && drawerFlag.equals(MAP_FRAGMENT_TAG)){
                Marker newMarker = mGoogleMap.addMarker(options);
                newMarker.setVisible(location.isActive() && dbManager.isFiltered(groupId));
                mapMarkers.add(new MapMarker(newMarker,options, userId, groupId, MapMarker.LOCATION_MARKER,location.isActive()));
            }else{
                mapMarkers.add(new MapMarker(options, userId, groupId, MapMarker.LOCATION_MARKER,location.isActive()));
            }
        }
    }

    private int findMarker(String userId, String groupId){
        for (MapMarker marker : mapMarkers){
            if (marker.getGroupId().equals(groupId) && marker.getUserId().equals(userId)){
                return mapMarkers.indexOf(marker);
            }
        }
        return -1;
    }

    private String groupIdMarker(String markerId){
        for (MapMarker m : mapMarkers){
            if (m.getMarker().getId().equals(markerId)){
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
    public void quickMsgSent(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        String groups = "";
        for(Map.Entry<Group,Boolean> entry : DBManager.mGroups.entrySet()){
            if(entry.getValue()) {
                groups = groups + entry.getKey().getName();
                groups = groups + ", ";
            }
        }
        if(groups.equals(""))
            Snackbar.make(view, "No groups selected", Snackbar.LENGTH_SHORT).show();
        else{
            groups = groups.substring(0, groups.length() - 3);
            Snackbar.make(view, "Message sent to "+groups, Snackbar.LENGTH_LONG).show();
        }

    }

    /******************************* GROUPS FRAGMENT METHODS ***********************************/

    @Override
    public void addGroup(){
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


    /****************************** SERVICE METHODS *****************************************/

    @Override
    public void onMyLocationChanged(Location location){
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

    /***************************** DBMANAGER METHODS ****************************************/

    @Override
    public void signedIn() {
        Log.d(TAG,"DBMANAGER: signed in:  "+dbManager.isAuthenticated());
        pref.edit().putBoolean("profileCreated",true);
        if(!mBound){
            Intent intent = new Intent(this, es.udc.tfg.pruebafinalfirebase.mService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
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
        EditGroupFragment editGroupFragment = (EditGroupFragment) fragmentManager.findFragmentByTag(EDIT_GROUP_FRAGMENT_TAG+group.getId());
        if(filterFragment!=null)
            filterFragment.updateFilter();
        if(groupsFragment!=null)
            groupsFragment.updateGroupList();
        if(editGroupFragment != null)
            editGroupFragment.setUI();

    }

    @Override
    public void locationReceived(String userId,String nick, String groupId, Location location) {
        Log.d(TAG,"DBMANAGER: location received");
        locationChanged(location,userId,nick,groupId);
    }

    @Override
    public void messageReceived(final String groupId, Message msg) {
        Log.d(TAG,"DBMANAGER: message received");
        final String userId = msg.getSender().getMemberId();
        if(drawerFlag.equals(MAP_FRAGMENT_TAG)){
            int position = findMarker(userId,groupId);
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
            }
        }
        else{
            MessagesFragment messagesFragment = (MessagesFragment) fragmentManager.findFragmentByTag(MESSAGES_FRAGMENT_TAG+groupId);
            if(messagesFragment != null)
                messagesFragment.onMsgReceived(msg);
        }
    }

    @Override
    public void requestReceived(Request request) {
        Log.d(TAG,"DBMANAGER: request received");
        int n = dbManager.getPendingRequests().size();
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
        new AlertDialog.Builder(MainActivity.this)
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
                        }
                    }
                })
                .show();
    }

    @Override
    public void initMsgList(String groupId, ArrayList<Message> messages) {
        MessagesFragment fragment = (MessagesFragment) fragmentManager.findFragmentByTag(MESSAGES_FRAGMENT_TAG+groupId);
        if (fragment!=null){
            fragment.initList(messages);
        }
    }

    @Override
    public void updateFilter(){

        for(MapMarker marker : mapMarkers){
            Marker aux = marker.getMarker();
            if(aux!=null && !marker.getGroupId().equals("")) {
                Boolean bool = DBManager.mGroups.get(dbManager.findGroupById(marker.getGroupId()));
                Log.d(TAG, "isActive? " +bool);
                aux.setVisible(bool && marker.isActive());
                marker.setMarker(aux);
            }
        }
    }

    @Override
    public void initInterestPoint(InterestPoint interestPoint, String userId, String ipId) {
        InterestPointFragment fragment = (InterestPointFragment) fragmentManager.findFragmentByTag(IP_FRAGMENT_TAG+userId+ipId);
        if(fragment!=null)
            fragment.onInterestPointReceived(interestPoint);
    }


    /****************************** GOOGLE MAPS METHODS **************************************/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapLoadedCallback(this);
        googleMap.setInfoWindowAdapter(new mInfoWindowAdapter(getLayoutInflater()));
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Object tag = marker.getTag();
                if(tag instanceof Message){
                    Message msg = (Message) tag;
                    switch (msg.getType()){
                        case Message.TYPE_TEXT:
                            setMessagesFragment(groupIdMarker(marker.getId()));
                            break;
                        case Message.TYPE_IP:
                            Log.d(TAG,"MSG IP INFOW WINDOW CLICKED     " +msg.getUserIp()+"   "+msg.getIpId());
                            setIpFragment(msg.getUserIp(),msg.getIpId());
                            break;
                    }
                } else if (tag instanceof InterestPoint){
                    InterestPoint ip = (InterestPoint) tag;
                    setIpFragment(ip.getUserId(),ip.getIpId());
                }
            }
        });
        mGoogleMap = googleMap;
    }


    @Override
    public void onMapLoaded() {
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
        mapLoaded = true;
        autoZoomFab.setEnabled(true);
        locationFab.setEnabled(true);

        Location location = dbManager.getProfile().getLocation();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLat(),location.getLng()),3));


        for(MapMarker m: mapMarkers){
            if(m!=null){
                MarkerOptions options = m.getMarkerOptions();
                Marker marker = mGoogleMap.addMarker(options);
                Log.d(TAG,m.getGroupId());
                marker.setVisible(dbManager.isFiltered(m.getGroupId())&& m.isActive());
                m.setMarker(marker);
            }
        }

        User profile = dbManager.getProfile();
        if(profile.getInterestPoints()!=null)
            for(InterestPoint ip : dbManager.getProfile().getInterestPoints().values()){
                Marker m = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(ip.getLat(), ip.getLng()))
                        .title(ip.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pushpin))
                        .anchor((float)0.1,1)
                        .visible(ipState));
                m.setTag(ip);
                ipMapMarkers.add(m);
            }

        autoZoomFab.performClick();
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

                                    String name = et.getText().toString();
                                    String description = et2.getText().toString();
                                    if (!name.equals("")&&!description.equals("")){
                                        dbManager.createInterestPoint(name,description,point.latitude,point.longitude);
                                    }
                                }
                            })
                            .show();
                }else if(item == 1){
                    Toast.makeText(MainActivity.this,"Not available yet",Toast.LENGTH_SHORT).show();
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
        Object tag = marker.getTag();
        if(tag instanceof InterestPoint) {

        /*marker.showInfoWindow();
        new CountDownTimer(5000,5000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                marker.hideInfoWindow();
            }
        };*/

            QuickMsgFragment quickMsgFragment = (QuickMsgFragment) fragmentManager.findFragmentByTag(QUICKMSG_FRAGMENT_TAG);
            if (quickMsgFragment != null) {
                quickMsgFragment.addIp((InterestPoint)marker.getTag());
            }
        }
        return false;
    }

    /********************************* ANDROID METHODS ****************************************/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (drawerFlag.equals(MAP_FRAGMENT_TAG))
            setMapFragment();
        if (drawerFlag.equals(GROUPS_FRAGMENT_TAG))
            setGroupsFragment();

        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        menuItemFilter = menu.findItem(R.id.action_filter);
        menuItemQuickmsg = menu.findItem(R.id.action_quickmsg);
        menuItemQuickmap = menu.findItem(R.id.action_quickMap);
        menuItemQuickmap.setVisible(false);

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
        if(notifications!=null)
            notifications.setText(dbManager.getPendingRequests().size()+"");
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
                if(fragment1 instanceof EditGroupFragment || fragment1 instanceof MessagesFragment)
                    onBackPressed();
                else
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
                    Filter_fragment fragment = Filter_fragment.newInstance(ipState);
                    transaction.add(R.id.filter_fragment_content,fragment,FILTER_FRAGMENT_TAG);
                    transaction.addToBackStack(FILTER_FRAGMENT_TAG);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();
                }
                return true;
            case R.id.action_quickmsg:
                FragmentTransaction qmTransaction = fragmentManager.beginTransaction();
                qmTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                Filter_fragment existingFilter = (Filter_fragment) fragmentManager.findFragmentByTag(FILTER_FRAGMENT_TAG);
                QuickMsgFragment existingQuickmsg = (QuickMsgFragment) fragmentManager.findFragmentByTag(QUICKMSG_FRAGMENT_TAG);
                if(existingQuickmsg != null) {
                    qmTransaction.remove(existingFilter);
                    if (existingFilter!=null){
                        qmTransaction.remove(existingFilter);
                    }
                    qmTransaction.commit();
                    fragmentManager.popBackStack();
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } else {
                    if (existingFilter==null){
                        Filter_fragment newFilter = Filter_fragment.newInstance(ipState);
                        qmTransaction.add(R.id.filter_fragment_content,newFilter,FILTER_FRAGMENT_TAG);
                    }
                    QuickMsgFragment newQuickMsg = new QuickMsgFragment();
                    qmTransaction.add(R.id.quickMsg_fragment_content,newQuickMsg,QUICKMSG_FRAGMENT_TAG);
                    qmTransaction.addToBackStack(QUICKMSG_FRAGMENT_TAG);
                    qmTransaction.commit();
                }
                return true;
            case R.id.action_quickMap:
                fragmentManager.popBackStack();
                setMapFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
