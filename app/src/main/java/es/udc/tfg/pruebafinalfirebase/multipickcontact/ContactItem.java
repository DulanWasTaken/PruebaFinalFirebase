package es.udc.tfg.pruebafinalfirebase.multipickcontact;

/**
 * Created by Usuario on 19/12/2016.
 */

class ContactItem {
    private String name,data,img_uri;
    private boolean checked;

    public ContactItem() {
    }

    public ContactItem(String name, String data,String img_uri) {
        this.name = name;
        this.data = data;
        this.checked = false;
        this.img_uri = img_uri;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected String getData() {
        return data;
    }

    protected void setData(String data) {
        this.data = data;
    }

    protected boolean isChecked() {
        return checked;
    }

    protected void setChecked(boolean checked) {
        this.checked = checked;
    }

    protected String getImg_uri() {
        return img_uri;
    }

    protected void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

    @Override
    public boolean equals(Object o) {
        ContactItem c = (ContactItem) o;
        return this.name.equals(c.getName());
    }
}
