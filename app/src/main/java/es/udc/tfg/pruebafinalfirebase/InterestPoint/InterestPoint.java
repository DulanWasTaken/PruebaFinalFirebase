package es.udc.tfg.pruebafinalfirebase.InterestPoint;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Usuario on 18/12/2016.
 */

public class InterestPoint extends Point {
    private String description;
    private String userId;
    private HashMap<String,Float> rating;

    public InterestPoint(){

    }

    public InterestPoint(double lat, double lng, String name, String description, String userId, String ipId) {
        super(lat,lng,name,ipId);
        this.description = description;
        this.userId = userId;
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


    public void setUserId(String userId) {
        this.userId = userId;
    }

}
