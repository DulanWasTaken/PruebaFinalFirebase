package es.udc.tfg.pruebafinalfirebase.Messages;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Core.DBManager;
import es.udc.tfg.pruebafinalfirebase.R;


public class MessagesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private Context context;
    private ArrayList<Message> messages;
    private String groupId;

    private RecyclerView msgRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private msgRecyclerViewAdapter adapter;
    private EditText msgEditText;
    private ImageButton msgButton;
    private DBManager dbManager;


    public MessagesFragment() {
        // Required empty public constructor
    }


    public static MessagesFragment newInstance(String param1) {
        MessagesFragment fragment = new MessagesFragment();
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
        View v = inflater.inflate(R.layout.fragment_messages, container, false);
        msgButton = (ImageButton) v.findViewById(R.id.send_message_button);
        msgEditText = (EditText)v.findViewById(R.id.send_message_edittext);
        msgRecyclerView= (RecyclerView) v.findViewById(R.id.messages_recycler_view);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();

        mLayoutManager = new LinearLayoutManager(context);
        msgRecyclerView.setLayoutManager(mLayoutManager);

        dbManager.initMsgList(groupId,0);

        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = msgEditText.getText().toString();
                if(!msg.equals("")){
                    dbManager.sendMsg(msg,groupId,Message.TYPE_TEXT,null);
                }
                msgEditText.setText("");
            }
        });
    }

    public void initList(ArrayList<Message> messages){
        adapter = new msgRecyclerViewAdapter(messages,groupId);
        msgRecyclerView.setAdapter(adapter);
        mLayoutManager.scrollToPosition(adapter.getItemCount()-1);
    }

    public void onMsgReceived(Message msg){
        //adapter.addItemToDataset(msg);
        ArrayList<Message> msgs = adapter.getmDataset();
        msgs.add(msg);
        adapter = new msgRecyclerViewAdapter(msgs,groupId);
        msgRecyclerView.setAdapter(adapter);
        mLayoutManager.scrollToPosition(adapter.getItemCount()-1);
    }
}
