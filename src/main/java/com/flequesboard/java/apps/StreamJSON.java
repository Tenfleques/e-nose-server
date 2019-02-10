package com.flequesboard.java.apps;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;

import java.util.*;

public class StreamJSON {
    StringBuilder json =  new StringBuilder();
    StreamJSON(KeyValueIterator<String,String> streamData){
        json.append("[");
        Integer i = 0;
        while(streamData.hasNext()){
            KeyValue<String, String> record = streamData.next();
            if(i!=0)
                json.append(",");
            json.append( "{");
            json.append( "\"key\":\""+record.key + "\",\"value\":" + "\""+ record.value+"\"");
            json.append( "}");
            i++;
        }
        json.append( "]");
    }
    StreamJSON(Iterator<Map.Entry<String,Long>> map){
        json.append( "[");
        Integer i = 0;
        while(map.hasNext()){
            Map.Entry<String, Long> record = map.next();
            if(i!=0)
                json.append( ",");
            json.append( "{");
            json.append( "\"key\":\""+record.getKey() + "\",\"value\":" + "\""+ record.getValue()+"\"");
            json.append( "}");
            i++;
        }
        json.append( "]");
    }
    //[1513038733000,1492280283000, 1, [2, 0, 2, 6, 8, 3, 7, 4]] all
    //"1512597672000, 0, [1, 2, 6, 6, 7, 8, 3, 9] session
    StreamJSON(Set<String> set){
        json.append("[");
        Integer i = 0;
        for (String record : set){
            if(i!=0)
                json.append( ",");
            String parsed = record.replace('[','-').replace(']','-');//.substring(0,record.indexOf(']'));
            List<String> metaAndSensors = Arrays.asList(parsed.split("-"));
            List<String> meta = Arrays.asList(metaAndSensors.get(0).split(","));
            json.append( "{");
            json.append( "\"sensors\": [" + metaAndSensors.get(1)  + "],");
            json.append( "\"date\":\"" + meta.get(0) + "\",");
            json.append( "\"flag\":\"" + meta.get(2) + "\"");
            if(meta.toArray().length != 1) { //all
                json.append( ",\"session\":\"" + meta.get(1) + "\"");
            }
            json.append( "}");
            i++;
        }
        json.append( "]");
    }
    StreamJSON(Set<String> set, int flat){
        json.append( "[");
        Integer i = 0;
        for (String record : set){
            if(i!=0)
                json.append( ",");
            json.append("\"" +record + "\"");
            i++;
        }
        json.append( "]");
    }
    StreamJSON(List<String> ls){
        json.append( "[");
        Integer i = 0;
        for (String record : ls){
            if(i!=0)
                json.append( ",");
            json.append( "\""+ record+"\"");
            i++;
        }
        json.append( "]");
    }
    public String getJson(){
        return json.toString();
    }
}
