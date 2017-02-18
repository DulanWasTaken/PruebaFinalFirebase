package es.udc.tfg.pruebafinalfirebase.notificationsFragment;

import java.util.ArrayList;

/**
 * Created by Usuario on 26/12/2016.
 */

public class NotificationItem {
    private String groupName,requestId;
    private ArrayList<String> members;
    private long time;

    public NotificationItem(){

    }

    public NotificationItem(String groupName, ArrayList<String> members, long time,String requestId){
        this.groupName=groupName;
        this.members=members;
        this.time=time;
        this.requestId=requestId;
    }

    public String getGroupName() {
        return groupName;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public long getTime() {
        return time;
    }

    public String getRequestId() {
        return requestId;
    }
}
