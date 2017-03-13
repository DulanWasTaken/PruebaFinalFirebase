package es.udc.tfg.pruebafinalfirebase.Group;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Usuario on 21/12/2016.
 */

public class Group {
    public final static int GROUP_STATE_ACTIVE = 1;
    public final static int GROUP_STATE_STOPPED = 0;

    private String name;
    private ArrayList<GroupMember> membersId;
    private ArrayList<String> admins;
    private long time;
    private String id;

    public Group() {
    }

    public Group(String name, ArrayList<GroupMember> membersId,ArrayList<String> admins) {
        this.name = name;
        this.membersId = membersId;
        this.admins = admins;
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
        for(GroupMember member : membersId){
            result.add(member.getNick());
        }
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

    public void addMember(GroupMember member){
        if(membersId == null)
            membersId = new ArrayList<>();
        if (!membersId.contains(member))
            membersId.add(member);
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
}
