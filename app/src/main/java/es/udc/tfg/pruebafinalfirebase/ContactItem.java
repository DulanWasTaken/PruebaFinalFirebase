package es.udc.tfg.pruebafinalfirebase;

/**
 * Created by Usuario on 19/12/2016.
 */

public class ContactItem {
    String name,data;
    boolean checked;

    public ContactItem() {
    }

    public ContactItem(String name, String data) {
        this.name = name;
        this.data = data;
        this.checked = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
