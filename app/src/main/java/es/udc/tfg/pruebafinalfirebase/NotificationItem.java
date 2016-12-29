package es.udc.tfg.pruebafinalfirebase;

import java.util.ArrayList;

/**
 * Created by Usuario on 26/12/2016.
 */

public class NotificationItem {
    private String groupName;
    private ArrayList<String> members;
    private long time;

    public NotificationItem(){

    }

    public NotificationItem(String groupName, ArrayList<String> members, long time){
        this.groupName=groupName;
        this.members=members;
        this.time=time;
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
}
