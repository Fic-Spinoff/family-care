package es.udc.apm.familycare.model;

public enum EventType {
        FALL(1), BATTERY(2), LOCATION(3), CALL(4);
        private final int value;

        EventType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }