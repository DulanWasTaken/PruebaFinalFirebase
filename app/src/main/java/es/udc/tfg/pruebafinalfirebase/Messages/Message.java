package es.udc.tfg.pruebafinalfirebase.Messages;

import es.udc.tfg.pruebafinalfirebase.Group.GroupMember;
import es.udc.tfg.pruebafinalfirebase.InterestPoint;

/**
 * Created by Usuario on 18/02/2017.
 */

public class Message {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IP = 2;

    long time;
    GroupMember sender;
    String msg;
    String ipId;
    String userIp;
    int type;

    public Message(){
    }

    public Message(GroupMember sender,String msg,String ipId,String userIp,int type) {
        this.sender = sender;
        this.type = type;
        this.time = System.currentTimeMillis();
        this.msg = msg;
        this.userIp = userIp;
        this.ipId = ipId;
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
}
