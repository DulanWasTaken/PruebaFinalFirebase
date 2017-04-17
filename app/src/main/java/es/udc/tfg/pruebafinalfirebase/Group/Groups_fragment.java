package es.udc.tfg.pruebafinalfirebase.Group;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import java.util.LinkedHashMap;

import es.udc.tfg.pruebafinalfirebase.DBManager;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


public class Groups_fragment extends Fragment {

    private Context context;
    private RecyclerView mRecyclerView;
    private GroupsRecyclerViewAdapter adapter;
    private FloatingActionButton addGroupButton;
    private OnGroupsFragmentInteractionListener mListener;


    public Groups_fragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
        if (context instanceof OnGroupsFragmentInteractionListener) {
            mListener = (OnGroupsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGroupsFragmentIteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_groups_fragment, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.groups_recyclerview);
        addGroupButton = (FloatingActionButton) v.findViewById(R.id.add_group_button);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        adapter = new GroupsRecyclerViewAdapter();
        mRecyclerView.setAdapter(adapter);

        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addGroup();
            }
        });

    }

    public void updateGroupList(){
        adapter.notifyDataSetChanged();
    }

    public interface OnGroupsFragmentInteractionListener {
        public void addGroup();
    }
}
