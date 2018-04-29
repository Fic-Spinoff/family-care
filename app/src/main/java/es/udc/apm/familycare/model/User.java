package es.udc.apm.familycare.model;

/**
 * Created by Gonzalo on 29/04/2018.
 */

public class User {

    private String uid;
    private String name;
    private String photo;
    private String role;
    private String link;
    private String vip;

    public User(){
    }

    public User(String uid, String name, String photo, String role, String link, String vip) {
        this.uid = uid;
        this.name = name;
        this.photo = photo;
        this.role = role;
        this.link = link;
        this.vip = vip;
    }

    public User(String uid, String name, String photo) {
        this.uid = uid;
        this.name = name;
        this.photo = photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }
}
