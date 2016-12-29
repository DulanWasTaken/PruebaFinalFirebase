package es.udc.tfg.pruebafinalfirebase;

import java.util.ArrayList;

/**
 * Created by Usuario on 21/12/2016.
 */

public class Group {
    public final static int GROUP_STATE_ACTIVE = 1;
    public final static int GROUP_STATE_STOPPED = 0;

    private String name;
    private ArrayList<GroupMember> membersId;
    private long time;
    private int state;

    public Group() {
    }

    public Group(String name, ArrayList<GroupMember> membersId) {
        this.name = name;
        this.membersId = membersId;
        this.time = System.currentTimeMillis();
        this.state = GROUP_STATE_ACTIVE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<GroupMember> getMembersId() {
        return membersId;
    }

    public void setMembersId(ArrayList<GroupMember> membersId) {
        this.membersId = membersId;
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
}
