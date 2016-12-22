package es.udc.tfg.pruebafinalfirebase;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.multipickcontact.MultiPickContactActivity;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActiv";
    public static final int RC_SIGN_IN = 777;
    public static final int RC_PHONE_CONTACTS = 888;
    public static final int RC_EMAIL_CONTACTS = 999;
    public static final int RC_KEY_CONTACTS = 6;
    public static final int PERMISSION_REQUEST_READ_CONTACTS = 333;
    public boolean initListeners = true;

    private Button signInButton,shareButton;
    private Dialog pb;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /************************ PANTALLA DE CARGA ************************************/
        pb = new Dialog(this, android.R.style.Theme_Black);
        View view = LayoutInflater.from(this).inflate(R.layout.progress_bar, null);
        pb.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pb.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pb.setContentView(view);
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
        /**************************** INICILIZAR LAS VIEWS ********************************/
        setContentView(R.layout.activity_main);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        shareButton = (Button) findViewById(R.id.share_button);
        /*********************** INICILIZAR LOS ONCLICKLISTENERS **************************/
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signInButton.getText().toString().equals("Sign in"))
                    login();
                else if(signInButton.getText().toString().equals("Sign out"))
                    logout();
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
        /***********************  COMPROBAR ESTADO DE AUTENTICACIÓN **************************/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mAuth = firebaseAuth;
                mUser = mAuth.getCurrentUser();
                if(mUser!=null){
                    enableButtons();
                    signInButton.setText("Sign out");
                    myProfileRef = database.getReference().child("users").child(mUser.getUid());
                    //CHECK IF THE PROFILE ALREADY EXISTS IN DB
                    myProfileRef.addValueEventListener(new ValueEventListener() {
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
                                if(initListeners){
                                    initListeners();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG,"Ref: "+myProfileRef.toString()+"  database error: "+databaseError);
                        }
                    });
                } else{
                    initListeners = true;
                    disableButtons();
                    signInButton.setText("Sign in");
                }

            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void enableButtons(){
        pb.cancel();
        signInButton.setEnabled(true);
        shareButton.setEnabled(true);
    }

    private void disableButtons(){
        pb.cancel();
        signInButton.setEnabled(true);
        shareButton.setEnabled(false);
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
        myRequestsRef = database.getReference().child("requests").child(requestsId);
        myRequestsRef.setValue(null);
        database.getReference().child("publicIds").child(mProfile.getEmail()).setValue(requestsId);
        database.getReference().child("publicIds").child(mProfile.getPhoneNumber()).setValue(requestsId);
        if(!mProfile.getKey().equals(""))
            database.getReference().child("publicIds").child(mProfile.getKey()).setValue(requestsId);
    }

    private void initListeners(){
        Log.d(TAG,"init listeners");
        initListeners = false;
        //BIND SERVICE, GROUPS LISTENERS, REQUEST LISTENERS

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

    private void createGroup(String name, ArrayList<String> selectedContacts){

        final ArrayList<String> foreignRequests = new ArrayList<>();
        for (String contact : selectedContacts){
            publicIdsRef.child(contact).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    foreignRequests.add(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG,"Cancelled: publicIds "+databaseError);
                }
            });
        }
        ArrayList<String> members = new ArrayList<>();
        members.add(mUser.getUid());
        final String groupId = mUser.getUid()+System.currentTimeMillis();
        groupsRef.child(groupId).setValue(new Group(name,members));
        myProfileRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User p = mutableData.getValue(User.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                p.addGroup(groupId);
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

        for (String requestId : foreignRequests){
            myRequestsRef.getParent().child(requestId).child(mProfile.getNick()).setValue(new Request(groupId,Request.REQUEST_TYPE_GROUP));
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

}
