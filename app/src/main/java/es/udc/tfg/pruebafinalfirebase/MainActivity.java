package es.udc.tfg.pruebafinalfirebase;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import es.udc.tfg.pruebafinalfirebase.multipickcontact.MultiPickContactActivity;

public class MainActivity extends AppCompatActivity implements Notifications_fragment.OnNotifFragmentInteractionListener {

    private String TAG = "MainActiv";
    public static final String NOTIF_FRAGMENT_TAG = "NOTIF_FRAGMENT_TAG";
    public static final int RC_SIGN_IN = 777;
    public static final int RC_PHONE_CONTACTS = 888;
    public static final int RC_EMAIL_CONTACTS = 999;
    public static final int RC_KEY_CONTACTS = 6;
    public static final int PERMISSION_REQUEST_READ_CONTACTS = 333;
    public boolean initListeners = true;

    public ArrayList<String> myGroups = new ArrayList<>();

    private RelativeLayout main_content;
    private MenuItem menuItemLog,menuItemShare,menuItemNotif;
    private Menu menu;
    private Dialog pb;
    private ActionBar ab;

    FragmentManager fragmentManager;

    private FirebaseBackgroundListeners mService;
    private ServiceConnection mConnection;
    private boolean mBound;

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
        /**************************** INICILIZAR LAS VIEWS ********************************/
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();

        main_content = (RelativeLayout) findViewById(R.id.main_content);
        /*********************** INICILIZAR LOS ONCLICKLISTENERS **************************/


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
                                                    pb.show();
                                                    createProfile(phoneNumber,nick);
                                                }
                                            }
                                        })
                                        .show();
                            }else{
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
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void enableButtons(){
        pb.cancel();
        if (menu != null){
            menuItemLog.setEnabled(true);
            menuItemShare.setEnabled(true);
            menuItemNotif.setEnabled(true);
            menuItemShare.setVisible(true);
            menuItemNotif.setVisible(true);
        }
    }

    private void disableButtons(){
        pb.cancel();
        if (menu != null) {
            menuItemLog.setEnabled(true);
            menuItemNotif.setVisible(false);
            menuItemShare.setVisible(false);
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
        }

    }

    private void logout(){
        Log.d(TAG,"logout");
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }
    private void login(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void createProfile(String phoneNumber, String nick){
        User mProfile = new User(Utils.generateValidEmail(mUser.getEmail()),"",mUser.getUid(),phoneNumber,nick,new Location(),new ArrayList<InterestPoint>(),"",new ArrayList<String>());
        myProfileRef.setValue(mProfile);

        String requestsId = mProfile.getEmail()+mProfile.getPhoneNumber()+mProfile.getKey();
        database.getReference().child("publicIds").child(mProfile.getEmail()).setValue(requestsId);
        database.getReference().child("publicIds").child(mProfile.getPhoneNumber()).setValue(requestsId);
        if(!mProfile.getKey().equals(""))
            database.getReference().child("publicIds").child(mProfile.getKey()).setValue(requestsId);
    }

    private void initListeners(){
        Log.d(TAG,"init listeners");
        startService(new Intent(MainActivity.this,FirebaseBackgroundListeners.class));

        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                FirebaseBackgroundListeners.LocalBinder binder = (FirebaseBackgroundListeners.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                enableButtons();
                pb.cancel();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };

        Intent intent = new Intent(this, FirebaseBackgroundListeners.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        myRequestsRef = database.getReference().child("requests").child(mProfile.getEmail()+mProfile.getPhoneNumber());

        myProfileRef.child("groupsId").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                pb.show();
                Log.d(TAG,"GROUP ADDED");
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
                groupsRef.child(groupId).child("membersId").runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        ArrayList<GroupMember> members = mutableData.getValue(ArrayList.class);
                        for(GroupMember member : members){
                            if(member.getMemberId().equals(mUser.getUid()))
                                members.remove(member);
                        }
                        mutableData.setValue(members);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(TAG,"Transaction removing my id completed");
                    }
                });
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                        locationChanged(dataSnapshot.getValue(Location.class),member.getMemberId(),groupId);
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
        pb.cancel();
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
                    myRequestsRef.getParent().child(dataSnapshot.getValue(String.class)).child(mProfile.getNick()).setValue(new Request(groupId,Request.REQUEST_TYPE_GROUP));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG,"Cancelled: publicIds "+databaseError);
                }
            };
            publicIdsRef.child(contact).addListenerForSingleValueEvent(listener);
        }

    }

    private void groupNameChanged(String name, String groupId){}

    private void stateGroupChanged(int state,String groupId){}

    private void locationChanged(Location location, String userId, String groupId){}


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
                    Log.d(TAG,"SELECTED CONTACTS ARE: "+selectedContacts.toString());
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

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                } else {
                    Toast.makeText(MainActivity.this,"Permissions denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu,menu);
        menuItemLog = menu.findItem(R.id.action_log);
        menuItemShare = menu.findItem(R.id.action_share);
        menuItemNotif = menu.findItem(R.id.action_notifications);
        if(mUser!=null)
            menuItemLog.setTitle("Sign out");
        else {
            menuItemShare.setVisible(false);
            menuItemNotif.setVisible(false);
            menuItemShare.setEnabled(false);
            menuItemNotif.setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log:
                pb.show();
                if(menuItemLog.getTitle().toString().equals("Sign in"))
                    login();
                else if(menuItemLog.getTitle().toString().equals("Sign out"))
                    logout();
                return true;
            case R.id.action_share:
                checkPermissions();
                return true;
            case R.id.action_notifications:
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Notifications_fragment existFragment = (Notifications_fragment) fragmentManager.findFragmentByTag(NOTIF_FRAGMENT_TAG);
                if (existFragment != null){
                    menuItemNotif.setIcon(R.mipmap.ic_notif);
                    Log.d(TAG,"FRAGMENT EXISTS");
                    transaction.remove(existFragment);
                    transaction.commit();
                    fragmentManager.popBackStack();
                }else{
                    menuItemNotif.setIcon(R.mipmap.ic_notif_pressed);
                    Notifications_fragment fragment = new Notifications_fragment();
                    transaction.replace(R.id.fragment_content,fragment,NOTIF_FRAGMENT_TAG);
                    transaction.addToBackStack(NOTIF_FRAGMENT_TAG);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /****************************** INTERACTIONS WITH FRAGMENTS **************************/

    @Override
    public ArrayList<Request> getRequests() {
        Log.d(TAG,"getRequests... bound = "+mBound);
        if (mBound)
            return mService.getPendingRequests();
        return new ArrayList<>();
    }
}
