package es.udc.tfg.pruebafinalfirebase.Group;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import es.udc.tfg.pruebafinalfirebase.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnGroupCreationFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class GroupCreationFragment extends Fragment {

    private OnGroupCreationFragmentInteractionListener mListener;
    private Button addImageButton;
    private ImageView imageView;
    private EditText editText;
    private CheckBox checkBox;
    private Uri imgUri;

    public GroupCreationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_creation, container, false);
        addImageButton = (Button) v.findViewById(R.id.group_creation_image_button);
        imageView = (ImageView) v.findViewById(R.id.group_creation_image);
        editText = (EditText) v.findViewById(R.id.group_creation_name);
        checkBox = (CheckBox) v.findViewById(R.id.group_creation_allow_invite);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupCreationFragmentInteractionListener) {
            mListener = (OnGroupCreationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pickImg();
            }
        });
    }

    public void setImg(Uri uri){
        imgUri = uri;
        imageView.setImageURI(uri);
    }

    public String getName(){
        return editText.getText().toString();
    }

    public boolean getInviteMode(){
        return checkBox.isChecked();
    }

    public Uri getImgUri (){
        return imgUri;
    }

    public interface OnGroupCreationFragmentInteractionListener {
        // TODO: Update argument type and name
        void pickImg();
    }
}
