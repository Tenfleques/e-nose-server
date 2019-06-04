package com.flequesboard.redis;


import com.flequesboard.nose.NoseRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;

import java.lang.reflect.Type;
import java.util.*;


public class RedisSink {
    private RedisClient redisClient;
    private String getNoseReadingCollectionKey(String ownerID, String noseID, String sessionID){
        return ownerID + "_" + AdministrativeStores.SESSION_RECORDS_KEY.getValue() + "_" +  noseID + "_" + sessionID;
    }
    private String getNoseCollectionKey(String ownerID){
        return ownerID + "_" + AdministrativeStores.ENOSE_IDS_KEY.getValue();
    }
    private String getSessionsCollectionKey(String ownerID, String noseID){
        return ownerID + "_" + AdministrativeStores.ENOSE_SESIONS_KEY.getValue() + "_" + noseID;
    }


    public RedisSink(String redishost, int redisport) {
        this.redisClient = new RedisClient(
                RedisURI.create("redis://"+redishost+":"+redisport));
    }
//    public String sinkNose(String noseID, String owner){
//        Map<String, String> nose = new HashMap<>();
//        nose.put("noseID", noseID.hashCode() + "");
//        nose.put("title", noseID);
//        nose.put("short_description", "");
//        nose.put("date_reg", new Date().getTime()+"");
//        nose.put("org", owner);
//
//        return this.sinkNose(nose, owner);
//    }
    public String sinkNose(Map<String, String> nose, String owner){
        String status = "";

        try{
            String id = nose.get("noseID");

            RedisConnection<String, String> connection = redisClient.connect();

            String set_key = getNoseCollectionKey(owner);

            Gson gson = new GsonBuilder().create();
            String json_nose = gson.toJson(nose);
            Map<String, String> nose_map = new HashMap<>();

            nose_map.put( id, json_nose);

            status = connection.hmset(set_key, nose_map);
            connection.close();

        }catch (NullPointerException e){
            System.out.println(e.getMessage() + 54 + " redissink.java");
        }

        return status;
    }
    public String sinkNoseSession(Map<String, String> noseSession, String owner){
        String status = "fail";
        try{
            String id = noseSession.get("noseID");
            String session_key = this.getSessionsCollectionKey(owner, id);

            RedisConnection<String, String> connection = redisClient.connect();

            Gson gson = new GsonBuilder().create();
            String json_session = gson.toJson(noseSession);

            Map<String, String> nose_session = new HashMap<>();
            nose_session.put(noseSession.getOrDefault("sessionID", "unknown"), json_session);

            status = connection.hmset(session_key, nose_session);

            connection.close();

        }catch (NullPointerException e){
            System.out.println(e.getMessage() + 78 + " redissink.java");
        }
        return status;
    }
    public String sinkNoseRecord(NoseRecord noseRecord, String owner){
        String status = "";
        try{
            RedisConnection<String, String> connection = redisClient.connect();
            String id = noseRecord.getNoseID();

            String session_key = this.getNoseReadingCollectionKey(owner, id, noseRecord.getSession());

            Gson gson = new GsonBuilder().create();
            String json_session = gson.toJson(noseRecord);

            Map<String, String> nose_record = new HashMap<>();
            nose_record.put(noseRecord.getDate(), json_session);

            status = connection.hmset(session_key, nose_record);

            connection.close();

        }catch(NullPointerException e){
            System.out.println(e.getMessage() + 101 + " redissink.java");
        }

        return status;
    }
    public String sinkNoseRecord(List<NoseRecord> noseRecords, String owner){
        String status = "";
        try{
            RedisConnection<String, String> connection = redisClient.connect();
            if(noseRecords.isEmpty()) {
                status = "empty list";
                return status;
            }
            NoseRecord zeroth = noseRecords.get(0);
            String session_key = getNoseReadingCollectionKey(owner, zeroth.getNoseID(), zeroth.getSession());
            Gson gson = new Gson();

            noseRecords.forEach(noseRecord -> {
                String json_session = gson.toJson(noseRecord);
                Map<String, String> nose_record = new HashMap<>();
                nose_record.put(noseRecord.getDate(), json_session);
                connection.hmset(session_key, nose_record);
            });

            connection.close();
            status = "ok";

        }catch(NullPointerException e){
            System.out.println(e.getMessage() + 101 + " redissink.java");
        }

        return status;
    }
    public String getNoses(String owner, boolean csv){
        RedisConnection<String, String> connection = redisClient.connect();
        String set_key = this.getNoseCollectionKey(owner);

        Map<String, String> set = connection.hgetall(set_key);
        connection.close();

        Gson gson = new GsonBuilder().create();
        List<Map<String,String>> noses = new ArrayList<>();
        Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();

        for (String s: set.keySet()) {
            Map<String, String> nose = gson.fromJson(set.get(s), typeOfHashMap);
            noses.add(nose);
        }

        return gson.toJson(noses); //new StreamJSON(set).getJson();
    }
    public String getSessionsForNose(String owner, String noseID, boolean csv){
        String session_key = this.getSessionsCollectionKey(owner, noseID);
        RedisConnection<String, String> connection = redisClient.connect();

        Map<String, String> set = connection.hgetall(session_key);
        connection.close();

        Gson gson = new GsonBuilder().create();
        List<Map<String,String>> sessions = new ArrayList<>();
        Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();

        for (String s: set.keySet()) {
            Map<String, String> session = gson.fromJson(set.get(s), typeOfHashMap);
            sessions.add(session);
        }
        return gson.toJson(sessions);
    }
    public String getRecordsForNoseSession(String owner, String noseID, String sessionID, boolean csv){

        String key = getNoseReadingCollectionKey(owner, noseID,sessionID);

        RedisConnection<String, String> connection = redisClient.connect();

        Map<String, String> set = connection.hgetall(key);
        connection.close();

        Gson gson = new GsonBuilder().create();
        List<NoseRecord> noses_records = new ArrayList<>();

        for (String s: set.keySet()) {
            NoseRecord record = gson.fromJson(set.get(s), NoseRecord.class);
            noses_records.add(record);
        }
        return gson.toJson(noses_records);
    }

    public void close(){
        redisClient.shutdown();
    }
}
