package es.udc.tfg.pruebafinalfirebase.InterestPoint;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashMap;

import es.udc.tfg.pruebafinalfirebase.DBManager;
import es.udc.tfg.pruebafinalfirebase.R;


public class InterestPointFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "IPFRAGMENT";

    private String ipId,userId;
    private EditText title_et,description_et;
    private TextView title_tv,description_tv,rating_info;
    private RatingBar ratingBar;
    private Button save_button,delete_button, copy_button, exit_button;
    private OnInterestPointFragmentInteractionListener mListener;
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
            save_button = (Button) v.findViewById(R.id.save_ip_button);
            delete_button = (Button) v.findViewById(R.id.delete_ip_button);
            title_et = (EditText) v.findViewById(R.id.ip_title_et);
            description_et = (EditText) v.findViewById(R.id.ip_description_et);
            rating_info = (TextView) v.findViewById(R.id.ip_info_rating);
            ratingBar = (RatingBar) v.findViewById(R.id.ip_ratingBar);
        }else{
            v = inflater.inflate(R.layout.fragment_interest_point_view, container, false);
            copy_button = (Button) v.findViewById(R.id.copy_ip_button);
            exit_button = (Button) v.findViewById(R.id.exit_ip_button);
            title_tv = (TextView) v.findViewById(R.id.ip_title_tv);
            description_tv = (TextView) v.findViewById(R.id.ip_description_tv);
            ratingBar = (RatingBar) v.findViewById(R.id.ip_ratingBar2);
            rating_info = (TextView) v.findViewById(R.id.ip_info_rating2);
        }


        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInterestPointFragmentInteractionListener) {
            mListener = (OnInterestPointFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInterestPointFragmentInteractionListener");
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if(userId.equals(dbManager.getId())){
            ratingBar.setIsIndicator(true);
            final InterestPoint ip = dbManager.getInterestPoint(userId,ipId);
            Log.d(TAG,"");
            title_et.setText(ip.getName());
            description_et.setText(ip.getDescription());
            if(ip.getRating()!=null) {
                ratingBar.setRating(meanRating(ip.getRating().values()));
                rating_info.setText(ip.getRating().size() + " total ratings");
            }else{
                ratingBar.setRating(0);
                rating_info.setText("0" + " total ratings");
            }
            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final InterestPoint interestP = ip;
                    String name = title_et.getText().toString();
                    String desc = description_et.getText().toString();
                    interestP.setName(name);
                    interestP.setDescription(desc);

                    if(!name.equals("")&&!desc.equals(""))
                        dbManager.saveInterestPoint(ipId,interestP);
                    mListener.exitIpFragment(0);
                }
            });
            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbManager.deleteInterestPoint(userId,ipId);
                    mListener.exitIpFragment(1);
                }
            });
        }else{
            Log.d(TAG,"REQUIERO PUNTO DE INTERES");
            dbManager.getInterestPoint(userId,ipId);
        }
    }

    public void onInterestPointReceived(final InterestPoint ip){
        Log.d(TAG,"IP RECEIVED WITH NAME: "+ip.getName());
        ratingBar.setIsIndicator(false);
        title_tv.setText(ip.getName());
        description_tv.setText(ip.getDescription());
        HashMap<String,Float> aux = ip.getRating();
        if(aux!=null){
            Float mRating = aux.get(dbManager.getId());
            if(mRating!=null)
                ratingBar.setRating(mRating);
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                dbManager.rateInterestPoint(userId,ipId,v);
            }
        });

        copy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.copyInterestPoint(ip);
                mListener.copyInterestPoint(ip);
            }
        });

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.exitIpFragment(0);
            }
        });
        rating_info.setVisibility(View.GONE);
    }

    private float meanRating(Collection<Float> ratings){
        float sum = 0;
        for(float f : ratings)
            sum=sum+f;
        return sum/ratings.size();
    }

    public interface OnInterestPointFragmentInteractionListener {
        void exitIpFragment(int mode);
        void copyInterestPoint(InterestPoint ip);
    }
}
