package es.udc.tfg.pruebafinalfirebase;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Usuario on 18/12/2016.
 */

public class User {
    private String id;
    private String phoneNumber;
    private String email;
    private String key;
    private String nick;
    private Location location;
    private ArrayList<InterestPoint> interestPoints;
    private long time;
    private String status;
    private ArrayList<String> groupsId;

    public User (){

    }

    public User(String email, String key, String id, String phoneNumber,String nick, Location location, ArrayList<InterestPoint> interestPoints,String status, ArrayList<String> groupsId) {
        this.email = email;
        this.key = key;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.interestPoints = interestPoints;
        this.time = System.currentTimeMillis();
        this.status = status;
        this.nick = nick;
        this.groupsId = groupsId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<InterestPoint> getInterestPoints() {
        return interestPoints;
    }

    public void setInterestPoints(ArrayList<InterestPoint> interestPoints) {
        this.interestPoints = interestPoints;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void addGroup(String groupId){
        groupsId.add(groupId);
    }

    public ArrayList<String> getGroupsId(){
        return groupsId;
    }
}
