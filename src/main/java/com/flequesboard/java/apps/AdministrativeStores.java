package com.flequesboard.java.apps;

enum AdministrativeStores {
    LIVE_DATES("LIVEDATES"),
    ENOSE_IDS("enose-IDS"),
    ENOSE_SESIONS("enose-sessions-");
    String value;
    AdministrativeStores(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
