package es.udc.tfg.pruebafinalfirebase.Group;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import es.udc.tfg.pruebafinalfirebase.DBManager;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.Notifications.Request;
import es.udc.tfg.pruebafinalfirebase.User;
import es.udc.tfg.pruebafinalfirebase.multipickcontact.SimpleDividerItemDecoration;


public class EditGroupFragment extends Fragment {

    private static final String ARG_PARAM1 = "groupId";

    private DBManager dbManager;

    private Context context;
    private String groupId;
    private Group group;
    private ImageButton editNameButton;
    private Button addMemberButton,exitGroupButton;
    private EditText editGroupName;
    private RecyclerView groupMembersRecyclerView;

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

        setUI();
    }

    public void setUI(){
        group = dbManager.findGroupById(groupId);
        editGroupName.setText(group.getName());
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        groupMembersRecyclerView.setLayoutManager(mLayoutManager);
        groupMembersRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        groupMembersRecyclerView.setAdapter(new GroupMemberRecyclerViewAdapter(group.getMembersId(),groupId));

        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editGroupName.getText()!=null){
                    String name = editGroupName.getText().toString();
                    group.setName(name);
                    dbManager.updateGroup(group);
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
                dbManager.exitGroup(group.getId());
                getFragmentManager().popBackStack();
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
    }
}
