package es.udc.tfg.pruebafinalfirebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import es.udc.tfg.pruebafinalfirebase.Group.Group;
import es.udc.tfg.pruebafinalfirebase.Group.GroupMember;
import es.udc.tfg.pruebafinalfirebase.Indoor.SitumAccount;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.DestinationPoint;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.InterestPoint;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.Point;
import es.udc.tfg.pruebafinalfirebase.Messages.Message;
import es.udc.tfg.pruebafinalfirebase.Notifications.Request;

/**
 * Created by Usuario on 22/02/2017.
 */
public class DBManager {
    private static final String DB_USER_REFERENCE = "users";
    private static final String DB_USER_LOCATION_REFERENCE = "location";
    private static final String DB_USER_REQUEST_REFERENCE = "request";
    private static final String DB_USER_GROUPS_REFERENCE = "groupsId";
    private static final String DB_USER_INTERESTPOINTS_REF = "interestPoints";
    private static final String DB_GROUPS_REFERENCE = "groups";
    private static final String DB_GROUPS_MEMBERS_REFERENCE = "membersId";
    private static final String DB_GROUPS_DESTINATIONS_REFERENCE = "destinationPoints";
    private static final String DB_MESSAGES_REFERENCE = "messages";
    private static final String DB_MESSAGES_OLDER_REFERENCE = "oldermessages";
    private static final String DB_REQUESTS_REFERENCE = "requests";
    private static final String DB_PUBLICID_REFERENCE = "publicIds";
    private static final String DB_SITUMACC_REFERENCE = "situmAccs";
    public static final int MODE_CREATE = 0;
    public static final int MODE_APPEND = 1;
    private static final String TAG = "DBManager";

    private static DBManager ourInstance = new DBManager();

    private DatabaseReference DBroot = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mProfileReference;
    private FirebaseAuth DBauth = FirebaseAuth.getInstance();
    private DBManagerInteractions mListener;
    private Context context;
    private SharedPreferences appPreferences;

    private ValueEventListener profileCheck;
    private ValueEventListener groupListener;
    private ValueEventListener userLocationListener;
    private ChildEventListener groupMembersListener;
    private ChildEventListener groupMsgListener;
    private ValueEventListener groupRequestListener;
    private ChildEventListener groupDestinationsListener;
    private FirebaseUser mUser;
    private User mProfile;

    public static ArrayList<Request> pendingRequests = new ArrayList<>();
    public static ArrayList<Group> mGroupsRequest = new ArrayList<>();
    public static LinkedHashMap<Group,Boolean> mGroups = new LinkedHashMap<>();
    public static ArrayList<MapMarker> mapMarkers = new ArrayList<>();
    //public static ArrayList<InterestPoint> mInterestPoints = new ArrayList<>();
    public Boolean authenticated = null;
    public Boolean listenersEnabled = false;
    private String lastMsgKey = "";
    private long lastMsgTime = 0;

    public static DBManager getInstance() {
        Log.d(TAG,"GET SINGLETON INSTANCE");
        return ourInstance;
    }

    /*********************************** AUTH METHODS ******************************************/

