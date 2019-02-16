package com.flequesboard.java.apps;

import java.util.*;
import java.util.stream.Collectors;

class StreamJSON {
    private StringBuilder json =  new StringBuilder();

    StreamJSON(Set<String> set, int flat){
        json.append( "[");
        int i = 0;
        for (String record : set){
            if(i!=0)
                json.append( ",");
            json.append("\"").append(record).append("\"");
            i++;
        }
        json.append( "]");
    }
    StreamJSON(List<String> ls){
        json.append( "[");
        int i = 0;
        for (String record : ls){
            if(i!=0)
                json.append( ",");
            json.append("\"").append(record).append("\"");
            i++;
        }
        json.append( "]");
    }
    StreamJSON(Map<String,String> hashMap){
        Map<String,List<String>> dateToSensors = new HashMap<>();

        for (String s: hashMap.keySet()){
            String[] ds = s.split(AdministrativeStores.KEY_SEP.getValue());
            String noseRead = "\"" + ds[1] + "\":"+ hashMap.get(s);

            List<String> valSet = new ArrayList<>();
            if(dateToSensors.containsKey(ds[0])){
                valSet = dateToSensors.get(ds[0]);
            }
            valSet.add(noseRead);
            dateToSensors.put(ds[0], valSet);
        }

        json.append("[").append(dateToSensors.entrySet().stream()
                .map(e -> {
                    StringBuilder sensors = new StringBuilder();
                    sensors.append("{\"date\":")
                            .append(e.getKey());

                    for(String s : e.getValue()){
                        sensors.append(",").append(s);
                    }

                    sensors.append("}");

                    return sensors.toString();
                }).collect(Collectors.joining(", "))).append("]");
    }

    String getJson(){
        return json.toString();
    }
}
