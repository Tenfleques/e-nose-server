package com.flequesboard;
import org.apache.kafka.common.PartitionInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Application {
    public static void main(String[] args) {
        String usage = "you must supply the config.json file with this structure"
                +"\n{"
                + "\n\t \"brokers\" : [\"required: the clusters address e.g localhost:9092\"],"
                + "\n\t \"topic\" : \"required: the Kafka topic feeding the application\","
                + "\n\t \"restUrl\" : \"required: the url for the REST endpoint e.g localhost\","
                + "\n\t \"restPort\" : \"required: the port for the REST endpoint e.g 7070\""
                + "\n}\n";
        String filename;

        if(args.length != 1) {
            System.out.print(usage);
            System.out.println("Using the default setup in resources/config.json");
            filename = "resources/config.json";
        }else {
            filename = args[0];
            System.out.println("Using the setup in ");
            System.out.print(filename);
        }

        final int ENDPOINT_PORT, REDIS_PORT;
        StringBuilder BROKERS = new StringBuilder();
        final String TOPIC, ENDPOINT, REDIS_URL;

        try {
            File configFile = new File(filename);
            String filePath = configFile.getAbsolutePath();
            FileReader file = new FileReader(filePath);

            JSONParser parser = new JSONParser();
            JSONObject config = (JSONObject) parser.parse(file);
            if(!config.containsKey("brokers")
                    || !config.containsKey("topic")
                    || !config.containsKey("restUrl")
                    || !config.containsKey("restPort")
                    || !config.containsKey("redisUrl")
                    || !config.containsKey("redisPort")){
                throw new KeyNotFoundException("error in config.json, missing keys.");
            }
            TOPIC = config.get("topic").toString();
            ENDPOINT = config.get("restUrl").toString();
            ENDPOINT_PORT = Integer.parseInt(config.get("restPort").toString());

            REDIS_URL = config.get("redisUrl").toString();
            REDIS_PORT = Integer.parseInt(config.get("redisPort").toString());

            for (Object broker: (JSONArray)config.get("brokers") ) {
                BROKERS.append((BROKERS.length() == 0) ? "" : ",");
                BROKERS.append(broker);
            }
            int numTries = 10, allTries = 10;
            while (numTries > 0) {
                try {
                    Map<String, List<PartitionInfo>> x = ReadKafka.readKafka(BROKERS.toString(), TOPIC);
                    //if(x.size() > 0 ) {
                    NoseServer noseServer = new NoseServer(BROKERS.toString(), TOPIC, ENDPOINT, ENDPOINT_PORT, REDIS_URL, REDIS_PORT);
                    //}
                } catch (Exception e ) {
                    if (numTries == 0) {
                        System.out.print(e.getMessage());
                        try {
                            throw e;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }else{
                        System.out.println("an error occurred, reattempting " + (allTries - numTries));
                    }
                }
                numTries--;
            }

        }catch (FileNotFoundException e){
            System.out.print("the config.json file was not found, verify if you have supplied the correct path to the" +
                    " file");
        }catch (IOException e){
            System.out.print("failed to open the file supplied. check to see if you have the right permissions or " +
                    "that the file is not locked by another process" +
                    " ");
        }catch(ParseException e) {
            System.out.print("Failed to parse the config.json supplied, check if you have supplied a correct path to " +
                    "the config.json file and that it is in the exact format!");
            System.out.print(usage);
        }catch (KeyNotFoundException e){
            System.out.print(e + usage);
        }
    }
}
