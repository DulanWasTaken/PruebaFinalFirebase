package es.udc.tfg.pruebafinalfirebase.Indoor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.R;

/**
 * Created by Usuario on 09/01/2017.
 */

public class IndoorRecyclerViewAdapter extends RecyclerView.Adapter<IndoorRecyclerViewAdapter.ViewHolder> {

    private String TAG = "IndoorRecyclerView",accEmail;
    private OnIndoorAdapterInteractionListener mListener;
    private ArrayList<SitumAccount> mDataset;
    private Context context;
    private IndoorRecyclerViewAdapter.ViewHolder lastAcc;
    private int color_green;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,acc;
        public ImageView checked;
        public LinearLayout row;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.situm_name);
            acc = (TextView) view.findViewById(R.id.situm_acc);
            checked = (ImageView) view.findViewById(R.id.situm_checked);
            row = (LinearLayout) view.findViewById(R.id.situm_acc_row);
        }
    }

    public IndoorRecyclerViewAdapter(ArrayList<SitumAccount> accounts, String acc){
        mDataset = accounts;
        this.accEmail = acc;
    }

    @Override
    public IndoorRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (context instanceof OnIndoorAdapterInteractionListener) {
            mListener = (OnIndoorAdapterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnIndoorAdapterInteractionListener");
        }

        color_green = parent.getResources().getColor(R.color.validate_green);

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.situm_acc_row, parent, false);

        IndoorRecyclerViewAdapter.ViewHolder vh = new IndoorRecyclerViewAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final IndoorRecyclerViewAdapter.ViewHolder holder, final int position) {
        final SitumAccount acc = mDataset.get(position);
        if(acc.getEmail().equals(accEmail)) {
            lastAcc = holder;
            holder.row.setBackgroundColor(color_green);
            holder.checked.setVisibility(View.VISIBLE);
        }
        holder.name.setText(acc.getPublicName());
        holder.acc.setText(acc.getEmail());
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.accountSelected(mDataset.get(position));
                if(lastAcc!=null){
                    lastAcc.row.setBackgroundColor(Color.WHITE);
                    lastAcc.checked.setVisibility(View.INVISIBLE);
                }
                accEmail = acc.getEmail();
                lastAcc = holder;
                holder.row.setBackgroundColor(color_green);
                holder.checked.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnIndoorAdapterInteractionListener {
        void accountSelected(SitumAccount account);
    }
}