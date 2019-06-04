package com.flequesboard.redis;

public enum AdministrativeStores {
    LIVE_DATES("LIVEDATES"),
    ENOSE_IDS_KEY("enose_IDS"),
    ENOSE_SESIONS_KEY("enose_sessions_"),
    KEY_SEP("__sep__"),
    SESSION_RECORDS_KEY("enose_session_records_"),
    DEFAULT_ORG("ru.vsuet.noses");
    String value;
    AdministrativeStores(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
