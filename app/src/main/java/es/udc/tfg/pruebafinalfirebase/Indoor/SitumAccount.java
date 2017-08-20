package es.udc.tfg.pruebafinalfirebase.Indoor;

/**
 * Created by Usuario on 02/08/2017.
 */

public class SitumAccount {
    String email,pwd,publicName,userId;

    public SitumAccount() {

    }

    public SitumAccount(String email, String pwd, String publicName, String userId) {
        this.email = email;
        this.pwd = pwd;
        this.publicName = publicName;
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
