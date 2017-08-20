package es.udc.tfg.pruebafinalfirebase;

/**
 * Created by Usuario on 18/12/2016.
 */

public class Location {
    private double lat,lng;
    private float accurracy,bearing;
    private long time;
    private boolean active,isIndoor;
    private String buildingId,floorId;

    public Location(){

    }

    public Location(double lat, double lng, float accurracy,float bearing,boolean active,boolean isIndoor){
        this.lat = lat;
        this.lng = lng;
        this.accurracy = accurracy;
        this.time = System.currentTimeMillis();
        this.active = active;
        this.bearing = bearing;
        this.isIndoor = isIndoor;
    }

    public Location(double lat, double lng, float accurracy, float bearing, boolean active, boolean isIndoor, String buildingId, String floorId) {
        this.lat = lat;
        this.lng = lng;
        this.accurracy = accurracy;
        this.bearing = bearing;
        this.active = active;
        this.isIndoor = isIndoor;
        this.buildingId = buildingId;
        this.floorId = floorId;
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

    public boolean isIndoor() {
        return isIndoor;
    }

    public void setIndoor(boolean indoor) {
        isIndoor = indoor;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }
}
