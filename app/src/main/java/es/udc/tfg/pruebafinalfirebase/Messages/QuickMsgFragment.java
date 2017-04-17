package es.udc.tfg.pruebafinalfirebase.Messages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Map;

import es.udc.tfg.pruebafinalfirebase.DBManager;
import es.udc.tfg.pruebafinalfirebase.Group.Group;
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
    private ImageButton msgSendMsg;
    private DBManager dbManager = DBManager.getInstance();

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
                for(Map.Entry<Group,Boolean> entry : DBManager.mGroups.entrySet()){
                    Log.d(TAG,entry.getKey().getName()+": "+entry.getValue());
                    if (entry.getValue() && !msgEditText.getText().toString().equals(""))
                        dbManager.sendMsg(msgEditText.getText().toString(),entry.getKey().getId());
                }
                mListener.quickMsgSent();
                getFragmentManager().popBackStack();
            }
        });
    }

    public interface OnQuickMsgFragmentInteractionListener {
        public void quickMsgSent();
    }
}
