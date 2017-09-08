package es.udc.tfg.pruebafinalfirebase.LevelPicker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import es.situm.sdk.model.cartography.Floor;
import es.udc.tfg.pruebafinalfirebase.R;

public class LevelPickerFragment extends Fragment {

    private Context context;
    private RecyclerView mRecyclerView;
    private levelPickerRecyclerViewAdapter adapter;
    private String locationFloor = "";


    public LevelPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_level_picker, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.level_recycler_view);

        return v;
    }


    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }


    @Override
    public void onResume() {
        super.onResume();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void initFloorList(ArrayList<Floor> floors, String currentFloor){
        adapter = new levelPickerRecyclerViewAdapter(floors,currentFloor,locationFloor);
        mRecyclerView.setAdapter(adapter);
    }

    public void setLevelLocation(String id){
        /*adapter = new levelPickerRecyclerViewAdapter(adapter.getmDataset(),adapter.getCurrentFloor(),id);
        mRecyclerView.setAdapter(adapter);*/
        Log.d("TEST LEVEL PICKER 2", "ID RECIBIDO: "+id+" ID ACTUAL "+locationFloor);
        if(!id.equals(locationFloor)){
            locationFloor = id;
            adapter = new levelPickerRecyclerViewAdapter(adapter.getmDataset(),adapter.getCurrentFloor(),locationFloor);
            mRecyclerView.setAdapter(adapter);
            Log.d("TEST LEVEL PICKER", "cambio de piso: "+id);
        }
    }
}
