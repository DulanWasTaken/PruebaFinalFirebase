package es.udc.tfg.pruebafinalfirebase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class InterestPointFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String ipId,userId;
    private EditText title_et,description_et;
    private TextView title_tv,description_tv,rating_info;
    private RatingBar ratingBar;
    private Button save_button;
    //private OnInterestPointFragmentInteractionListener mListener;
    private DBManager dbManager = DBManager.getInstance();

    public InterestPointFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InterestPointFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InterestPointFragment newInstance(String param1, String param2) {
        InterestPointFragment fragment = new InterestPointFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ipId = getArguments().getString(ARG_PARAM1);
            userId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v;
        if(userId.equals(dbManager.getId())){
            v = inflater.inflate(R.layout.fragment_interest_point_edit, container, false);
            save_button = (Button) v.findViewById(R.id.ip_save);
            save_button.setVisibility(View.VISIBLE);
            title_et = (EditText) v.findViewById(R.id.ip_title_et);
            description_et = (EditText) v.findViewById(R.id.ip_description_et);
            rating_info = (TextView) v.findViewById(R.id.ip_info_rating);
            ratingBar = (RatingBar) v.findViewById(R.id.ip_ratingBar);
        }else{
            v = inflater.inflate(R.layout.fragment_interest_point_view, container, false);
            title_tv = (TextView) v.findViewById(R.id.ip_title_tv);
            description_tv = (TextView) v.findViewById(R.id.ip_description_tv);
            ratingBar = (RatingBar) v.findViewById(R.id.ip_ratingBar2);
        }


        return v;
    }


    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInterestPointFragmentInteractionListener) {
            mListener = (OnInterestPointFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInterestPointFragmentInteractionListener");
        }
    }*/



    @Override
    public void onResume() {
        super.onResume();
        if(userId.equals(dbManager.getId())){
            ratingBar.setIsIndicator(true);
            InterestPoint ip = dbManager.getInterestPoint(userId,ipId);
            title_et.setText(ip.getName());
            description_et.setText(ip.getDescription());
            ratingBar.setRating(meanRating(ip.getRating().values()));
            rating_info.setText(ip.getRating().size()+" total ratings");
            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = title_et.getText().toString();
                    String desc = description_et.getText().toString();
                    if(!name.equals("")&&!desc.equals(""))
                        dbManager.saveInterestPoint(ipId,name,desc);
                }
            });
        }else{
            dbManager.getInterestPoint(userId,ipId);
        }
    }

    public void onInterestPointReceived(InterestPoint ip){
        ratingBar.setIsIndicator(false);
        title_tv.setText(ip.getName());
        description_tv.setText(ip.getDescription());
        HashMap<String,Float> aux = ip.getRating();
        if(aux!=null){
            Float mRating = aux.get(dbManager.getId());
            ratingBar.setRating(mRating);
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                dbManager.rateInterestPoint(userId,ipId,v);
            }
        });
    }

    private float meanRating(Collection<Float> ratings){
        float sum = 0;
        for(float f : ratings)
            sum=sum+f;
        return sum/ratings.size();
    }
/*
    public interface OnInterestPointFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }*/
}
