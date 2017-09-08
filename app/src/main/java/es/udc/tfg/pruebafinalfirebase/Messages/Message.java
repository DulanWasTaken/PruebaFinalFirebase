package es.udc.tfg.pruebafinalfirebase.Messages;

import com.google.android.gms.maps.model.LatLng;

import es.udc.tfg.pruebafinalfirebase.Group.GroupMember;

/**
 * Created by Usuario on 18/02/2017.
 */

public class Message {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IP = 2;
    public static final int TYPE_IMG = 3;

    long time;
    GroupMember sender;
    String msg;
    String ipId;
    String userIp;
    double ipLat,ipLng;
    int type;
    String msgId;

    public Message(){
    }

    public Message(GroupMember sender,String msg,String ipId,String userIp,double ipLat, double ipLng,int type) {
        this.sender = sender;
        this.type = type;
        this.time = System.currentTimeMillis();
        this.msg = msg;
        this.userIp = userIp;
        this.ipId = ipId;
        this.ipLat = ipLat;
        this.ipLng = ipLng;
    }

    public Message(GroupMember sender, String msg, int type) {
        this.sender = sender;
        this.msg = msg;
        this.type = type;
        this.time = System.currentTimeMillis();
    }

    public GroupMember getSender() {
        return sender;
    }

    public String getMsg() {
        return msg;
    }

    public long getTime() {
        return time;
    }

    public String getIpId() {
        return ipId;
    }

    public int getType() {
        return type;
    }

    public String getUserIp() {
        return userIp;
    }

    public double getIpLat() {
        return ipLat;
    }

    public void setIpLat(double ipLat) {
        this.ipLat = ipLat;
    }

    public double getIpLng() {
        return ipLng;
    }

    public void setIpLng(double ipLng) {
        this.ipLng = ipLng;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
