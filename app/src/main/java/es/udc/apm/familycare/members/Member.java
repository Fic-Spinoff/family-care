package es.udc.apm.familycare.members;

/**
 * Created by Gonzalo on 23/04/2018.
 */

public class Member {
    private String id;
    private String photo;
    private String name;
    private String email;

    public Member() {

    }

    public Member(String id, String photo, String name, String email) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
