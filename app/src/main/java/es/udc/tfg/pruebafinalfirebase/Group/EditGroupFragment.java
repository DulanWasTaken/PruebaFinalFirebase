package es.udc.tfg.pruebafinalfirebase.Group;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Core.DBManager;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.DestinationPoint;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


public class EditGroupFragment extends Fragment {

    private static final String ARG_PARAM1 = "groupId";

    private DBManager dbManager;

    private Context context;
    private String groupId;
    private Group group;
    private Button addMemberButton,exitGroupButton,saveChangesButton;
    private EditText editGroupName;
    private TextView destTv;
    private RecyclerView groupMembersRecyclerView,destinationsRecyclerView;
    private ImageView imageView;
    private CheckBox checkBox;

    private OnEditGroupFragmentInteractionListener mListener;

    public EditGroupFragment() {

    }

    public static EditGroupFragment newInstance(String param1) {
        EditGroupFragment fragment = new EditGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_PARAM1);
        }
        dbManager = DBManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_group, container, false);

        saveChangesButton = (Button) v.findViewById(R.id.edit_group_save);
        addMemberButton = (Button) v.findViewById(R.id.add_member_button);
        exitGroupButton = (Button) v.findViewById(R.id.exit_group_button);
        editGroupName = (EditText) v.findViewById(R.id.group_name_edit);
        groupMembersRecyclerView = (RecyclerView) v.findViewById(R.id.group_members_recycler_view);
        destinationsRecyclerView = (RecyclerView) v.findViewById(R.id.destinations_recycler_view);
        destTv = (TextView) v.findViewById(R.id.destinations_text_view);
        imageView = (ImageView) v.findViewById(R.id.image_group);
        checkBox = (CheckBox) v.findViewById(R.id.allow_invitations_checkbox);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnEditGroupFragmentInteractionListener) {
            mListener = (OnEditGroupFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditGroupFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("groups").child(groupId);
        ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });
        setUI();
    }

    public void setUI(){
        group = dbManager.findGroupById(groupId);
        editGroupName.setText(group.getName());
        if(group.getAdmins().contains(dbManager.getId())){
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(group.isAllowInvitations());
        }
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        groupMembersRecyclerView.setLayoutManager(mLayoutManager);
        groupMembersRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        groupMembersRecyclerView.setAdapter(new GroupMemberRecyclerViewAdapter(group,groupId));
        final RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(context);
        destinationsRecyclerView.setLayoutManager(mLayoutManager2);
        if (group.getDestinationPoints() == null) {
            destinationsRecyclerView.setVisibility(View.GONE);
            destTv.setVisibility(View.GONE);
        }else{
            ArrayList<DestinationPoint> destinations = new ArrayList<>(group.getDestinationPoints().values());
            destinationsRecyclerView.setAdapter(new DestinationPointsRecyclerViewAdapter(destinations,groupId));
        }



        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editGroupName.getText()!=null){
                    String name = editGroupName.getText().toString();
                    group.setName(name);
                    group.setAllowInvitations(checkBox.isChecked());
                    dbManager.updateGroup(group);
                    mListener.saveChanges();
                }

            }
        });

        if(!group.getAdmins().contains(dbManager.getId()) && !group.isAllowInvitations()){
            addMemberButton.setVisibility(View.GONE);
        } else if (group.getAdmins().size()==1) {
            exitGroupButton.setText("Delete Group");
        }

        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addGroupMember();
            }
        });

        exitGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exitGroupButton.getText().toString().equals("Delete Group")){
                    ArrayList<String> members = new ArrayList<String>();
                    for(GroupMember member : group.getMembersId()){
                        members.add(member.getMemberId());
                    }
                    dbManager.removeMember(groupId,members);
                    ArrayList<String> invitations = new ArrayList<String>();
                    if(group.getInvitations()!=null){
                        for(GroupMember member : group.getInvitations()){
                            invitations.add(member.getMemberId());
                        }
                        dbManager.cancelInvitation(groupId,invitations);
                    }
                    if(group.getDestinationPoints()!=null){
                        ArrayList<String> destinations = new ArrayList<String>(group.getDestinationPoints().keySet());
                        dbManager.removeDestinationPoint(groupId,destinations);
                    }
                }
                dbManager.exitGroup(group.getId());
                //getFragmentManager().popBackStack();
                mListener.deleteGroup();
            }
        });
    }

    public void membersAdded(ArrayList<String> membersAdded){
        dbManager.addMembers(groupId,membersAdded);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnEditGroupFragmentInteractionListener {
        public void addGroupMember();
        public void deleteGroup();
        public void saveChanges();
    }
}
