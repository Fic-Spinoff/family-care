package es.udc.apm.familycare.model;

import com.google.firebase.Timestamp;

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
    private String vipName;
    private Timestamp stillSince;
    private String fcmId;

    public User() {

    }

    public User(String uid, String name, String photo, String role, String link, String vip) {
        this.uid = uid;
        this.name = name;
        this.photo = photo;
        this.role = role;
        this.link = link;
        this.vip = vip;
    }

    public User(String uid, String name, String photo, String fcmId){
        this.uid = uid;
        this.name = name;
        this.photo = photo;
        this.fcmId = fcmId;
    }

    public void setStillSince(Timestamp stillSince) {
        this.stillSince = stillSince;
    }

    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    public Timestamp getStillSince() {
        return stillSince;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
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