    private DBManager() {
        Log.d(TAG,"CREANDO DBMANAGER");
        DBauth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                Log.d(TAG, "ME CONECTO CON EL USUARIO "+mUser);

                if(mUser == null){
                    //desconectado
                    Log.d(TAG,"LOG OUT");
                    mProfileReference = null;
                    authenticated=false;
                    if(mListener!=null){
                        mListener.signedOut();
                    }
                }else{
                    //conectado
                    profileCheck = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                //existe perfil
                                authenticated = true;
                                mProfile = dataSnapshot.getValue(User.class);
                                if(mListener!=null)
                                    mListener.signedIn(mProfile.getLocation());
                                if(!listenersEnabled)
                                initListeners(mUser.getUid());
                            } else{
                                Log.d(TAG,"NO EXISTE PERFIL");
                                authenticated = false;
                                if(mListener!=null)
                                    mListener.noProfileAvailable();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).addListenerForSingleValueEvent(profileCheck);
                    DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                mProfile = dataSnapshot.getValue(User.class);

                                if (mProfile!=null)
                                    mProfileReference = DBroot.child(DB_USER_REFERENCE).child(mUser.getUid());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        /*Log.d(TAG,"create");
        DBauth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG,"auth listener");
                mUser = firebaseAuth.getCurrentUser();

                if (mUser!=null){
                    Log.d(TAG,"LOG IN");
                    DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){
                                Log.d(TAG, "SE EJECUTA TODO ESTO");
                                mProfile = dataSnapshot.getValue(User.class);
                                if(mProfile!=null){
                                    mProfileReference = DBroot.child(DB_USER_REFERENCE).child(mUser.getUid());
                                    if(mListener!=null){
                                        if(authenticated!=null){
                                            if(!authenticated)
                                                authenticated = true;
                                            mListener.signedIn();
                                        } else{
                                            mListener.signedIn();
                                        }
                                    }
                                    authenticated = true;
                                    initListeners(mUser.getUid());
                                }
                            }else{
                                if(mListener!=null){
                                    mListener.noProfileAvailable();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    Log.d(TAG,"LOG OUT");
                    mProfile = null;
                    mProfileReference = null;
                    authenticated=false;
                    if(mListener!=null){
                        Log.d(TAG,"LOG OUT,,, listener: "+mListener.toString());
                        mListener.signedOut();
                    }
                }
            }
        });*/
    }

