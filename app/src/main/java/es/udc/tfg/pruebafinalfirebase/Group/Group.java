package es.udc.tfg.pruebafinalfirebase.Group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import es.udc.tfg.pruebafinalfirebase.InterestPoint.Point;

/**
 * Created by Usuario on 21/12/2016.
 */

public class Group {
    public final static int GROUP_STATE_ACTIVE = 1;
    public final static int GROUP_STATE_STOPPED = 0;

    private String name;
    private ArrayList<GroupMember> membersId;
    private ArrayList<GroupMember> invitations;
    private Map<String,Point> destinationPoints;
    private ArrayList<String> admins;
    private long time;
    private String id;

    public Group() {
    }

    public Group(String name,String id, ArrayList<GroupMember> membersId,ArrayList<String> admins,ArrayList<GroupMember> invitations) {
        this.name = name;
        this.id = id;
        this.membersId = membersId;
        this.admins = admins;
        this.invitations = invitations;

        this.time = System.currentTimeMillis();
    }

    public ArrayList<String> getAdmins() {
        return admins;
    }

    public void setAdmins(ArrayList<String> admins) {
        this.admins = admins;
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

    public ArrayList<String> getMembersNick(){
        ArrayList<String> result = new ArrayList<>();
        if(membersId!=null)
            for(GroupMember member : membersId)
                result.add(member.getNick());

        return result;
    }

    public void setMembersId(ArrayList<GroupMember> membersId) {
        this.membersId = membersId;
    }

    public long getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<GroupMember> getInvitations() {
        return invitations;
    }

    public void setInvitations(ArrayList<GroupMember> invitations) {
        this.invitations = invitations;
    }

    public void addMember(GroupMember member){
        if(membersId == null)
            membersId = new ArrayList<>();
        if (!membersId.contains(member))
            membersId.add(member);
    }

    public void addInvitation(GroupMember member){
        if(invitations == null)
            invitations = new ArrayList<>();
        boolean contains = false;
        for(GroupMember m : invitations){
            if(m.getMemberId().equals(member.getMemberId()))
                contains = true;
        }
        if (!contains)
            invitations.add(member);
    }

    public void removeInvitation(String id){
        if(id!=null && !id.equals("")){
            Iterator<GroupMember> iter = invitations.iterator();
            while (iter.hasNext()) {
                GroupMember member = iter.next();
                if (member.getMemberId().equals(id))
                    iter.remove();
            }
            /*for(GroupMember member : membersId){
                if(member.getMemberId().equals(id))
                    membersId.remove(member);
            }*/
        }
    }

    public void removeMember(String id){
        if(id!=null && !id.equals("")){
            Iterator<GroupMember> iter = membersId.iterator();
            while (iter.hasNext()) {
                GroupMember member = iter.next();
                if (member.getMemberId().equals(id))
                    iter.remove();
            }
            /*for(GroupMember member : membersId){
                if(member.getMemberId().equals(id))
                    membersId.remove(member);
            }*/
        }
    }

    public Map<String, Point> getDestinationPoints() {
        return destinationPoints;
    }

    public void setDestinationPoints(Map<String, Point> destinationPoints) {
        this.destinationPoints = destinationPoints;
    }
}
