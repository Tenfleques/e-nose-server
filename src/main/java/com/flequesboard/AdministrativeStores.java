package com.flequesboard;

enum AdministrativeStores {
    LIVE_DATES("LIVEDATES"),
    ENOSE_IDS("enose_IDS"),
    ENOSE_SESIONS("enose_sessions_"),
    KEY_SEP("__sep__"),
    SESSION_RECORDS("enose_session_records_"),
    DEFAULT_ORG("ru.vsuet.noses");
    String value;
    AdministrativeStores(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
