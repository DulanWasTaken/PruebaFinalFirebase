package es.udc.tfg.pruebafinalfirebase.Map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.udc.tfg.pruebafinalfirebase.Messages.Message;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.Utils.Utils;

/**
 * Created by Usuario on 04/07/2017.
 */

public class infoWindowRecyclerViewAdapter extends RecyclerView.Adapter<infoWindowRecyclerViewAdapter.ViewHolder> {

    private List<Message> mDataset;
    private onMapChatAdapterInteractionListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView msg,time;
        private ImageView attach;

        public ViewHolder(View view) {
            super(view);
            msg = (TextView) view.findViewById(R.id.textview_msg);
            time = (TextView) view.findViewById(R.id.textview_time);
            attach = (ImageView) view.findViewById(R.id.imageView_attach);
        }
    }

    public infoWindowRecyclerViewAdapter(List<Message> msgs){
        mDataset = msgs;
    }

    @Override
    public infoWindowRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        if (context instanceof onMapChatAdapterInteractionListener) {
            mListener = (onMapChatAdapterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onMapChatAdapterInteractionListener");
        }

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.map_chat_row, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(infoWindowRecyclerViewAdapter.ViewHolder holder, final int position) {
        Message message = mDataset.get(position);
        if(message.getType()==Message.TYPE_IP) {
            holder.attach.setVisibility(View.VISIBLE);
            holder.attach.setImageResource(R.drawable.ic_infowindow_chat_ip);
        }
        if(message.getType()==Message.TYPE_IMG) {
            holder.attach.setVisibility(View.VISIBLE);
            holder.attach.setImageResource(R.drawable.ic_image);
        }
        holder.msg.setText(message.getMsg());
        holder.time.setText(Utils.longToShortDate(message.getTime()));

        holder.attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onInterestPointClicked(mDataset.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface onMapChatAdapterInteractionListener{
        void onInterestPointClicked(Message msg);
    }
}