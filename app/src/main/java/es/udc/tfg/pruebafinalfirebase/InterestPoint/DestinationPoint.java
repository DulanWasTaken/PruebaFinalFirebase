package es.udc.tfg.pruebafinalfirebase.InterestPoint;

/**
 * Created by Usuario on 21/08/2017.
 */

public class DestinationPoint extends Point {
    public String destinationTime;

    public DestinationPoint(){

    }

    public DestinationPoint(double lat, double lng, String name,String ipId,String destinationTime){
        super(lat,lng,name,ipId);
        this.destinationTime = destinationTime;
    }

    public String getDestinationTime() {
        return destinationTime;
    }

    public void setDestinationTime(String destinationTime) {
        this.destinationTime = destinationTime;
    }
}
