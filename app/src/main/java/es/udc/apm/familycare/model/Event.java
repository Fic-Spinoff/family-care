package es.udc.apm.familycare.model;

import com.google.firebase.Timestamp;

public class Event {

    private int type;

    // Notification title
    private String title;

    // Notification body
    private String body;

    // Event date
    private Timestamp date;

    // TODO: More data for activity log

    public Event(){

    }

    public Event(int type, String title, String body, Timestamp date) {
        this.type = type;
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
