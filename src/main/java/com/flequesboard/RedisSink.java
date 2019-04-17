package com.flequesboard;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;

import java.util.Map;
import java.util.Set;


class RedisSink {
    private RedisClient redisClient;

    RedisSink(String redishost, int redisport) {
        this.redisClient = new RedisClient(
                RedisURI.create("redis://"+redishost+":"+redisport));
    }


    Long sinkNose(String noseID, String owner){
        RedisConnection<String, String> connection = redisClient.connect();

        //save the reg_noses => [noseID ]
        Long insert = connection.sadd(getNoseCollectionKey(owner), noseID);

        connection.close();
        return insert;
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
        Set<String> set = connection.smembers(getNoseCollectionKey(owner));
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
