package es.udc.tfg.pruebafinalfirebase;

import android.content.Context;
import android.support.annotation.NonNull;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import es.udc.tfg.pruebafinalfirebase.Group.Group;
import es.udc.tfg.pruebafinalfirebase.Group.GroupMember;
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
    private static final String DB_GROUPS_REFERENCE = "groups";
    private static final String DB_GROUPS_MEMBERS_REFERENCE = "membersId";
    private static final String DB_MESSAGES_REFERENCE = "messages";
    private static final String DB_MESSAGES_OLDER_REFERENCE = "oldermessages";
    private static final String DB_REQUESTS_REFERENCE = "requests";
    private static final String DB_PUBLICID_REFERENCE = "publicIds";
    private static final String TAG = "DBManager";

    private static DBManager ourInstance = new DBManager();

    private DatabaseReference DBroot = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mProfileReference;
    private FirebaseAuth DBauth = FirebaseAuth.getInstance();
    private DBManagerInteractions mListener;

    private ValueEventListener groupListener;
    private ValueEventListener userLocationListener;
    private ChildEventListener groupMembersListener;
    private ChildEventListener groupMsgListener;
    private ValueEventListener groupRequestListener;
    private FirebaseUser mUser;
    private User mProfile;

    public static ArrayList<Request> pendingRequests = new ArrayList<>();
    public static ArrayList<Group> mGroupsRequest = new ArrayList<>();
    public static LinkedHashMap<Group,Boolean> mGroups = new LinkedHashMap<>();
    public boolean authenticated = false;

    public static DBManager getInstance() {
        Log.d(TAG,"GET SINGLETON INSTANCE");
        return ourInstance;
    }

    /*********************************** AUTH METHODS ******************************************/

    private DBManager() {
        Log.d(TAG,"create");
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
                                mProfile = dataSnapshot.getValue(User.class);
                                Log.d(TAG,"ASLÑDKFJASLÑDFJ  "+mProfile.toString()+ "  "+mListener.toString());
                                if(mProfile!=null){
                                    mProfileReference = DBroot.child(DB_USER_REFERENCE).child(mUser.getUid());
                                    if(mListener!=null && !authenticated){
                                        Log.d(TAG, "entramos");
                                        authenticated=true;
                                        mListener.signedIn();
                                    }
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
                    if(mListener!=null){
                        Log.d(TAG,"LOG OUT,,, listener: "+mListener.toString());
                        mListener.signedOut();
                        authenticated=false;
                    }
                }
            }
        });
    }

    private void initListeners(String id){
        DBroot.child(DB_USER_REFERENCE).child(id).child(DB_USER_GROUPS_REFERENCE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG,"GROUP ADDED");
                final String groupId = dataSnapshot.getValue(String.class);
                if(mListener!=null)
                    //mListener.groupAdded(groupId);
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
                                if(member!=null){
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
                            if(dataSnapshot.exists()){
                                Message msg = dataSnapshot.getValue(Message.class);
                                if (msg!=null && mListener!=null){
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

                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).addValueEventListener(groupListener);
                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).child(DB_GROUPS_MEMBERS_REFERENCE).addChildEventListener(groupMembersListener);
                    DBroot.child(DB_MESSAGES_REFERENCE).child(groupId).child(DB_MESSAGES_OLDER_REFERENCE).addChildEventListener(groupMsgListener);
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
                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).removeEventListener(groupListener);
                    DBroot.child(DB_GROUPS_REFERENCE).child(groupId).child(DB_GROUPS_MEMBERS_REFERENCE).removeEventListener(groupMembersListener);
                    DBroot.child(DB_MESSAGES_REFERENCE).child(groupId).child(DB_MESSAGES_OLDER_REFERENCE).removeEventListener(groupMsgListener);
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
                            aux.remove(request.getIdGroup());
                            removeGroup(request.getIdGroup());
                            DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_GROUPS_REFERENCE).setValue(aux);
                            DBroot.child(DB_REQUESTS_REFERENCE).child(mProfile.getRequest()).child(request.getId()).removeValue();
                        }else {
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
                            DBroot.child(DB_GROUPS_REFERENCE).child(request.getIdGroup()).addListenerForSingleValueEvent(groupRequestListener);
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
                        DBroot.child(DB_USER_GROUPS_REFERENCE).child(request.getIdGroup()).removeEventListener(groupRequestListener);
                        mGroupsRequest.remove(findGroupByRequest(request.getIdGroup()));
                        removeRequest(request);
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
    }

    private void updateGroupList(Group group){
        ArrayList<Group> aux = new ArrayList<>(mGroups.keySet());
        for(Group g : aux){
            if (g.getId().equals(group.getId())){
                mGroups.put(group,mGroups.get(g));
                mGroups.remove(g);
                return;
            }
        }
        mGroups.put(group,true);
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

    private void removeRequest(Request request){
        for(Request r : pendingRequests){
            if(r.getId().equals(request.getId())) {
                pendingRequests.remove(r);
                return;
            }

        }
    }

    public void bindDBManager(Context context){

        if (context instanceof DBManagerInteractions) {
            mListener = (DBManagerInteractions) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DBManagerInteractions");
        }

        if(authenticated)
            mListener.signedIn();
        else
            mListener.signedOut();
    }

    public void signIn(GoogleSignInAccount googlekey){

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
        DBauth.signOut();
    }

    public boolean isAuthenticated(){
        if(mUser!=null){
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
        return mProfile.getNick();
    }

    /*********************************** PROFILE METHODS ******************************************/

    public void createProfile(String phoneNumber, String nick){
        String email = Utils.generateValidEmail(mUser.getEmail());
        String phone = Utils.generateValidPhoneNumber(phoneNumber);
        String requestPath = DBroot.child(DB_REQUESTS_REFERENCE).push().getKey();

        DBroot.child(DB_PUBLICID_REFERENCE).child(email).setValue(requestPath);
        DBroot.child(DB_PUBLICID_REFERENCE).child(phone).setValue(requestPath);
        User newProfile = new User(email,"",mUser.getUid(),phone,nick,new Location(),new ArrayList<InterestPoint>(),"",new ArrayList<String>(),requestPath);
        DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).setValue(newProfile);
    }

    public ArrayList<Request> getPendingRequests(){
        return pendingRequests;
    }

    public void setLocation(Location location){
        DBroot.child(DB_USER_REFERENCE).child(mUser.getUid()).child(DB_USER_LOCATION_REFERENCE).setValue(location);
    }

    public ArrayList<String> getGroupsId(){
        return mProfile.getGroupsId();
    }

    public void disableMyLocation(){
        if(mProfile!=null){
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
        DBroot.child(DB_GROUPS_REFERENCE).child(groupId).setValue(new Group(name,members,admins));

        ArrayList<String> mGroups = mProfile.getGroupsId();
        if(mGroups == null)
            mGroups = new ArrayList<>();
        mGroups.add(groupId);
        mProfileReference.child(DB_USER_GROUPS_REFERENCE).setValue(mGroups);

        for(String contact : contacts){
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
                    }
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
            if(group.getId().equals(id))
                return group;
        }
        return null;
    }

    public Boolean isFiltered(String groupId){
        Group group = findGroupById(groupId);
        if(group!= null)
            return mGroups.get(group);
        else
            return true;
    }

    public void acceptRequest(Request request){
        joinGroup(request.getIdGroup());
        DBroot.child(DB_REQUESTS_REFERENCE).child(mProfile.getRequest()).child(request.getId()).removeValue();
    }

    public void cancelRequest(Request request){
        DBroot.child(DB_REQUESTS_REFERENCE).child(mProfile.getRequest()).child(request.getId()).removeValue();
    }

    public void initMsgList(final String groupId){
        final ArrayList<Message> result = new ArrayList<>();
        Query query = DBroot.child(DB_MESSAGES_REFERENCE).child(groupId).child(DB_MESSAGES_OLDER_REFERENCE).limitToFirst(15);
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

    public void setFilter(Group group, Boolean filter){
        mGroups.put(group,filter);
    }

    public void updateGroup(Group group){
        DBroot.child(DB_GROUPS_REFERENCE).child(group.getId()).setValue(group);
    }

    public void exitGroup(final String groupId){
        removeMember(groupId,mUser.getUid());
    }

    public void addMembers(final String groupId, ArrayList<String> members){
        for(String contact : members){
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
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void removeMember(final String groupId, final String userId){
        DBroot.child(DB_USER_REFERENCE).child(userId).child(DB_USER_REQUEST_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String requestId = dataSnapshot.getValue(String.class);
                DatabaseReference reqRef = DBroot.child(DB_REQUESTS_REFERENCE).child(requestId).push();
                String reqId = reqRef.getKey();
                reqRef.setValue(new Request(groupId,Request.REQUEST_TYPE_DELETED,reqId));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Group group = findGroupById(groupId);
        group.removeMember(userId);
        DBroot.child(DB_GROUPS_REFERENCE).child(groupId).setValue(group);
    }

    /*********************************** COMMUNICATION INTERFACE ******************************************/

    public interface DBManagerInteractions{
        void signedIn();
        void signedOut();
        void groupChanged(Group group);
        void locationReceived(String userId,String nick, String groupId, Location location);
        void messageReceived(String groupId, Message msg);
        void requestReceived(Request request);
        void requestRemoved();
        void noProfileAvailable();
        void initMsgList(String groupId, ArrayList<Message> messages);

    }
}