    private void initListeners(String id){
        Log.d(TAG,"INIT listeners");

        DBroot.child(DB_USER_REFERENCE).child(id).child(DB_USER_GROUPS_REFERENCE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final String groupId = dataSnapshot.getValue(String.class);
                Log.d(TAG,"GROUP ADDED WITH ID "+groupId);
                if (groupId!=null){
                    groupListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String id = dataSnapshot.getKey();
                                Group group = dataSnapshot.getValue(Group.class);
                                if (group!=null){
                                    group.setId(id);
                                    updateGroupList(group);
                                    if(mListener!=null)
                                        mListener.groupChanged(group);
                                    //if(mListener!=null)
                                        //mListener.groupChanged(group);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    groupMembersListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.exists()){
                                final GroupMember member = dataSnapshot.getValue(GroupMember.class);
                                if(member!=null && !member.getMemberId().equals(mUser.getUid())){
                                    userLocationListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                Location location = dataSnapshot.getValue(Location.class);
                                                if(location!=null && mListener!=null){
                                                    mListener.locationReceived(member.getMemberId(),member.getNick(),groupId,location);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    };
                                    DBroot.child(DB_USER_REFERENCE).child(member.getMemberId()).child(DB_USER_LOCATION_REFERENCE).addValueEventListener(userLocationListener);
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                final GroupMember member = dataSnapshot.getValue(GroupMember.class);
                                if(member!=null && userLocationListener!=null){
                                    DBroot.child(DB_USER_REFERENCE).child(member.getMemberId()).child(DB_USER_LOCATION_REFERENCE).removeEventListener(userLocationListener);
                                }
                            }
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    groupMsgListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.exists() && dataSnapshot.getKey()!=lastMsgKey){
                                Message msg = dataSnapshot.getValue(Message.class);
                                if (msg!=null && mListener!=null){
                                    Log.d(TAG,"UN MSG RECIBIDO: "+msg.getMsg());
                                    lastMsgKey = dataSnapshot.getKey();

                                    if(msg.getTime()>lastMsgTime){
                                        lastMsgTime = msg.getTime();
                                        Group auxgroup = findGroupById(groupId);
                                        boolean auxbool = mGroups.get(auxgroup);
                                        LinkedHashMap auxMap = (LinkedHashMap) mGroups.clone();
                                        mGroups.clear();
                                        mGroups.put(auxgroup,auxbool);
                                        mGroups.putAll(auxMap);
                                    }

                                    mListener.messageReceived(groupId,msg);
                                }
                            }
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
                    groupDestinationsListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.exists()){
                                DestinationPoint p = dataSnapshot.getValue(DestinationPoint.class);
                                if(p!=null && mListener!=null)
                                    mListener.destinationPointAdded(p,groupId);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.exists()){
                                DestinationPoint p = dataSnapshot.getValue(DestinationPoint.class);
                                if(p!=null && mListener!=null)
                                    mListener.destinationPointChanged(p,groupId);
                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                DestinationPoint p = dataSnapshot.getValue(DestinationPoint.class);
                                if(p!=null && mListener!=null)
                                    mListener.destinationPointRemoved(p,groupId);
                            }
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).addValueEventListener(groupListener);
                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).child(DB_GROUPS_MEMBERS_REFERENCE).addChildEventListener(groupMembersListener);
                    DBroot.child(DB_MESSAGES_REFERENCE).child(groupId).child(DB_MESSAGES_OLDER_REFERENCE).limitToLast(appPreferences==null?15:Integer.parseInt(appPreferences.getString(SettingsFragment.KEY_MESSAGES,"15"))).addChildEventListener(groupMsgListener);
                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).child(DB_GROUPS_DESTINATIONS_REFERENCE).addChildEventListener(groupDestinationsListener);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG,"GROUP REMOVED");
                String groupId = dataSnapshot.getValue(String.class);
                if (groupId!=null){
                    mListener.groupChanged(null);
                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).removeEventListener(groupListener);
                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).child(DB_GROUPS_MEMBERS_REFERENCE).removeEventListener(groupMembersListener);
                    DBroot.child(DB_MESSAGES_REFERENCE).child(groupId).child(DB_MESSAGES_OLDER_REFERENCE).removeEventListener(groupMsgListener);
                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).child(DB_GROUPS_DESTINATIONS_REFERENCE).removeEventListener(groupDestinationsListener);

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DBroot.child(DB_REQUESTS_REFERENCE).child(mProfile.getRequest()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    Request request = dataSnapshot.getValue(Request.class);
                    if (request != null){
                        if(request.getType()==Request.REQUEST_TYPE_DELETED){
                            ArrayList<String> aux = mProfile.getGroupsId();
                            if(aux!=null){
                                if(aux.contains(request.getIdGroup())) {
                                    aux.remove(request.getIdGroup());
                                    removeGroup(request.getIdGroup());
                                    DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_GROUPS_REFERENCE).setValue(aux);
                                }
                            }else if(findRequestByGroup(request.getIdGroup())!=null){
                                DBroot.child(DB_REQUESTS_REFERENCE).child(mProfile.getRequest()).child(findRequestByGroup(request.getIdGroup()).getId()).removeValue();
                            }
                            DBroot.child(DB_REQUESTS_REFERENCE).child(mProfile.getRequest()).child(request.getId()).removeValue();
                        }else {
                            DBroot.child(DB_GROUPS_REFERENCE).child(request.getIdGroup()).runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    Group group = mutableData.getValue(Group.class);
                                    if (group==null)
                                        return Transaction.success(mutableData);
                                    else {
                                        group.addInvitation(new GroupMember(1,mUser.getUid(),mProfile.getNick()));
                                        updateRequestGroup(group);
                                        mutableData.setValue(group);
                                        return Transaction.success(mutableData);
                                    }
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                }
                            });

                            groupRequestListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        String id = dataSnapshot.getKey();
                                        Group group = dataSnapshot.getValue(Group.class);
                                        if(group!=null) {
                                            group.setId(id);
                                            updateRequestGroup(group);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            //DBroot.child(DB_GROUPS_REFERENCE).child(request.getIdGroup()).addListenerForSingleValueEvent(groupRequestListener);
                            updatePendingRequests(request);
                            if (mListener != null)
                                mListener.requestReceived(request);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Request request = dataSnapshot.getValue(Request.class);
                    if(request!=null && request.getType()==Request.REQUEST_TYPE_GROUP){
                        DBroot.child(DB_GROUPS_REFERENCE).child(request.getIdGroup()).runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Group group = mutableData.getValue(Group.class);
                                if (group==null)
                                    return Transaction.success(mutableData);
                                else {
                                    group.removeInvitation(mUser.getUid());
                                    mutableData.setValue(group);
                                    return Transaction.success(mutableData);
                                }
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });


                        //DBroot.child(DB_USER_GROUPS_REFERENCE).child(request.getIdGroup()).removeEventListener(groupRequestListener);
                        mGroupsRequest.remove(findGroupByRequest(request.getIdGroup()));
                        removeRequest(request);
                        if(mListener!=null)
                            mListener.requestRemoved();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DBroot.child(DB_USER_REFERENCE).child(id).child(DB_USER_INTERESTPOINTS_REF).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                InterestPoint ip = null;
                if(dataSnapshot.exists())
                    ip = dataSnapshot.getValue(InterestPoint.class);
                if(ip!=null){
                    if (mListener != null)
                        mListener.interestPointAdded(ip);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                InterestPoint ip = null;
                if(dataSnapshot.exists())
                    ip = dataSnapshot.getValue(InterestPoint.class);
                if(ip!=null){
                    //mInterestPoints.remove(ip);
                    if (mListener != null)
                        mListener.interestPointRemoved(ip);
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listenersEnabled = true;
    }

    private void updateGroupList(Group group){
        if(!mGroups.isEmpty()) {
            ArrayList<Group> aux = new ArrayList<>(mGroups.keySet());
            for (Group g : aux) {
                if (g.getId().equals(group.getId())) {
                    mGroups.put(group, mGroups.get(g));
                    mGroups.remove(g);
                    return;
                }
            }
        }
        mGroups.put(group, false);

        /*Boolean found = false;
        Iterator it = mGroups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Group aux = (Group) pair.getKey();
            if(aux.getId().equals(group.getId())) {
                mGroups.put(group, (Boolean) pair.getValue());
                mGroups.remove(aux);
                found = true;
            }
        }
        if(!found)
            mGroups.put(group,true);*/
    }

    private void updateRequestGroup(Group group){
        for(Group gr : mGroupsRequest){
            if (gr != null)
                if(gr.getId().equals(group.getId())){
                    mGroupsRequest.remove(gr);
                    mGroupsRequest.add(group);
                    return;
                }
        }
        mGroupsRequest.add(group);
    }

    private void updatePendingRequests(Request request){
        for(Request req :pendingRequests){
            if(req!=null)
                if(req.getId().equals(request.getId())){
                    pendingRequests.remove(req);
                    pendingRequests.add(request);
                    return;
                }
        }
        pendingRequests.add(request);
    }

    private void removeGroup(String groupId){
        ArrayList<Group> aux = new ArrayList<>(mGroups.keySet());
        for(Group g : aux){
            if (g.getId().equals(groupId)){
                mGroups.remove(g);
                return;
            }
        }
    }

    private Request findRequestByGroup(String groupId) {
        for (Request r : pendingRequests) {
            if (r.getIdGroup().equals(groupId) && r.getType()==Request.REQUEST_TYPE_GROUP) {
                return r;
            }

        }
        return null;
    }

    private void removeRequest(Request request){
        for(Request r : pendingRequests){
            if(r.getId().equals(request.getId())) {
                pendingRequests.remove(r);
                return;
            }

        }
    }

    public void bindDBManager(Context context, int mode){
        this.context = context;

        appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (context instanceof DBManagerInteractions) {
            mListener = (DBManagerInteractions) context;
            Log.d(TAG,"mListener = "+mListener.toString());
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DBManagerInteractions");
        }
        if(authenticated != null && mode==MODE_CREATE) {
            if (authenticated)
                mListener.signedIn(mProfile.getLocation());
            else
                mListener.signedOut();
        }
    }

    public String getDbManagerListenerContext(){
        if(mListener instanceof MainActivity){
            return MainActivity.TAG;
        } else if (mListener instanceof mService){
            return mService.TAG;
        }
        return "";
    }

    public void signIn(GoogleSignInAccount googlekey){
        Log.d(TAG,"signin");
        AuthCredential credential = GoogleAuthProvider.getCredential(googlekey.getIdToken(), null);
        DBauth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                        }
                    }
                });
    }

    public void signOut(){
        disableMyLocation();
        DBauth.signOut();
    }

    public boolean isAuthenticated(){
        if(mUser!=null && authenticated!=null){
            return authenticated;
        }else{
            return false;
        }
    }

    public String getId(){
        if(mUser!=null){
            return mUser.getUid();
        }else{
            return null;
        }
    }

    public String getNick(){
        if(mProfile!=null)
            return mProfile.getNick();
        else
            return "noNick";
    }

    /*********************************** PROFILE METHODS ******************************************/

    public void createProfile(String phoneNumber, String nick){
        String email = Utils.generateValidEmail(mUser.getEmail());
        String phone = Utils.generateValidPhoneNumber(phoneNumber);
        String requestPath = DBroot.child(DB_REQUESTS_REFERENCE).push().getKey();

        DBroot.child(DB_PUBLICID_REFERENCE).child(email).setValue(requestPath);
        DBroot.child(DB_PUBLICID_REFERENCE).child(phone).setValue(requestPath);
        User newProfile = new User(email,"",mUser.getUid(),phone,nick,new Location(),"",new ArrayList<String>(),requestPath);
        DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).setValue(newProfile);

        DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).addListenerForSingleValueEvent(profileCheck);
    }

    public User getProfile(){
        return mProfile;
    }

    public ArrayList<Request> getPendingRequests(){
        return pendingRequests;
    }

    public void setLocation(Location location){
        if(mUser!=null)
            DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_LOCATION_REFERENCE).setValue(location);
    }

    public ArrayList<String> getGroupsId(){
        return mProfile.getGroupsId();
    }

    public void disableMyLocation(){
        if(mProfile!=null && mUser!=null){
            Location aux = mProfile.getLocation();
            aux.setActive(false);
            DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_LOCATION_REFERENCE).setValue(aux);
        }
    }

    /*********************************** GROUPS METHODS ******************************************/

    public void createGroup(String name, ArrayList<String> contacts){
        ArrayList<GroupMember> members = new ArrayList<>();
        ArrayList<String> admins = new ArrayList<>();
        members.add(new GroupMember(Group.GROUP_STATE_ACTIVE,mUser.getUid(),mProfile.getNick()));
        admins.add(mUser.getUid());
        final String groupId = DBroot.child(DB_GROUPS_REFERENCE).push().getKey();
        DBroot.child(DB_GROUPS_REFERENCE).child(groupId).setValue(new Group(name,groupId,members,admins,null));

        ArrayList<String> mGroups = mProfile.getGroupsId();
        if(mGroups == null)
            mGroups = new ArrayList<>();
        mGroups.add(groupId);
        mProfileReference.child(DB_USER_GROUPS_REFERENCE).setValue(mGroups);

        for(String aux : contacts){
            final String contact = aux;
            DBroot.child(DB_PUBLICID_REFERENCE).child(contact).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String requestId = dataSnapshot.getValue(String.class);
                        if(requestId != null){
                            DatabaseReference reqRef = DBroot.child(DB_REQUESTS_REFERENCE).child(requestId).push();
                            String reqId = reqRef.getKey();
                            reqRef.setValue(new Request(groupId,Request.REQUEST_TYPE_GROUP,reqId));
                        }
                    } else if(mListener!=null)
                        mListener.noUserFound(contact);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void joinGroup(String groupId){
        DBroot.child(DB_GROUPS_REFERENCE).child(groupId).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Group group = mutableData.getValue(Group.class);
                if (group==null)
                    return Transaction.success(mutableData);
                else {
                    group.addMember(new GroupMember(1,mUser.getUid(),mProfile.getNick()));
                    mutableData.setValue(group);
                    return Transaction.success(mutableData);
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        ArrayList<String> mGroups = mProfile.getGroupsId();
        if(mGroups == null)
            mGroups = new ArrayList<>();
        mGroups.add(groupId);
        mProfileReference.child(DB_USER_GROUPS_REFERENCE).setValue(mGroups);
    }

    public LinkedHashMap<Group,Boolean> getMyGroups(){
        return mGroups;
    }

    public Group findGroupById(String id){
        for(Group group : mGroups.keySet()){
            if(group.getId().equals(id))
                return group;
        }
        return null;
    }

    public Group findGroupByRequest (String id){
        for(Group group : mGroupsRequest){
            Log.d(TAG,mGroupsRequest.toString());
            if(group.getId().equals(id))
                return group;
        }
        return null;
    }

    public Boolean isFiltered(String groupId){
        if(groupId.equals(""))
            return true;
        Group group = findGroupById(groupId);
        if(group!= null)
            return mGroups.get(group);
        else
            return false;
    }

    public Boolean isUserFiltered(String id){
        for(Group group : mGroups.keySet()){
            for(GroupMember member : group.getMembersId()){
                if(member.getMemberId().equals(id))
                    return mGroups.get(group);
            }
        }
        return false;
    }

    public void acceptRequest(Request request){
        joinGroup(request.getIdGroup());
        DBroot.child(DB_REQUESTS_REFERENCE).child(mProfile.getRequest()).child(request.getId()).removeValue();
    }

    public void cancelRequest(Request request){
        DBroot.child(DB_REQUESTS_REFERENCE).child(mProfile.getRequest()).child(request.getId()).removeValue();
    }

    public void initMsgList(final String groupId, int i){
        final ArrayList<Message> result = new ArrayList<>();
        Query query = DBroot.child(DB_MESSAGES_REFERENCE).child(groupId).child(DB_MESSAGES_OLDER_REFERENCE).limitToLast(Integer.parseInt(appPreferences.getString(SettingsFragment.KEY_MESSAGES,"15"))+i);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot children : dataSnapshot.getChildren()){
                    Message msg = children.getValue(Message.class);
                    if(msg!=null)
                        result.add(msg);
                }
                if(mListener!=null){
                    mListener.initMsgList(groupId,result);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMsg(String msg, String groupId,int type,InterestPoint interestPoint){

        if(interestPoint == null)
            DBroot.child(DB_MESSAGES_REFERENCE).child(groupId).child(DB_MESSAGES_OLDER_REFERENCE).push().setValue(new Message(new GroupMember(1,mUser.getUid(),mProfile.getNick()),msg,type));
        else
            DBroot.child(DB_MESSAGES_REFERENCE).child(groupId).child(DB_MESSAGES_OLDER_REFERENCE).push().setValue(new Message(new GroupMember(1,mUser.getUid(),mProfile.getNick()),msg,interestPoint.getIpId(),interestPoint.getUserId(),interestPoint.getLat(),interestPoint.getLng(),type));

    }

    public void setFilter(Group group, Boolean filter){
        if(!appPreferences.getBoolean(SettingsFragment.KEY_FILTER,false)){
            for(Group g : mGroups.keySet()){
                mGroups.put(g,false);
            }
        }
        mGroups.put(group,filter);
        if(mListener!=null)
            mListener.updateFilter();
    }

    public void updateGroup(Group group){
        DBroot.child(DB_GROUPS_REFERENCE).child(group.getId()).setValue(group);
    }

    public void exitGroup(final String groupId){
        ArrayList<String> aux = new ArrayList<String>();
        aux.add(mUser.getUid());
        removeMember(groupId,aux);
    }

    public void addMembers(final String groupId, ArrayList<String> members){
        for(String aux : members){
            final String contact = aux;
            DBroot.child(DB_PUBLICID_REFERENCE).child(contact).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String requestId = dataSnapshot.getValue(String.class);
                        if(requestId != null){
                            DatabaseReference reqRef = DBroot.child(DB_REQUESTS_REFERENCE).child(requestId).push();
                            String reqId = reqRef.getKey();
                            reqRef.setValue(new Request(groupId,Request.REQUEST_TYPE_GROUP,reqId));
                        }
                    } else if(mListener!=null)
                        mListener.noUserFound(contact);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void removeMember(final String groupId, final ArrayList<String> userIds){
        for(String userId : userIds) {
            DBroot.child(DB_USER_REFERENCE).child(userId).child(DB_USER_REQUEST_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String requestId = dataSnapshot.getValue(String.class);
                    DatabaseReference reqRef = DBroot.child(DB_REQUESTS_REFERENCE).child(requestId).push();
                    String reqId = reqRef.getKey();
                    reqRef.setValue(new Request(groupId, Request.REQUEST_TYPE_DELETED, reqId));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Group group = findGroupById(groupId);
            group.removeMember(userId);
            DBroot.child(DB_GROUPS_REFERENCE).child(groupId).setValue(group);
        }
    }

    public void cancelInvitation(final String groupId, final ArrayList<String> userIds){
        for(String userId : userIds) {
            DBroot.child(DB_USER_REFERENCE).child(userId).child(DB_USER_REQUEST_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String requestId = dataSnapshot.getValue(String.class);
                    DatabaseReference reqRef = DBroot.child(DB_REQUESTS_REFERENCE).child(requestId).push();
                    String reqId = reqRef.getKey();
                    reqRef.setValue(new Request(groupId, Request.REQUEST_TYPE_DELETED, reqId));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            /*Group group = findGroupById(groupId);
            group.removeMember(Id);
            DBroot.child(DB_GROUPS_REFERENCE).child(groupId).setValue(group);*/
        }
    }

    /*********************************** INTEREST POINTS METHODS ******************************************/

    public int findMarker(String userId, String groupId){
        for (MapMarker marker : mapMarkers){
            if(marker.getGroupId()!=null && marker.getId()!=null) {
                if (marker.getGroupId().equals(groupId) && marker.getId().equals(userId))
                    return mapMarkers.indexOf(marker);
            }
        }
        return -1;
    }

    public InterestPoint createInterestPoint(String name, String description, double lat, double lng){
        DatabaseReference iPref = mProfileReference.child(DB_USER_INTERESTPOINTS_REF).push();
        InterestPoint ip = new InterestPoint(lat,lng,name,description,mUser.getUid(),iPref.getKey());
        iPref.setValue(ip);
        return ip;
    }

    public InterestPoint getInterestPoint(final String userId, final String ipId){
        if(mProfile.getInterestPoints()!=null) {
            InterestPoint point = mProfile.getInterestPoints().get(ipId);
            if (point != null)
                return point;
        }
        DBroot.child(DB_USER_REFERENCE).child(userId).child(DB_USER_INTERESTPOINTS_REF).child(ipId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListener.initInterestPoint(dataSnapshot.getValue(InterestPoint.class), userId, ipId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return null;
    }

    public void saveInterestPoint(String ipId, InterestPoint ip){
        String name = ip.getName();
        String description = ip.getDescription();
        double lat = ip.getLat();
        double lng = ip.getLng();
        DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_INTERESTPOINTS_REF).child(ipId).child("name").setValue(name);
        DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_INTERESTPOINTS_REF).child(ipId).child("description").setValue(description);
        DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_INTERESTPOINTS_REF).child(ipId).child("lat").setValue(lat);
        DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_INTERESTPOINTS_REF).child(ipId).child("lng").setValue(lng);
    }

    public void rateInterestPoint(String userId, String ipId, Float rate){
        DBroot.child(DB_USER_REFERENCE).child(userId).child(DB_USER_INTERESTPOINTS_REF).child(ipId).child("rating").child(mUser.getUid()).setValue(rate);

    }

    public void deleteInterestPoint(String userId, String ipId){
        if(userId.equals(mUser.getUid())){
            DBroot.child(DB_USER_REFERENCE).child(userId).child(DB_USER_INTERESTPOINTS_REF).child(ipId).removeValue();
        }
    }

    public void copyInterestPoint(InterestPoint ip){
        DatabaseReference ref = DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_INTERESTPOINTS_REF).push();
        String id = ref.getKey();
        InterestPoint myIp = ip;
        myIp.setIpId(id);
        myIp.setUserId(mUser.getUid());
        ref.setValue(myIp);
    }

    public void createDestinationPoint(String name, String groupId, double lat, double lng, String hour){

        DatabaseReference ref = DBroot.child(DB_GROUPS_REFERENCE).child(groupId).child(DB_GROUPS_DESTINATIONS_REFERENCE).push();
        DestinationPoint destinationPoint = new DestinationPoint(lat,lng,name,ref.getKey(),hour);
        ref.setValue(destinationPoint);
    }

    public void removeDestinationPoint(String groupId, ArrayList<String> ids){
        for(String id:ids){
            DBroot.child(DB_GROUPS_REFERENCE).child(groupId).child(DB_GROUPS_DESTINATIONS_REFERENCE).child(id).removeValue();
        }
    }

    public void editDestinationPoint(String groupId, String pId, String name, double lat, double lng, String hour){
        DestinationPoint p = new DestinationPoint(lat,lng,name,pId,hour);
        DBroot.child(DB_GROUPS_REFERENCE).child(groupId).child(DB_GROUPS_DESTINATIONS_REFERENCE).child(pId).setValue(p);
    }

    /******************************** SITUM ACCOUNTS *********************************/

    public void addSitumAccount(String email, String pwd, String publicName){
        String userId = mUser.getUid();
        SitumAccount situmAccount = new SitumAccount(email,pwd,publicName,userId);
        DBroot.child(DB_SITUMACC_REFERENCE).child(Utils.encodeForFirebaseKey(email)).setValue(situmAccount);
    }

    public void initSitumAccountList(){
        final ArrayList<SitumAccount> result = new ArrayList<>();
        Query query = DBroot.child(DB_SITUMACC_REFERENCE).limitToLast(100);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot children : dataSnapshot.getChildren()){
                    SitumAccount acc = children.getValue(SitumAccount.class);
                    if(acc!=null)
                        result.add(acc);
                }
                if(mListener!=null){
                    mListener.initSitumAccountList(result);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void findSitumPwd(String email){
        DBroot.child(DB_SITUMACC_REFERENCE).child(Utils.encodeForFirebaseKey(email)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    mListener.enableIndoor(dataSnapshot.getValue(SitumAccount.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*********************************** COMMUNICATION INTERFACE ******************************************/

    public interface DBManagerInteractions{
        void signedIn(Location lastLocation);
        void signedOut();
        void groupChanged(Group group);
        void noUserFound(String contact);
        void locationReceived(String userId,String nick, String groupId, Location location);
        void messageReceived(String groupId, Message msg);
        void requestReceived(Request request);
        void requestRemoved();
        void noProfileAvailable();
        void initMsgList(String groupId, ArrayList<Message> messages);
        void updateFilter();
        void initInterestPoint(InterestPoint interestPoint,String userId, String ipId);
        void interestPointAdded(InterestPoint ip);
        void interestPointRemoved(InterestPoint ip);
        void destinationPointAdded(DestinationPoint p, String groupId);
        void destinationPointChanged(DestinationPoint p, String groupId);
        void destinationPointRemoved(DestinationPoint p, String groupId);
        void initSitumAccountList(ArrayList<SitumAccount> situmAccounts);
        void enableIndoor(SitumAccount account);
    }
}
