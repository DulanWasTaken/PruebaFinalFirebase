package es.udc.tfg.pruebafinalfirebase.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import es.udc.tfg.pruebafinalfirebase.Core.SettingsFragment;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.DestinationPoint;
import es.udc.tfg.pruebafinalfirebase.InterestPoint.InterestPoint;
import es.udc.tfg.pruebafinalfirebase.Map.infoWindowRecyclerViewAdapter;
import es.udc.tfg.pruebafinalfirebase.Messages.Message;
import es.udc.tfg.pruebafinalfirebase.R;

/**
 * Created by Usuario on 18/04/2017.
 */

public class mInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    LayoutInflater inflater;
    Context context;
    SharedPreferences appPreferences;

    public mInfoWindowAdapter(LayoutInflater inflater, Context context){
        this.inflater = inflater;
        this.context = context;
        appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Object tag = marker.getTag();
        if (tag instanceof ArrayList) {
            ArrayList<Message> aux = (ArrayList<Message>) tag;
            List<Message> msgs = aux.subList(Math.max(aux.size() - Integer.parseInt(appPreferences.getString(SettingsFragment.KEY_INFOWINDOW_MESSAGES,"4")), 0), aux.size());
            View myContentsViewText = inflater.inflate(R.layout.info_window_adapter, null);
            TextView titleText = (TextView) myContentsViewText.findViewById(R.id.info_window_title_tv);
            RecyclerView recyclerView = (RecyclerView) myContentsViewText.findViewById(R.id.infowWindow_recyclerView);
            titleText.setText(marker.getTitle());
            if(msgs.size()>0){
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(new infoWindowRecyclerViewAdapter(msgs));
            }else{

            }
            return myContentsViewText;
            /*switch (msg.getType()) {
                case Message.TYPE_TEXT:
                    View myContentsViewText = inflater.inflate(R.layout.info_window_adapter, null);
                    TextView titleText = (TextView) myContentsViewText.findViewById(R.id.info_window_title_tv);
                    TextView contentText = (TextView) myContentsViewText.findViewById(R.id.info_window_content_tv);
                    ImageView imgText = (ImageView) myContentsViewText.findViewById(R.id.infow_window_iv);

                    titleText.setText(marker.getTitle());
                    contentText.setText(marker.getSnippet());
                    imgText.setImageResource(R.drawable.ic_infowindow_chat_text);
                    return myContentsViewText;
                case Message.TYPE_IP:
                    View myContentsViewIp = inflater.inflate(R.layout.info_window_adapter, null);
                    TextView titleIp = (TextView) myContentsViewIp.findViewById(R.id.info_window_title_tv);
                    TextView contentIp = (TextView) myContentsViewIp.findViewById(R.id.info_window_content_tv);
                    ImageView imgIp = (ImageView) myContentsViewIp.findViewById(R.id.infow_window_iv);

                    titleIp.setText(marker.getTitle());
                    contentIp.setVisibility(View.GONE);
                    imgIp.setImageResource(R.drawable.ic_infowindow_chat_ip);
                    return myContentsViewIp;
            }*/
        } else if (tag instanceof InterestPoint) {
            InterestPoint ip = (InterestPoint) tag;
        } else if (tag instanceof DestinationPoint){
            DestinationPoint dp = (DestinationPoint) tag;
            View myContentsViewText = inflater.inflate(R.layout.destination_point_infowindow, null);
            TextView name = (TextView) myContentsViewText.findViewById(R.id.dp_name_infowindow);
            TextView hour = (TextView) myContentsViewText.findViewById(R.id.dp_hour_infowindow);
            name.setText(dp.getName());
            hour.setText(dp.getDestinationTime());
            return myContentsViewText;
        }
        return null;
    }
}
