package com.flequesboard;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

class StreamCSV {
    private StringBuilder csv =  new StringBuilder();
    private String eol = System.getProperty("line.separator");

    StreamCSV(Set<String> set){
        int i = 0;
        for (String record : set){
            if(i!=0)
                csv.append( eol);
            csv.append(record);
            i++;
        }
    }

    StreamCSV(Map<String,String> hashMap){
        Map<String, Map<String, String>> x = new HashMap<>();
        Set<String> headers = new TreeSet<>();

        for (Map.Entry<String, String> e :  hashMap.entrySet()){
            String[] key = e.getKey().split(AdministrativeStores.KEY_SEP.getValue());

            headers.add(key[1]);

            Map<String, String> tmp = new HashMap<>();
            if(x.containsKey(key[0])) {
                tmp = x.get(key[0]);
            }
            tmp.put(key[1], e.getValue());
            tmp.put("date", key[0]);

            x.put(key[0], tmp);
        }
        if(headers.isEmpty())
            return;

        csv.append("date,timestamp,")
                .append(String.join(", ", headers))
                .append(eol);
        for(Map<String, String> e : x.values()){
            csv.append(Instant.ofEpochMilli(Long.parseLong(e.get("date"))))
                    .append(",")
                    .append(e.get("date"));

            for(String s: headers){
                csv.append(",").append(e.getOrDefault(s, ""));
            }
            csv.append(eol);
        }
    }

    String getCSV(){
        return csv.toString();
    }
}
