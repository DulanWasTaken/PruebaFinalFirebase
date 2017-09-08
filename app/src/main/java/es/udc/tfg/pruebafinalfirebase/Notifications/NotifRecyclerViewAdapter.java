package es.udc.tfg.pruebafinalfirebase.Notifications;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Core.DBManager;
import es.udc.tfg.pruebafinalfirebase.Group.Group;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.Utils.Utils;


/**
 * Created by Usuario on 26/12/2016.
 */

public class NotifRecyclerViewAdapter extends RecyclerView.Adapter<NotifRecyclerViewAdapter.ViewHolder> {

    private String TAG = "NotifRecyclerView";
    private ArrayList<Request> mDataset;
    private Context context;
    private DBManager dbManager;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, members,time;
        public ImageButton action_accept,action_cancel;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.notif_title);
            members = (TextView) view.findViewById(R.id.notif_type);
            time = (TextView) view.findViewById(R.id.notif_time);
            action_accept = (ImageButton) view.findViewById(R.id.accept_notif_button);
            action_cancel = (ImageButton) view.findViewById(R.id.cancel_notif_button);
        }
    }

    public NotifRecyclerViewAdapter(ArrayList<Request> notifs){
        mDataset = notifs;
        dbManager = DBManager.getInstance();
    }

    @Override
    public NotifRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_row, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(NotifRecyclerViewAdapter.ViewHolder holder, final int position) {
        final Group group = dbManager.findGroupByRequest(mDataset.get(position).getIdGroup());
        if (group!=null) {
            holder.title.setText(group.getName());
            holder.members.setText(Utils.listToString(group.getMembersNick()));
            holder.time.setText(Utils.longToDate(mDataset.get(position).getTime()));

            holder.action_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbManager.cancelRequest(mDataset.get(position));
                }
            });
            holder.action_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbManager.acceptRequest(mDataset.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
