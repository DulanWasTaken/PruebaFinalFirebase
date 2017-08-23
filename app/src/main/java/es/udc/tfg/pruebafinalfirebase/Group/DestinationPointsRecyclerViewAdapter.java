package es.udc.tfg.pruebafinalfirebase.Group;

/**
 * Created by Usuario on 06/07/2017.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.DBManager;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.DestinationPoint;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.Point;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.RoundedImageView;

/**
 * Created by Usuario on 19/12/2016.
 */

public class DestinationPointsRecyclerViewAdapter extends RecyclerView.Adapter<DestinationPointsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<DestinationPoint> mDataset;
    private String TAG = "DestinationPointsRecyclerViewAdapter";
    private DBManager dbManager;
    private String groupId;
    private Drawable ic_destination;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name,admin;
        public ImageButton deleteButton;
        public RoundedImageView photo;

        public ViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.group_member_name);
            admin = (TextView)v.findViewById(R.id.admin_text_view);
            deleteButton = (ImageButton)v.findViewById(R.id.delete_group_member_button);
            photo = (RoundedImageView) v.findViewById(R.id.photo_img_group_member);
        }
    }

    public DestinationPointsRecyclerViewAdapter(ArrayList<DestinationPoint> mDataset, String groupId) {
        this.mDataset = mDataset;
        this.groupId = groupId;
        dbManager = DBManager.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DestinationPointsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_member_row, parent, false);

        ic_destination = parent.getResources().getDrawable(R.drawable.ic_destination);

        DestinationPointsRecyclerViewAdapter.ViewHolder vh = new DestinationPointsRecyclerViewAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DestinationPointsRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.admin.setVisibility(View.INVISIBLE);
        DestinationPoint point = mDataset.get(position);

        holder.name.setText(point.getName());
        holder.deleteButton.setVisibility(View.VISIBLE);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> aux = new ArrayList<>();
                aux.add(mDataset.get(position).getIpId());
                dbManager.removeDestinationPoint(groupId, aux);

            }
        });

        holder.photo.setImageDrawable(ic_destination);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}