package es.udc.tfg.pruebafinalfirebase.Filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import es.udc.tfg.pruebafinalfirebase.Group.EditGroupFragment;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


public class Filter_fragment extends Fragment {

    private static final String ARG_PARAM1 = "pi_state";

    private OnFilterFragmentInteractionListener mListener;
    private Context context;
    private RecyclerView mRecyclerView;
    private ImageButton ipButton;
    private FilterRecyclerViewAdapter adapter;
    private Drawable but_checked,but_unchecked;
    private Boolean ipState;

    public Filter_fragment() {
        // Required empty public constructor
    }

    public static Filter_fragment newInstance(Boolean param1) {
        Filter_fragment fragment = new Filter_fragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        but_checked = getActivity().getResources().getDrawable(R.drawable.ip_button_checked);
        but_unchecked = getActivity().getResources().getDrawable(R.drawable.ip_button_unchecked);
        if (getArguments() != null) {
            ipState = getArguments().getBoolean(ARG_PARAM1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.filter_recycler_view);
        ipButton = (ImageButton) view.findViewById(R.id.ip_button);
        ipButton.setBackground(ipState?but_checked:but_unchecked);
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
        adapter = new FilterRecyclerViewAdapter();
        mRecyclerView.setAdapter(adapter);


        ipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ipButton.getBackground().equals(but_checked)){
                    ipButton.setBackground(but_unchecked);
                    mListener.ipStateChanged();
                }else{
                    ipButton.setBackground(but_checked);
                    mListener.ipStateChanged();
                }
            }
        });
    }

    public void updateFilter(){
        Log.d("FILTER FRAGMENT","UPDATE FILTER");
        adapter.notifyDataSetChanged();
    }

    public interface OnFilterFragmentInteractionListener {
        public void ipStateChanged();
    }
}
