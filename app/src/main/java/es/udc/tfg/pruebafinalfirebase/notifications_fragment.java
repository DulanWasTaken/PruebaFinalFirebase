package es.udc.tfg.pruebafinalfirebase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


public class Notifications_fragment extends Fragment{

    private String TAG = "NOTIF_FRAGMENT";
    private Context context;

    private RecyclerView mRecyclerView;
    private OnNotifFragmentInteractionListener mListener;
    private ArrayList<NotificationItem> notifs = new ArrayList<>();

    public Notifications_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_notifications_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.notifications_recyclerview);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        Log.d(TAG,"onAttach");
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnNotifFragmentInteractionListener) {
            mListener = (OnNotifFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));

        ArrayList<Request> requests = mListener.getRequests();
        for(int i = 0; i< requests.size();i++){
            final Request request = requests.get(i);
            ValueEventListener listener;
            Log.d(TAG,"request number "+i);
            if (i == requests.size()-1)
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Group group = dataSnapshot.getValue(Group.class);
                        ArrayList<String> members = new ArrayList<>();
                        for (GroupMember member: group.getMembersId()){
                            members.add(member.getNick());
                        }
                        notifs.add(new NotificationItem(group.getName(),members,request.getTime(),request.getId()));
                        mRecyclerView.setAdapter(new NotifRecyclerViewAdapter(notifs));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
            else
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Group group = dataSnapshot.getValue(Group.class);
                        ArrayList<String> members = new ArrayList<>();
                        for (GroupMember member: group.getMembersId()){
                            members.add(member.getNick());
                        }
                        notifs.add(new NotificationItem(group.getName(),members,request.getTime(),request.getId()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
            FirebaseDatabase.getInstance().getReference().child("groups").child(request.getIdGroup()).addListenerForSingleValueEvent(listener);

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNotifFragmentInteractionListener {
        // TODO: Update argument type and name
        ArrayList<Request> getRequests();
    }
}
