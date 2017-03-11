package es.udc.tfg.pruebafinalfirebase.Group;

/**
 * Created by Usuario on 24/12/2016.
 */

public class GroupMember {
    private String memberId,nick;
    private int state;
    private long time, lastConection;

    public GroupMember() {
    }

    public GroupMember(int state, String memberId,String nick) {
        this.state = state;
        this.memberId = memberId;
        this.nick = nick;
        this.time = System.currentTimeMillis();
        this.lastConection = System.currentTimeMillis();
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setConction(){
        this.lastConection = System.currentTimeMillis();
    }

    public String getNick() {
        return nick;
    }
}
