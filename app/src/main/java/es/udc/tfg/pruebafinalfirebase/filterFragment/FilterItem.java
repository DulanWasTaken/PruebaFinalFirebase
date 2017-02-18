package es.udc.tfg.pruebafinalfirebase.filterFragment;

/**
 * Created by Usuario on 03/01/2017.
 */

public class FilterItem {
    private String groupName,groupId;

    public FilterItem() {
    }

    public FilterItem(String groupName, String groupId) {
        this.groupName = groupName;
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
