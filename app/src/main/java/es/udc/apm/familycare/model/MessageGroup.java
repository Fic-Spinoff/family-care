package es.udc.apm.familycare.model;

import java.util.Map;

/**
 * Created by Gonzalo on 29/04/2018.
 * Saves MessageGroup data for messaging groups
 */

public class MessageGroup {

    private Map<String, String> guards;

    public MessageGroup() {
    }

    public MessageGroup(Map<String, String> guards) {
        this.guards = guards;
    }

    public Map<String, String> getGuards() {
        return guards;
    }

    public void setGuards(Map<String, String> guards) {
        this.guards = guards;
    }
}
