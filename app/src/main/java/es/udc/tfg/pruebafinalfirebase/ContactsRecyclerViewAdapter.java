package es.udc.tfg.pruebafinalfirebase;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Usuario on 19/12/2016.
 */

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<ContactItem> mDataset;
    private String TAG = "ContRecyclerViewAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView data;
        public ImageView checked;
        public CardView cv;

        public ViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.contact_name);
            data = (TextView)v.findViewById(R.id.contact_data);
            checked = (ImageView)v.findViewById(R.id.checked_img);
            cv = (CardView)v.findViewById(R.id.cv);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ContactsRecyclerViewAdapter(ArrayList<ContactItem> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ContactItem contact = mDataset.get(position);
        holder.name.setText(contact.getName());
        holder.data.setText(contact.getData());
        holder.cv.setCardBackgroundColor(mDataset.get(position).isChecked()? Color.parseColor("#A9F5A9"):Color.WHITE);
        holder.cv.setCardElevation(mDataset.get(position).isChecked()? 15:6);
        holder.checked.setVisibility(mDataset.get(position).isChecked()? View.VISIBLE:View.INVISIBLE);
        final ViewHolder finalHolder=holder;
        Log.d(TAG,"position: "+position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"position: "+position);
                mDataset.get(position).setChecked(!mDataset.get(position).isChecked());
                finalHolder.cv.setCardBackgroundColor(mDataset.get(position).isChecked()? Color.parseColor("#A9F5A9"):Color.WHITE);
                finalHolder.cv.setCardElevation(mDataset.get(position).isChecked()? 15:6);
                finalHolder.checked.setVisibility(mDataset.get(position).isChecked()? View.VISIBLE:View.INVISIBLE);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<ContactItem> getmDataset(){
        return mDataset;
    }

    public void addItemToDataset(ContactItem item, int position){
        mDataset.add(position,item);
    }
}