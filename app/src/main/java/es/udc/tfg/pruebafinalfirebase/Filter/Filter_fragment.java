package es.udc.tfg.pruebafinalfirebase.Filter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


public class Filter_fragment extends Fragment {

    private Context context;
    private RecyclerView mRecyclerView;
    private FilterRecyclerViewAdapter adapter;

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
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        adapter = new FilterRecyclerViewAdapter();
        mRecyclerView.setAdapter(adapter);


    }

    public void updateFilter(){
        adapter.notifyDataSetChanged();
    }

}
