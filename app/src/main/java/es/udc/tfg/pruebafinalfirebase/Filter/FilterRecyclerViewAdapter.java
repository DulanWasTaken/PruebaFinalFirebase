package es.udc.tfg.pruebafinalfirebase.Filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Core.DBManager;
import es.udc.tfg.pruebafinalfirebase.Group.Group;
import es.udc.tfg.pruebafinalfirebase.R;

/**
 * Created by Usuario on 03/01/2017.
 */

public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder> {

    private String TAG = "FilterRecyclerView";
    private ArrayList<Group> mDatasetGroups;
    private ArrayList<Boolean> mDatasetFilter;
    private DBManager dbManager;
    private Context context;
    private Drawable but_checked,but_unchecked;
    private OnFilterFragmentAdapterInteractionListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button button;
        public ImageButton imageButton;

        public ViewHolder(View view) {
            super(view);
            button = (Button) view.findViewById(R.id.filter_checkbox);
            imageButton = (ImageButton) view.findViewById(R.id.filter_checkbox_add);
        }
    }

    public FilterRecyclerViewAdapter(){
        dbManager = DBManager.getInstance();
    }

    @Override
    public FilterRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (context instanceof OnFilterFragmentAdapterInteractionListener) {
            mListener = (OnFilterFragmentAdapterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFilterFragmentAdapterInteractionListener");
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

        if(position == DBManager.mGroups.size()){
            holder.button.setVisibility(View.GONE);
            holder.imageButton.setVisibility(View.VISIBLE);

            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.addGroup();
                }
            });
        } else {
            mDatasetGroups = new ArrayList<Group>(DBManager.mGroups.keySet());
            mDatasetFilter = new ArrayList<Boolean>(DBManager.mGroups.values());


            final Group group = mDatasetGroups.get(position);
            final String groupName = group.getName();
            final Boolean filtered = mDatasetFilter.get(position);

            final Button b = holder.button;

            holder.button.setText(groupName);
            if(filtered)
                b.setBackground(but_checked);
            else
                b.setBackground(but_unchecked);

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Boolean> mDatasetFilter2 = new ArrayList<Boolean>(DBManager.mGroups.values());
                    Boolean filtered2 = mDatasetFilter2.get(position);
                    dbManager.setFilter(group,!filtered2);
                /*if(filtered2)
                    b.setBackground(but_unchecked);
                else
                    b.setBackground(but_checked);*/
                    //mListener.updateFilter();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return DBManager.mGroups.size()+1;
    }

    public interface OnFilterFragmentAdapterInteractionListener{
        void addGroup();
    }
}
