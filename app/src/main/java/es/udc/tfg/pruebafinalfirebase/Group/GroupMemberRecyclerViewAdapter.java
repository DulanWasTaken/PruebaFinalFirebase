package es.udc.tfg.pruebafinalfirebase.Group;

/**
 * Created by Usuario on 30/01/2017.
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
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.RoundedImageView;

/**
 * Created by Usuario on 19/12/2016.
 */

public class GroupMemberRecyclerViewAdapter extends RecyclerView.Adapter<GroupMemberRecyclerViewAdapter.ViewHolder> {
    private ArrayList<GroupMember> mDataset;
    private ArrayList<String> admins;
    private Context context;
    private String TAG = "GroupMemberRecyclerViewAdapter";
    private Drawable ic_contact;
    private String groupId;
    private DBManager dbManager;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name,admin;
        public RoundedImageView photo;
        public ImageButton deleteButton;

        public ViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.group_member_name);
            admin = (TextView)v.findViewById(R.id.admin_text_view);
            deleteButton = (ImageButton)v.findViewById(R.id.delete_group_member_button);
            photo = (RoundedImageView) v.findViewById(R.id.photo_img_group_member);
        }
    }

    public GroupMemberRecyclerViewAdapter(Group mGroup,String groupId) {
        mDataset = mGroup.getMembersId();
        admins = mGroup.getAdmins();
        this.groupId = groupId;
        dbManager = DBManager.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupMemberRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_member_row, parent, false);

        ic_contact = parent.getResources().getDrawable(R.mipmap.ic_contact_photo);

        GroupMemberRecyclerViewAdapter.ViewHolder vh = new GroupMemberRecyclerViewAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GroupMemberRecyclerViewAdapter.ViewHolder holder, final int position) {
        GroupMember member = mDataset.get(position);
        holder.name.setText(member.getNick());
        if(!admins.contains(dbManager.getId())) {
            holder.admin.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.removeMember(groupId,mDataset.get(position).getMemberId());
            }
        });
        holder.photo.setImageDrawable(ic_contact);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}