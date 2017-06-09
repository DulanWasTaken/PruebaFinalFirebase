package es.udc.tfg.pruebafinalfirebase;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import es.udc.tfg.pruebafinalfirebase.Messages.Message;

/**
 * Created by Usuario on 18/04/2017.
 */

public class mInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    LayoutInflater inflater;

    public mInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }
    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Object tag = marker.getTag();
        if (tag instanceof Message) {
            Message msg = (Message) tag;
            switch (msg.getType()) {
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
            }
        } else if (tag instanceof InterestPoint) {
            InterestPoint ip = (InterestPoint) tag;
        }
        return null;
    }
}
