package es.udc.tfg.pruebafinalfirebase.Messages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.util.Map;

import es.udc.tfg.pruebafinalfirebase.Core.DBManager;
import es.udc.tfg.pruebafinalfirebase.Group.Group;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.InterestPoint;
import es.udc.tfg.pruebafinalfirebase.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnQuickMsgFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class QuickMsgFragment extends Fragment {

    private String TAG = "QuickMsgTag";
    private OnQuickMsgFragmentInteractionListener mListener;
    private EditText msgEditText;
    private ImageButton msgSendMsg,attach;
    private DBManager dbManager = DBManager.getInstance();
    private InterestPoint sendingIp;
    private Uri sendingImg;

    public QuickMsgFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quick_msg, container, false);

        msgEditText = (EditText) v.findViewById(R.id.quickMsgEditText);
        msgSendMsg = (ImageButton) v.findViewById(R.id.quickMsgButton);
        attach =(ImageButton) v.findViewById(R.id.quickMsg_attachButton);

        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnQuickMsgFragmentInteractionListener) {
            mListener = (OnQuickMsgFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnQuickMsgFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        msgEditText.requestFocus();
        msgSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"sending "+sendingIp);
                String groups = "";
                for(Map.Entry<Group,Boolean> entry : DBManager.mGroups.entrySet()){
                    //Log.d(TAG,entry.getKey().getName()+": "+entry.getValue());
                    if (entry.getValue() && !msgEditText.getText().toString().equals("")){
                        if(sendingIp != null)
                            dbManager.sendMsg(msgEditText.getText().toString(),entry.getKey().getId(),Message.TYPE_IP,sendingIp);
                        else if (sendingImg != null)
                            dbManager.sendMsg(msgEditText.getText().toString(),entry.getKey().getId(),Message.TYPE_IMG,sendingImg);
                        else
                            dbManager.sendMsg(msgEditText.getText().toString(),entry.getKey().getId(),Message.TYPE_TEXT,null);

                        groups = groups + entry.getKey().getName();
                        groups = groups + ", ";
                    }
                }
                if(!groups.equals(""))
                    groups.substring(0,groups.length()-3);
                mListener.quickMsgSent(groups);
            }
        });
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pickImg();
            }
        });
    }

    public void addIp(InterestPoint interestPoint){
        String text = msgEditText.getText().toString();
        if(sendingIp == interestPoint){
            sendingIp = null;
            text = text.substring(text.indexOf("]")+1,text.length());
            msgEditText.setText(text);
            msgEditText.setSelection(text.length());
        }else{
            sendingIp = interestPoint;
            if(text.startsWith("["))
                text = text.substring(text.indexOf("]")+1,text.length());
            text = "["+interestPoint.getName()+"]"+text;
            msgEditText.setText(text);
            msgEditText.setSelection(text.length());
        }
    }

    public void addImg (Uri uri){
        String text = msgEditText.getText().toString();
        sendingImg = uri;
        File file = new File(uri.getPath());
        if(text.startsWith("["))
            text = text.substring(text.indexOf("]")+1,text.length());
        text = "["+file.getName()+"]"+text;
        msgEditText.setText(text);
        msgEditText.setSelection(text.length());
    }

    public interface OnQuickMsgFragmentInteractionListener {
        public void quickMsgSent(String groups);
        public void pickImg();
    }
}
