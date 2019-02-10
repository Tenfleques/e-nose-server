package com.flequesboard.java.apps;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/*
* All the methods and fields are private save for the constructor so that all statistic calls are controlled and
* fixed to one for each instance of the class
*/

//nose_id,date,session_id,sensor_1,...,sensor_n
//stream and sink as:
/*
*nose_id:
*    -------(session_1:
*             -----------(date, sensor_1, sensor_2, ... , sensor_n)
*             )
*             .
*             .
*             .
*    -------(session_n:
*             -----------(date, sensor_1, sensor_2, ... , sensor_n)
*             )
*/

class StreamKafka {

    private RedisSink redisSink;
    private final KafkaStreams streams;

    StreamKafka(String brokers, String topic, String rpcEndpoint, Integer
            rpcPort, String redishost, int redisport) throws Exception {

        final Properties props;
        props  = new Properties();
        StreamsBuilder builder = new StreamsBuilder();
        this.redisSink = new RedisSink(redishost, redisport);


        final String APP_ID = "stream-nose-records";
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, rpcEndpoint + ":" + rpcPort);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        final File storeFile = Files.createTempDirectory(new File("/tmp").toPath(), APP_ID).toFile();
        props.put(StreamsConfig.STATE_DIR_CONFIG, storeFile.getPath());


        KStream<String, String> source = builder.stream(topic);
        //sink records to redis by noseID key
        sinkRecords(source);
        //expose the available REST endpoints
        StringBuilder info = new StringBuilder("\n" +
                "*available endpoints :\n");

        //info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append("/archive/stores\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append("/instances\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append("/noses\n");
        /*info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/live/"+sinkRecordsStore+"\n");*/
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/session/nose_id/session_id\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/records/nose_id\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/sessions/nose_id\n");

        final Topology topology = builder.build();
        streams = new KafkaStreams(topology, props);


        System.out.print(info);

        streams.start();
        final RPCService restService =  startRestProxy(streams,redisSink,rpcPort);
        //restService.setArchiveStores(historicalStores);
        final CountDownLatch latch = new CountDownLatch(1);
        try{
            latch.await();
        }catch (Throwable e){
            System.exit(1);
        }
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook"){
            @Override
            public void run(){
                streams.close();
                redisSink.close();
                try {
                    restService.stop();
                }catch (Exception e){
                    System.out.print(e.getMessage());
                }
                latch.countDown();
            }
        });
        System.exit(0);

    }
    private void sinkRecords(KStream<String, String> source){
        //final String store = new java.sql.Date(Instant.now().toEpochMilli()).toString();

        source.mapValues(NoseRecord::new)
              .map((key,noseRecord)-> {
                  redisSink.sinkSession(noseRecord.getSessionRecord());
                  redisSink.sinkNose(noseRecord.getRecord());
                  redisSink.sinkKey(noseRecord.getNoseID());
                  redisSink.sinkSessionKeys(noseRecord.getNoseID(), noseRecord.getSessionID().toString());
                  return noseRecord.getRecord();
              });

    }
    private static RPCService startRestProxy(final KafkaStreams streams, final RedisSink redisSink, final int port)
            throws Exception {
        final RPCService
                aggregateRestService = new RPCService(streams, redisSink);
        aggregateRestService.start(port);
        return aggregateRestService;
    }
}
