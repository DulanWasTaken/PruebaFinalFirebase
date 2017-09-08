package es.udc.tfg.pruebafinalfirebase.LevelPicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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

    public ArrayList<Floor> mDataset;
    public String currentFloor;
    private String floorLocation;
    private onLevelPickerAdapterInteractionListener mListener;
    private Context context;
    private Drawable but_checked,but_unchecked,but_checked_location,but_unchecked_location;
    private int checkedColor,uncheckedColor;
    private ViewHolder lastHolder;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button level;

        public ViewHolder(View view) {
            super(view);
            level = (Button) view.findViewById(R.id.level_picker_button);
        }
    }

    public levelPickerRecyclerViewAdapter(ArrayList<Floor> floors, String currentFloor, String floorLocation) {
        mDataset = floors;
        this.currentFloor = currentFloor;
        this.floorLocation = floorLocation;
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
        uncheckedColor = parent.getResources().getColor(R.color.background_gray);
        checkedColor = parent.getResources().getColor(R.color.divider_gray);

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.level_picker_row, parent, false);

        levelPickerRecyclerViewAdapter.ViewHolder vh = new levelPickerRecyclerViewAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Floor floor = mDataset.get(mDataset.size()-1-position);
        final String id = floor.getIdentifier();
        final int level = floor.getLevel();

        holder.level.setText(level+"");
        holder.level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.levelPicked(floor);
                if(lastHolder!=null){
                    //lastHolder.level.setBackground(but_unchecked);
                    GradientDrawable gradientDrawable = (GradientDrawable) lastHolder.level.getBackground();
                    gradientDrawable.setColor(uncheckedColor);
                    lastHolder.level.setBackground(gradientDrawable);
                }
                lastHolder = holder;
                currentFloor = id;
                //v.setBackground(but_checked);
                GradientDrawable gradientDrawable = (GradientDrawable) holder.level.getBackground();
                gradientDrawable.setColor(checkedColor);
                holder.level.setBackground(gradientDrawable);

            }
        });

        if(floor.getIdentifier().equals(currentFloor)) {
            lastHolder = holder;
            holder.level.setBackground(but_checked);
            mListener.levelPicked(floor);
        }else{
            holder.level.setBackground(but_unchecked);
        }

        if(floor.getIdentifier().equals(floorLocation)){
            GradientDrawable gradientDrawable = (GradientDrawable) holder.level.getBackground();
            gradientDrawable.setStroke(5, Color.BLUE);
            holder.level.setBackground(gradientDrawable);
        } else {
            GradientDrawable gradientDrawable = (GradientDrawable) holder.level.getBackground();
            gradientDrawable.setStroke(0, Color.TRANSPARENT);
            holder.level.setBackground(gradientDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setLevelLocation(String id){
        //floorLocation = id;
        this.notifyDataSetChanged();
    }

    public ArrayList<Floor> getmDataset(){
        return mDataset;
    }

    public String getCurrentFloor (){
        return currentFloor;
    }

    public interface onLevelPickerAdapterInteractionListener{
        void levelPicked(Floor floor);
    }
}
