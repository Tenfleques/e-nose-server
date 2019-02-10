package com.flequesboard.java.apps;

public enum  RecordFields {
    NOSE_FIELD(0),
    DATE_FIELD (1),
    FLAG(2),
    SESSION_FIELD(3);

    private final int value;

    RecordFields(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
