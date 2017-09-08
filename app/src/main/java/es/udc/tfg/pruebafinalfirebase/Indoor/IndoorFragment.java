package es.udc.tfg.pruebafinalfirebase.Indoor;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Core.DBManager;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IndoorFragment.OnIndoorFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class IndoorFragment extends Fragment {

    private static final String ARG_PARAM1 = "acc";

    private Context context;
    private FloatingActionButton situmButton;
    private RecyclerView accList;
    private OnIndoorFragmentInteractionListener mListener;
    private DBManager dbManager;
    private String acc;

    public IndoorFragment() {
        // Required empty public constructor
    }

    public static IndoorFragment newInstance(String param1) {
        IndoorFragment fragment = new IndoorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            acc = getArguments().getString(ARG_PARAM1);
        }
        dbManager = DBManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_indoor, container, false);
        situmButton = (FloatingActionButton) v.findViewById(R.id.situm_button);
        accList = (RecyclerView) v.findViewById(R.id.situm_acc_recyclerview);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnIndoorFragmentInteractionListener) {
            mListener = (OnIndoorFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnIndoorFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dbManager.initSitumAccountList();
        situmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSitumButtonClicked();
            }
        });
        accList.setLayoutManager(new LinearLayoutManager(context));
        accList.addItemDecoration(new SimpleDividerItemDecoration(context));
    }

    public void setList(ArrayList<SitumAccount> accounts){
        accList.setAdapter(new IndoorRecyclerViewAdapter(accounts,acc));
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
    public interface OnIndoorFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSitumButtonClicked();
    }
}
