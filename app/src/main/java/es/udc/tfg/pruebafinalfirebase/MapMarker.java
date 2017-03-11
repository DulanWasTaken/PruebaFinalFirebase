package es.udc.tfg.pruebafinalfirebase;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Usuario on 03/03/2017.
 */

public class MapMarker {
    public final static int LOCATION_MARKER = 1;

    Marker marker;
    MarkerOptions markerOptions;
    String userId;
    String groupId;
    Long time;
    int type;

    public MapMarker() {
    }

    public MapMarker(Marker marker,MarkerOptions markerOptions, String userId, String groupId, int type) {
        this.marker = marker;
        this.userId = userId;
        this.groupId = groupId;
        this.type = type;
        this.markerOptions = markerOptions;
    }

    public MapMarker(MarkerOptions markerOptions, String userId, String groupId, int type) {
        this.userId = userId;
        this.groupId = groupId;
        this.type = type;
        this.markerOptions = markerOptions;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
