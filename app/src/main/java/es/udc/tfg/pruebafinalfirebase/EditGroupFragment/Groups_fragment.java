package es.udc.tfg.pruebafinalfirebase.EditGroupFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Group;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


public class Groups_fragment extends Fragment {

    private Context context;
    private ArrayList<String> groupsId;
    private ArrayList<Group> mGroups = new ArrayList<>();
    private RecyclerView mRecyclerView;

    private OnGroupsFragmentInteractionListener mListener;

    public Groups_fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_groups_fragment, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.groups_recyclerview);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
        if (context instanceof OnGroupsFragmentInteractionListener) {
            mListener = (OnGroupsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGroupsFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));

        groupsId = mListener.getGroups();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("groups");
        for(int i = 0;i<groupsId.size();i++){
            ValueEventListener listener;
            if(i==groupsId.size()-1){
                 listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Group g = dataSnapshot.getValue(Group.class);
                        g.setId(dataSnapshot.getKey());
                        mGroups.add(g);
                        mRecyclerView.setAdapter(new GroupsRecyclerViewAdapter(mGroups));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
            }else{
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Group g = dataSnapshot.getValue(Group.class);
                        g.setId(dataSnapshot.getKey());
                        mGroups.add(g);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
            }
            ref.child(groupsId.get(i)).addListenerForSingleValueEvent(listener);
        }
        //mRecyclerView.setAdapter(new (GroupsRecyclerViewAdapter(mGroups)));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnGroupsFragmentInteractionListener {
        ArrayList<String> getGroups();
    }
}
