package es.udc.tfg.pruebafinalfirebase.Map;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Messages.Message;

/**
 * Created by Usuario on 03/03/2017.
 */

public class MapMarker {
    public final static int LOCATION_MARKER = 1;
    public final static int DESTINATION_MARKER = 2;
    public final static int PUBLIC_INTEREST_POINT = 3;

    Marker marker;
    MarkerOptions markerOptions;
    String Id;
    String groupId;
    Long time;
    int type;
    boolean active;
    ArrayList<Message> messages;
    String dpHour;

    public MapMarker() {
    }


    public MapMarker(MarkerOptions markerOptions, String Id, String groupId, int type, boolean active, Message msg) {
        this.marker = null;
        this.Id = Id;
        this.groupId = groupId;
        this.type = type;
        this.active = active;
        this.markerOptions = markerOptions;
        this.messages = new ArrayList<>();
        if(msg!=null)
            this.messages.add(msg);
    }

    public MapMarker(MarkerOptions markerOptions, String Id, String groupId, String hour, int type) {
        this.marker = null;
        this.Id = Id;
        this.groupId = groupId;
        this.type = type;
        this.markerOptions = markerOptions;
        this.active = true;
        this.dpHour = hour;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        groupId = groupId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message msg){
        messages.add(msg);
    }

    public String getDpHour() {
        return dpHour;
    }

    public void setDpHour(String dpHour) {
        this.dpHour = dpHour;
    }
}
