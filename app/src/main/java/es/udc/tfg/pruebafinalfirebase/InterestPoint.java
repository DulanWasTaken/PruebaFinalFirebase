package es.udc.tfg.pruebafinalfirebase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Usuario on 18/12/2016.
 */

public class InterestPoint {
    private double lat,lng;
    private String name;
    private String description;
    private String userId,ipId;
    private HashMap<String,Float> rating;

    public InterestPoint (){

    }

    public InterestPoint(double lat, double lng, String name, String description, String userId, String ipId) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.ipId = ipId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Float> getRating() {
        return rating;
    }

    public void setRating(HashMap<String, Float> rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public String getIpId() {
        return ipId;
    }
}
