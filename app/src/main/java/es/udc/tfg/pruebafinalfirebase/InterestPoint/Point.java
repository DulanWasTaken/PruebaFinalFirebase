package es.udc.tfg.pruebafinalfirebase.InterestPoint;

/**
 * Created by Usuario on 06/07/2017.
 */

public class Point {
    public double lat,lng;
    public String name,ipId;
    public long time;

    public Point(){

    }

    public Point(double lat, double lng, String name,String ipId) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.ipId = ipId;
        this.time = System.currentTimeMillis();
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

    public String getIpId() {
        return ipId;
    }

    public void setIpId(String ipId) {
        this.ipId = ipId;
    }

    public long getTime() {
        return time;
    }
}
