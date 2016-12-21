package es.udc.tfg.pruebafinalfirebase;

import java.util.ArrayList;

/**
 * Created by Usuario on 18/12/2016.
 */

public class InterestPoint {
    private Location location;
    private String name;
    private String description;
    private ArrayList<Integer> rating;

    public InterestPoint (){

    }

    public InterestPoint(Location location, String name, ArrayList<Integer> rating, String description) {
        this.location = location;
        this.name = name;
        this.rating = rating;
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public ArrayList<Integer> getRating() {
        return rating;
    }

    public void addRating(Integer rating) {
        this.rating.add(rating);
    }
}
