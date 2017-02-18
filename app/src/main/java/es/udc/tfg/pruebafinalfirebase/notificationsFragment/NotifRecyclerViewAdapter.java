package es.udc.tfg.pruebafinalfirebase.notificationsFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.Utils;


/**
 * Created by Usuario on 26/12/2016.
 */

public class NotifRecyclerViewAdapter extends RecyclerView.Adapter<NotifRecyclerViewAdapter.ViewHolder> {

    private String TAG = "NotifRecyclerView";
    private OnNotifAdapterInteractionListener mListener;
    private ArrayList<NotificationItem> mDataset;
    private Context context;

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

    public NotifRecyclerViewAdapter(ArrayList<NotificationItem> notifs){
        mDataset = notifs;
    }

    @Override
    public NotifRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (context instanceof OnNotifAdapterInteractionListener) {
            mListener = (OnNotifAdapterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNotifAdapterInteractionListener");
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_row, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(NotifRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.title.setText(mDataset.get(position).getGroupName());
        holder.members.setText(Utils.listToString(mDataset.get(position).getMembers()));
        holder.time.setText(Utils.longToDate(mDataset.get(position).getTime()));

        holder.action_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationItem item = mDataset.get(position);
                mDataset.remove(position);
                notifyItemRemoved(position);
                mListener.cancelRequest(item.getRequestId());
            }
        });
        holder.action_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationItem item = mDataset.get(position);
                Log.d(TAG,item.toString());
                mDataset.remove(position);
                notifyItemRemoved(position);
                mListener.acceptRequest(item.getRequestId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnNotifAdapterInteractionListener {
        // TODO: Update argument type and name
        void acceptRequest(String requestId);
        void cancelRequest(String requestId);
    }
}
