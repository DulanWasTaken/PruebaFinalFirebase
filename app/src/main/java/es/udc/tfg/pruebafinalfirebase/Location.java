package es.udc.tfg.pruebafinalfirebase;

/**
 * Created by Usuario on 18/12/2016.
 */

public class Location {
    private double lat,lng;
    private float accurracy;
    private long time;

    public Location(){

    }

    public Location(double lat, double lng, float accurracy){
        this.lat = lat;
        this.lng = lng;
        this.accurracy = accurracy;
        this.time = System.currentTimeMillis();

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public float getAccurracy() {
        return accurracy;
    }

    public void setAccurracy(float accurracy) {
        this.accurracy = accurracy;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTime(){
        return time;
    }
}
