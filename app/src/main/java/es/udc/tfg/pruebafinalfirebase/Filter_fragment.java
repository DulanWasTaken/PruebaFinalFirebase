package es.udc.tfg.pruebafinalfirebase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Filter_fragment.OnFilterFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Filter_fragment extends Fragment {

    private OnFilterFragmentInteractionListener mListener;
    private Context context;
    private RecyclerView mRecyclerView;
    private ArrayList<FilterItem> recyclerViewItems = new ArrayList<>();

    public Filter_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.filter_recycler_view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
        if (context instanceof OnFilterFragmentInteractionListener) {
            mListener = (OnFilterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFilterFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));

        ArrayList<String> activeGroups = mListener.getActiveGroups();
        for(int i=0; i<activeGroups.size();i++){
            final String group = activeGroups.get(i);
            ValueEventListener listener;

            if (i==activeGroups.size()-1)
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Group group1 = dataSnapshot.getValue(Group.class);
                        recyclerViewItems.add(new FilterItem(group1.getName(),group));
                        mRecyclerView.setAdapter(new FilterRecyclerViewAdapter(recyclerViewItems,mListener.getFilteredGroups()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
            else
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Group group1 = dataSnapshot.getValue(Group.class);
                        recyclerViewItems.add(new FilterItem(group1.getName(),group));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

            FirebaseDatabase.getInstance().getReference().child("groups").child(group).addListenerForSingleValueEvent(listener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFilterFragmentInteractionListener {
        // TODO: Update argument type and name
        ArrayList<String> getActiveGroups();
        ArrayList<String> getFilteredGroups();
    }
}
