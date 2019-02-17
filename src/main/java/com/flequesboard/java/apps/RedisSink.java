package com.flequesboard.java.apps;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;

import java.util.Map;
import java.util.Set;


class RedisSink {
    private RedisClient redisClient;
    RedisSink(String redishost, int redisport){
        try {
            this.redisClient = new RedisClient(
                    RedisURI.create("redis://"+redishost+":"+redisport));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void sinkNose(String noseID){
        RedisConnection<String, String> connection = redisClient.connect();

        //save the reg_noses => [noseID ]
        connection.sadd(AdministrativeStores.ENOSE_IDS.getValue(), noseID);

        connection.close();
    }

    void sinkNoseRecord(NoseRecord noseRecord){
        Map<String, String> redisNoseRecords = noseRecord.getRedisReadyRecord();
        RedisConnection<String, String> connection = redisClient.connect();

        //save the nose-id => [session ]
        connection.sadd(AdministrativeStores.ENOSE_SESIONS.getValue() + noseRecord.getNoseID(), noseRecord.getSessionID());


        //save the nose + session  => [sensor -> val + date -> date]
        String sessionID = getSessionKey(noseRecord.getNoseID(),  noseRecord.getSessionID());

        connection.hmset(sessionID, redisNoseRecords);

        connection.close();
    }
    private String  getSessionKey(String noseID, String sessionID){
        return  AdministrativeStores.SESSION_RECORDS.getValue() +  noseID + sessionID;
    }

    String getNoseKeys(boolean csv){
        RedisConnection<String, String> connection = redisClient.connect();
        Set<String> set = connection.smembers(AdministrativeStores.ENOSE_IDS.getValue());
        connection.close();

        if (csv)
            return "noseID\n" + new StreamCSV(set).getCSV();

        return new StreamJSON(set).getJson();
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
