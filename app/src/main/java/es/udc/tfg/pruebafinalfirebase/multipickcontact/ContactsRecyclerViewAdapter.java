package es.udc.tfg.pruebafinalfirebase.multipickcontact;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
 * Created by Usuario on 19/12/2016.
 */

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<ContactItem> mDataset;
    private String TAG = "ContRecyclerViewAdapter";
    private Drawable ic_contact;
    private int color_green;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView data;
        public ImageView checked;
        public LinearLayout row;
        public RoundedImageView photo;

        public ViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.contact_name);
            data = (TextView)v.findViewById(R.id.contact_data);
            checked = (ImageView)v.findViewById(R.id.checked_img);
            row = (LinearLayout) v.findViewById(R.id.row);
            photo = (RoundedImageView) v.findViewById(R.id.photo_img);
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
        ic_contact = parent.getResources().getDrawable(R.mipmap.ic_contact_photo);
        color_green = parent.getResources().getColor(R.color.validate_green);

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
        if(contact.getImg_uri()!=null)
            holder.photo.setImageURI(Uri.parse(contact.getImg_uri()));
        else
            holder.photo.setImageDrawable(ic_contact);
        holder.row.setBackgroundColor(mDataset.get(position).isChecked()? color_green:Color.WHITE);
        holder.checked.setVisibility(mDataset.get(position).isChecked()? View.VISIBLE:View.INVISIBLE);
        final ViewHolder finalHolder=holder;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataset.get(position).setChecked(!mDataset.get(position).isChecked());
                finalHolder.row.setBackgroundColor(mDataset.get(position).isChecked()? color_green:Color.WHITE);
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