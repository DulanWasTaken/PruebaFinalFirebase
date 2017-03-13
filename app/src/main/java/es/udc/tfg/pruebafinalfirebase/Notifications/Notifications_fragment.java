package es.udc.tfg.pruebafinalfirebase.Notifications;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import es.udc.tfg.pruebafinalfirebase.DBManager;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


public class Notifications_fragment extends Fragment{

    private String TAG = "NOTIF_FRAGMENT";
    private Context context;

    private RecyclerView mRecyclerView;

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
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        mRecyclerView.setAdapter(new NotifRecyclerViewAdapter(DBManager.pendingRequests));
    }

    public void updateNotifs(){
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }
}
