package com.flequesboard.java.apps;

import org.apache.kafka.streams.KeyValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//nose_id, date, flag, session_id,  sensor_1, ... ,sensor_n
public class NoseRecord {
    private List<String> sensors = new ArrayList<>();;
    private String noseID = "";
    private Long date,sessionID;
    private Integer flag;
    NoseRecord(String rec){

        List<String> record = Arrays.asList(rec.split(","));
        if(record.size() < 10) //malformed record
            return;

        this.noseID = record.get(RecordFields.NOSE_FIELD.getValue());
        this.sessionID = Long.parseLong(record.get(RecordFields.SESSION_FIELD.getValue()));
        this.date = Long.parseLong(record.get(RecordFields.DATE_FIELD.getValue()));
        this.flag = Integer.parseInt(record.get(RecordFields.FLAG.getValue()));
        sensors = record.stream()
                        .filter(val-> !val.equals(noseID) && !val.equals(sessionID.toString()) && !val.equals(date.toString())
                                && !val
                                .equals(flag.toString()))
                        .collect(Collectors.toList());
    }
    NoseRecord(String noseId, String sessionId){
        this.noseID = noseId;
        this.sessionID = Long.parseLong(sessionId);
        this.date = sessionID;
    }
    public String getKey(){
        return this.noseID + "_session_" + this.sessionID;
    }
    public KeyValue<String,String> getRecord() {
        return KeyValue.pair(this.getKey(),this.date + "," + this.flag + ", " + this.sensors.toString());
    }
    KeyValue<String,String> getSessionRecord(){
        return KeyValue.pair(this.noseID,this.date + "," + this.sessionID + ", "  + this.flag + ", " + this.sensors
                .toString());
    }
    String getNoseID() {
        return noseID;
    }

    public Long getDate() {
        return date;
    }

    Long getSessionID() {
        return sessionID;
    }

    public List<String> getSensors() {
        return sensors;
    }
}
