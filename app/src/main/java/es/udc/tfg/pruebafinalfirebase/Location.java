package es.udc.tfg.pruebafinalfirebase;

/**
 * Created by Usuario on 18/12/2016.
 */

public class Location {
    private double lat,lng;
    private float accurracy,bearing;
    private long time;
    private boolean active;

    public Location(){

    }

    public Location(double lat, double lng, float accurracy,float bearing,boolean active){
        this.lat = lat;
        this.lng = lng;
        this.accurracy = accurracy;
        this.time = System.currentTimeMillis();
        this.active = active;
        this.bearing = bearing;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }
}
