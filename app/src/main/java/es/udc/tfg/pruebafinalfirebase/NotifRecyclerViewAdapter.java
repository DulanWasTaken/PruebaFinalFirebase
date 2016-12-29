package es.udc.tfg.pruebafinalfirebase;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Usuario on 26/12/2016.
 */

public class NotifRecyclerViewAdapter extends RecyclerView.Adapter<NotifRecyclerViewAdapter.ViewHolder> {

    private ArrayList<NotificationItem> mDataset;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, members,time;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.notif_title);
            members = (TextView) view.findViewById(R.id.notif_type);
            time = (TextView) view.findViewById(R.id.notif_time);
        }
    }

    public NotifRecyclerViewAdapter(ArrayList<NotificationItem> notifs){
        mDataset = notifs;
    }

    @Override
    public NotifRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_row, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(NotifRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.title.setText(mDataset.get(position).getGroupName());
        holder.members.setText(mDataset.get(position).getMembers()+"");
        holder.time.setText(mDataset.get(position).getTime()+"");
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
