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
* streams kafka records of a given nose id, commit well structured form into redis.
* */

class StreamKafka {

    private RedisSink redisSink;
    private final KafkaStreams streams;
    private final String topic;

    StreamKafka(String brokers, String topic, String rpcEndpoint, Integer
            rpcPort, String redishost, int redisport) throws Exception {
        this.topic = topic;

        final Properties props;
        props  = new Properties();
        StreamsBuilder builder = new StreamsBuilder();
        this.redisSink = new RedisSink(redishost, redisport);

        //sink the noseID
        redisSink.sinkNose(topic);

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

        //sink records to of this nose's session
        KStream<String,NoseRecord> noseRecords =  source.mapValues(a -> {
                                                    NoseRecord noseRecord = new NoseRecord(a,this.topic);
                                                    redisSink.sinkNoseRecord(noseRecord);
                                                    return  noseRecord;
                                                });
        //can use th nose records as wished here. e.g feed an AI
        //expose the available REST endpoints
        StringBuilder info = new StringBuilder("\n" +
                "*available endpoints :\n");

        //info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append("/instances\n");

        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append("/noses\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append("/csv/noses\n");

        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/sessions/{nose_id}\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/csv/sessions/{nose_id}\n");



        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/session/{nose_id}/{session_id}\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/csv/session/{nose_id}/{session_id}\n");

        final Topology topology = builder.build();
        streams = new KafkaStreams(topology, props);


        System.out.print(info);

        streams.start();
        final RPCService restService =  startRestProxy(streams,redisSink,rpcPort);

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
    private static RPCService startRestProxy(final KafkaStreams streams, final RedisSink redisSink, final int port)
            throws Exception {
        final RPCService
                aggregateRestService = new RPCService(streams, redisSink);
        aggregateRestService.start(port);
        return aggregateRestService;
    }
}
