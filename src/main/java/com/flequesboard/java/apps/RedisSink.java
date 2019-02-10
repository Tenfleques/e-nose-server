package com.flequesboard.java.apps;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import org.apache.kafka.streams.KeyValue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


public class RedisSink {
    RedisClient redisClient;
    RedisSink(String redishost, int redisport){
        try {
            this.redisClient = new RedisClient(
                    RedisURI.create("redis://"+redishost+":"+redisport));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sinkNose(KeyValue<String,String> noseRecord){
        RedisConnection<String, String> connection = redisClient.connect();
        connection.sadd(noseRecord.key,noseRecord.value);
        connection.close();
    }
    public void sinkSession(KeyValue<String,String> noseSessionRecord){
        RedisConnection<String, String> connection = redisClient.connect();
        connection.sadd(noseSessionRecord.key,noseSessionRecord.value);
        connection.close();
    }
    public void sinkKey(String key){
        RedisConnection<String, String> connection = redisClient.connect();
        connection.sadd(AdministrativeStores.ENOSE_IDS.getValue(),key);
        connection.close();
    }
    public void sinkSessionKeys(String noseID, String session){
        RedisConnection<String, String> connection = redisClient.connect();
        connection.sadd(AdministrativeStores.ENOSE_SESIONS.getValue() + noseID,session);
        connection.close();
    }
    public String getNoseKeys(){
        RedisConnection<String, String> connection = redisClient.connect();
        Set<String> set = connection.smembers(AdministrativeStores.ENOSE_IDS.getValue());
        connection.close();
        return new StreamJSON(set, 1).getJson();
    }
    public  String getSessionsForNose(String noseId){
        RedisConnection<String, String> connection = redisClient.connect();
        Set<String> set = connection.smembers(AdministrativeStores.ENOSE_SESIONS.getValue() + noseId);
        connection.close();
        return new StreamJSON(set,1).getJson();
    }
    public String getAllRecordsForNoseID(String noseID){
        RedisConnection<String, String> connection = redisClient.connect();
        Set<String> set = connection.smembers(noseID);
        connection.close();
        return new StreamJSON(set).getJson();
    }

    public String getSessionRecordsForNoseKey(String noseIdAndSession){
        RedisConnection<String, String> connection = redisClient.connect();
        Set<String> set = connection.smembers(noseIdAndSession);
        connection.close();
        return new StreamJSON(set).getJson();
    }

    public void close(){
        redisClient.shutdown();
    }
}
