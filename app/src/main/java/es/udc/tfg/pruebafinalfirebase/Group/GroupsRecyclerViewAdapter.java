package es.udc.tfg.pruebafinalfirebase.Group;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import es.udc.tfg.pruebafinalfirebase.DBManager;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.Utils;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.RoundedImageView;

/**
 * Created by Usuario on 09/01/2017.
 */

public class GroupsRecyclerViewAdapter extends RecyclerView.Adapter<GroupsRecyclerViewAdapter.ViewHolder> {

    private String TAG = "GroupsRecyclerView";
    private OnGroupsAdapterInteractionListener mListener;
    private ArrayList<Group> mDataset;
    private Context context;
    private Drawable ic_group;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, data,time;
        public RoundedImageView img;
        public RelativeLayout row;
        public ImageButton editButton;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.group_name);
            data = (TextView) view.findViewById(R.id.group_data);
            time = (TextView) view.findViewById(R.id.group_time);
            img = (RoundedImageView) view.findViewById(R.id.group_img);
            row = (RelativeLayout) view.findViewById(R.id.group_row);
            editButton = (ImageButton) view.findViewById(R.id.edit_button);
        }
    }

    public GroupsRecyclerViewAdapter(){
        mDataset = new ArrayList<>(DBManager.mGroups.keySet());
    }

    @Override
    public GroupsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (context instanceof OnGroupsAdapterInteractionListener) {
            mListener = (OnGroupsAdapterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGroupsAdapterInteractionListener");
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_row, parent, false);

        ic_group = parent.getResources().getDrawable(R.mipmap.ic_contact_photo);

        GroupsRecyclerViewAdapter.ViewHolder vh = new GroupsRecyclerViewAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(GroupsRecyclerViewAdapter.ViewHolder holder, final int position) {
        ArrayList<String> members = new ArrayList<>();
        ArrayList<GroupMember> aux = mDataset.get(position).getMembersId();
        if(aux!=null){
            Iterator<GroupMember> iter = mDataset.get(position).getMembersId().iterator();
            while(iter.hasNext()){
                GroupMember member = iter.next();
                members.add(member.getNick());
            }
        }
        /*for(GroupMember g: mDataset.get(position).getMembersId()){
            members.add(g.getNick());
        }*/
        holder.name.setText(mDataset.get(position).getName());
        holder.data.setText(Utils.listToString(members));
        holder.time.setText(Utils.longToDate(mDataset.get(position).getTime()));
        holder.img.setImageDrawable(ic_group);
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.groupSelected(mDataset.get(position).getId());
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.groupLongClick(mDataset.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnGroupsAdapterInteractionListener {
        void groupSelected(String groupId);
        void groupLongClick(String groupId);
    }
}
