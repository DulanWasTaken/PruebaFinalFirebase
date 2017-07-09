package es.udc.tfg.pruebafinalfirebase.Messages;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.DBManager;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.Utils;

/**
 * Created by Usuario on 18/02/2017.
 */
public class msgRecyclerViewAdapter extends RecyclerView.Adapter<msgRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Message> mDataset;
    private DBManager dbManager;
    private String TAG = "msgRecyclerViewAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name,msg,date;

        public ViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.sender_name_tv);
            msg = (TextView)v.findViewById(R.id.msg_tv);
            date = (TextView)v.findViewById(R.id.msg_date_tv);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public msgRecyclerViewAdapter(ArrayList<Message> messages) {
        mDataset = messages;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public msgRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_row, parent, false);

        dbManager = DBManager.getInstance();

        msgRecyclerViewAdapter.ViewHolder vh = new msgRecyclerViewAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(msgRecyclerViewAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Message msg = mDataset.get(position);
        holder.name.setText(msg.getSender().getNick());
        if(msg.getSender().getMemberId().equals(dbManager.getId()))
            holder.name.setText("Me");
        holder.msg.setText(msg.getMsg());
        holder.date.setText(Utils.longToDate(msg.getTime()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<Message> getmDataset(){
        return mDataset;
    }

    public void addItemToDataset(Message item){
        mDataset.add(item);
        this.notifyDataSetChanged();
    }

}