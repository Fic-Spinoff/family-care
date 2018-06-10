package es.udc.apm.familycare.model;

/**
 * Created by Gonzalo on 29/04/2018.
 */

public class Member {

    private String uid;
    private String name;
    private String photo;

    public Member() {
    }

    public Member(String uid, String name, String photo) {
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
}
