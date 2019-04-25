package com.flequesboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;

import java.lang.reflect.Type;
import java.util.*;


class RedisSink {
    private RedisClient redisClient;

    RedisSink(String redishost, int redisport) {
        this.redisClient = new RedisClient(
                RedisURI.create("redis://"+redishost+":"+redisport));
    }


    String sinkNose(String noseID, String owner){
        Map<String, String> nose = new HashMap<>();
        nose.put("noseID", noseID.hashCode() + "");
        nose.put("title", noseID);
        nose.put("short_description", "");
        nose.put("date_reg", new Date().getTime()+"");
        nose.put("org", owner);

        return this.sinkNose(nose, owner);
    }
    String sinkNose(Map<String, String> nose, String owner){
        String status;
        String nose_id = nose.getOrDefault("noseID", nose.hashCode() + "");

        RedisConnection<String, String> connection = redisClient.connect();

        String set_key = getNoseCollectionKey(owner);

        Gson gson = new GsonBuilder().create();
        String json_nose = gson.toJson(nose);

        Map<String, String> nose_entry = new HashMap<>();
        nose_entry.put(nose_id, json_nose);

        status = connection.hmset(set_key,nose_entry );
        connection.close();
        return status;
    }

    String sinkNoseRecord(NoseRecord noseRecord){
        Map<String, String> redisNoseRecords = noseRecord.getRedisReadyRecord();
        RedisConnection<String, String> connection = redisClient.connect();

        //save the nose-id => [session ]
        connection.sadd(AdministrativeStores.ENOSE_SESIONS.getValue() + noseRecord.getNoseID(), noseRecord.getSessionID());


        //save the nose + session  => [sensor -> val + date -> date]
        String sessionID = getSessionKey(noseRecord.getNoseID(),  noseRecord.getSessionID());

        String status = connection.hmset(sessionID, redisNoseRecords);
        connection.close();
        return status;
    }
    private String  getSessionKey(String noseID, String sessionID){
        return  AdministrativeStores.SESSION_RECORDS.getValue() +  noseID + sessionID;
    }
    private String getNoseCollectionKey(String ownerID){

        return AdministrativeStores.ENOSE_IDS.getValue() + ownerID;
    }

    String getNoseKeys(String owner, boolean csv){
        RedisConnection<String, String> connection = redisClient.connect();
        Map<String, String> set = connection.hgetall(getNoseCollectionKey(owner));
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


    String getSessionsForNose(String noseID, boolean csv){
        RedisConnection<String, String> connection = redisClient.connect();
        Set<String> set = connection.smembers(AdministrativeStores.ENOSE_SESIONS.getValue() + noseID);

        connection.close();
        if (csv)
            return "sessionID\n" + new StreamCSV(set).getCSV();

        return new StreamJSON(set).getJson();
    }
    String getSessionRecordsForNoseSession(String noseID, String sessionID, boolean csv){
        RedisConnection<String, String> connection = redisClient.connect();

        String session = getSessionKey(noseID, sessionID);

        Map<String, String> set = connection.hgetall(session);

        connection.close();

        if (csv)
            return new StreamCSV(set).getCSV();

        return new StreamJSON(set).getJson();
    }
    void close(){
        redisClient.shutdown();
    }


}
