package es.udc.tfg.pruebafinalfirebase.EditGroupFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Group;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.Request;
import es.udc.tfg.pruebafinalfirebase.User;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditGroupFragment.OnEditGroupFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditGroupFragment extends Fragment {

    private static final String ARG_PARAM1 = "groupId";
    private static final String ARG_PARAM2 = "myId";

    private Context context;
    private String groupId,myId;
    private Group group;
    private ImageButton editNameButton;
    private Button addMemberButton,exitGroupButton;
    private EditText editGroupName;
    private RecyclerView groupMembersRecyclerView;

    private OnEditGroupFragmentInteractionListener mListener;
    private DatabaseReference groupRef,mProfileRef,publicIdRef,requestsRef;

    public EditGroupFragment() {

    }

    public static EditGroupFragment newInstance(String param1, String param2) {
        EditGroupFragment fragment = new EditGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2,param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_PARAM1);
            myId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_group, container, false);

        editNameButton = (ImageButton) v.findViewById(R.id.group_name_save);
        addMemberButton = (Button) v.findViewById(R.id.add_member_button);
        exitGroupButton = (Button) v.findViewById(R.id.exit_group_button);
        editGroupName = (EditText) v.findViewById(R.id.group_name_edit);
        groupMembersRecyclerView = (RecyclerView) v.findViewById(R.id.group_members_recycler_view);

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

        publicIdRef = FirebaseDatabase.getInstance().getReference().child("publicIds");
        requestsRef = FirebaseDatabase.getInstance().getReference().child("requests");
        groupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId);
        mProfileRef = FirebaseDatabase.getInstance().getReference().child("users").child(myId);
        if(groupRef!=null)
            groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                group = dataSnapshot.getValue(Group.class);
                group.setId(dataSnapshot.getKey());
                if (group!=null){
                    setUI();
                }else{
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        else
            return;
    }

    private void setUI(){
        editGroupName.setText(group.getName());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        groupMembersRecyclerView.setLayoutManager(mLayoutManager);
        groupMembersRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        groupMembersRecyclerView.setAdapter(new GroupMemberRecyclerViewAdapter(group.getMembersId()));

        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editGroupName.getText()!=null){
                    String name = editGroupName.getText().toString();
                    groupRef.child("name").setValue(name);
                    Toast.makeText(context,"Group name updated",Toast.LENGTH_SHORT).show();
                }

            }
        });

        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addGroupMember();
            }
        });

        exitGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Group group = mutableData.getValue(Group.class);
                        if(group==null)
                            return Transaction.success(mutableData);
                        group.removeMember(myId);
                        mutableData.setValue(group);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
                mProfileRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        User user = mutableData.getValue(User.class);
                        if (user == null)
                            return Transaction.success(mutableData);
                        user.removeGroup(groupId);
                        mutableData.setValue(user);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
                //return;
            }
        });
    }

    public void membersAdded(ArrayList<String> membersAdded){

        for (String contact: membersAdded){
            ValueEventListener listener;
            listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference requestRef = requestsRef.child(dataSnapshot.getValue(String.class)).push();
                    String id = requestRef.getKey();
                    requestRef.setValue(new Request(groupId,Request.REQUEST_TYPE_GROUP,id));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            publicIdRef.child(contact).addListenerForSingleValueEvent(listener);
        }
    }

    public void removeMember(final String id){
        groupRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Group group = mutableData.getValue(Group.class);
                if(group==null)
                    return Transaction.success(mutableData);
                group.removeMember(id);
                mutableData.setValue(group);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        mProfileRef.getParent().child(id).child("request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String requestId = dataSnapshot.getValue(String.class);
                DatabaseReference auxref = requestsRef.child(requestId).push();
                String auxid = auxref.getKey();
                auxref.setValue(new Request(groupId,Request.REQUEST_TYPE_DELETED,auxid));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //SEND DELETE REQUEST
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnEditGroupFragmentInteractionListener {
        public void addGroupMember();
    }
}
