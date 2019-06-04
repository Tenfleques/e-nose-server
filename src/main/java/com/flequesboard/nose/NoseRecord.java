package com.flequesboard.nose;

import com.flequesboard.redis.AdministrativeStores;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * formats the data structure of the nose for redis storage
 */
public class NoseRecord {

    private Map<String, Float> sensors;
    private String noseID;
    private String sessionID;
    private String date;

    public NoseRecord(){
        noseID = "";
        sessionID = "";
        date ="";
        sensors = new HashMap<>();
    }
    public NoseRecord(Map<String, String> noseMap){
        Gson gson = new Gson();
        noseID = noseMap.getOrDefault("noseID","");
        sessionID = noseMap.getOrDefault("sessionID","");;
        date = noseMap.getOrDefault("date","");;
        String str_sensors = noseMap.getOrDefault("sensors","{}");
        sensors = gson.fromJson(str_sensors,new TypeToken<Map<String, Float>>(){}.getType());
    }

    public NoseRecord(String rec){
        List<String> ss = Arrays.asList(rec.split(","));

        this.noseID = ss.get(ss.indexOf("_id")+1);

        int startOfDate = ss.indexOf("date");
        int startOfSession = ss.indexOf("session");
        int startOfSensors = ss.indexOf("sensors");

        this.date = ss.get(startOfDate + 1);
        this.sessionID = ss.get(startOfSession+1);

        //number of sensors can scale horizontally
        sensors = timeSensorRecord(ss.subList(startOfSensors + 1,ss.size()));
    }

    private Map<String, Float> timeSensorRecord(List<String> sensors){
        Map<String, Float> timeSensorPair = new HashMap<>();
        for(int i = 1; i < sensors.size(); i += 2) {
            timeSensorPair.put(this.date + AdministrativeStores.KEY_SEP.getValue() +sensors.get(i - 1), Float.parseFloat(sensors.get(i)));
        }
        return timeSensorPair;
    }
    public String getDate(){
        return this.date;
    }
    public String getNoseID() {
        return noseID;
    }
    public String getSession() {
        return sessionID;
    }
    public Map<String, Float> getSensors() {
        return sensors;
    }

    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public  boolean isEmpty(){
        return noseID.isEmpty() || sessionID.isEmpty() || sensors.isEmpty();
    }

}
