package es.udc.tfg.pruebafinalfirebase.LevelPicker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import es.situm.sdk.model.cartography.Floor;
import es.udc.tfg.pruebafinalfirebase.R;

/**
 * Created by Usuario on 18/08/2017.
 */

public class levelPickerRecyclerViewAdapter extends RecyclerView.Adapter<levelPickerRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Floor> mDataset;
    private String currentFloor;
    private onLevelPickerAdapterInteractionListener mListener;
    private Context context;
    private Drawable but_checked,but_unchecked;
    private ViewHolder lastHolder;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button level;

        public ViewHolder(View view) {
            super(view);
            level = (Button) view.findViewById(R.id.level_picker_button);
        }
    }

    public levelPickerRecyclerViewAdapter(ArrayList<Floor> floors, String currentFloor) {
        mDataset = floors;
        this.currentFloor = currentFloor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        if (context instanceof onLevelPickerAdapterInteractionListener) {
            mListener = (onLevelPickerAdapterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onLevelPickerAdapterInteractionListener");
        }

        but_checked = parent.getResources().getDrawable(R.drawable.level_picker_checked);
        but_unchecked = parent.getResources().getDrawable(R.drawable.level_picker_unchecked);

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.level_picker_row, parent, false);

        levelPickerRecyclerViewAdapter.ViewHolder vh = new levelPickerRecyclerViewAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Floor floor = mDataset.get(mDataset.size()-1-position);
        final int level = floor.getLevel();

        holder.level.setText(level+"");
        holder.level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.levelPicked(floor);
                lastHolder.level.setBackground(but_unchecked);
                lastHolder = holder;
                currentFloor = floor.getIdentifier();
                v.setBackground(but_checked);

            }
        });

        if(floor.getIdentifier().equals(currentFloor)) {
            holder.level.performClick();
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface onLevelPickerAdapterInteractionListener{
        void levelPicked(Floor floor);
    }
}
