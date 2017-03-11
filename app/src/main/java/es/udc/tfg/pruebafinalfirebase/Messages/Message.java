package es.udc.tfg.pruebafinalfirebase.Messages;

import es.udc.tfg.pruebafinalfirebase.Group.GroupMember;

/**
 * Created by Usuario on 18/02/2017.
 */

public class Message {
    long time;
    GroupMember sender;
    String msg;

    public Message(){
    }

    public Message(GroupMember sender,String msg) {
        this.sender = sender;
        this.msg = msg;
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
}
