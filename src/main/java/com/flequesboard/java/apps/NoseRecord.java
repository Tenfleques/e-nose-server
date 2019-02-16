package com.flequesboard.java.apps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 *
 * */
class NoseRecord {

    private Map<String, String> sensors;;
    private String noseID;
    private String sessionID;
    private String date;

    NoseRecord(String rec, String noseID){
        List<String> ss = Arrays.asList(rec.split(","));

        this.noseID = noseID;

        int startOfDate = ss.indexOf("date");
        int startOfSession = ss.indexOf("session");
        int startOfSensors = ss.indexOf("sensors");

        this.date = ss.get(startOfDate + 1);
        this.sessionID = ss.get(startOfSession+1);


        sensors = timeSensorRecord(ss.subList(startOfSensors + 1,ss.size()));
        //System.out.println(ss.subList(startOfSensors + 1,ss.size()));
    }
    private Map<String, String> timeSensorRecord(List<String> sensors){
        Map<String, String> timeSensorPair = new HashMap<>();
        for(int i = 1; i < sensors.size(); i += 2) {
            timeSensorPair.put(this.date + "__sep__" +sensors.get(i - 1), sensors.get(i));
        }
        return timeSensorPair;
    }

    String getNoseID() {
        return noseID;
    }

    String getSessionID() {
        return sessionID;
    }

    Map<String, String> getRedisReadyRecord(){
        return  this.sensors;
    }

}
