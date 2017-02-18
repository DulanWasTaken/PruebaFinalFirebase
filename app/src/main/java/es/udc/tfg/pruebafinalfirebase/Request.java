package es.udc.tfg.pruebafinalfirebase;

/**
 * Created by Usuario on 19/12/2016.
 */

public class Request {
    public final static int REQUEST_ACCEPTED = 1;
    public final static int REQUEST_WAITING = 0;
    public final static int REQUEST_CANCELLED = -1;

    public final static int REQUEST_TYPE_GROUP = 11;
    public final static int REQUEST_TYPE_DELETED = 33;
    public final static int REQUEST_TYPE_START_SHARING = 22;

    private String idGroup,id;
    private long time;
    private int state;
    private int type;

    public Request (){

    }

    public Request(String idGroup, int type, String id) {
        this.idGroup = idGroup;
        this.state = REQUEST_WAITING;
        this.time = System.currentTimeMillis();
        this.type = type;
        this.id = id;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setType(int type) {
        this.type = type;
    }
}
