package es.udc.tfg.pruebafinalfirebase;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Usuario on 03/01/2017.
 */

public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder> {

    private String TAG = "FilterRecyclerView";
    private OnFilterAdapterInteractionListener mListener;
    private ArrayList<FilterItem> mDataset;
    private ArrayList<String> filteredGroups;
    private Context context;
    private Drawable but_checked,but_unchecked;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button button;

        public ViewHolder(View view) {
            super(view);
            button = (Button) view.findViewById(R.id.filter_checkbox);
        }
    }

    public FilterRecyclerViewAdapter(ArrayList<FilterItem> activeGroups,ArrayList<String> filteredGroups){
        mDataset = activeGroups;
        this.filteredGroups = filteredGroups;
    }

    @Override
    public FilterRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (context instanceof OnFilterAdapterInteractionListener) {
            mListener = (OnFilterAdapterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNotifAdapterInteractionListener");
        }

        but_checked = parent.getResources().getDrawable(R.drawable.filter_button_checked);
        but_unchecked = parent.getResources().getDrawable(R.drawable.filter_button_unchecked);

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_row, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(FilterRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.button.setText(mDataset.get(position).getGroupName());
        if(filteredGroups.contains(mDataset.get(position).getGroupId()))
            holder.button.setBackground(but_checked);
        else
            holder.button.setBackground(but_unchecked);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!filteredGroups.contains(mDataset.get(position).getGroupId())) {
                    v.setBackground(but_checked);
                    mListener.addFilteredGroup(mDataset.get(position).getGroupId());
                    filteredGroups.add(mDataset.get(position).getGroupId());
                }else{
                    v.setBackground(but_unchecked);
                    mListener.removeFilteredGroup(mDataset.get(position).getGroupId());
                    filteredGroups.remove(mDataset.get(position).getGroupId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnFilterAdapterInteractionListener {
        // TODO: Update argument type and name
        void addFilteredGroup(String groupId);
        void removeFilteredGroup(String groupId);
    }
}
