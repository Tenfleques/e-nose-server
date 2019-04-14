package com.flequesboard;

import org.apache.kafka.clients.consumer.ConsumerConfig;
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

/*
* streams kafka records of a given nose id, commit well structured form into redis.
* */

class StreamKafka {
    private final KafkaStreams stream;
    StreamKafka(String brokers, String topic, RedisSink redisSink) throws Exception {

        final Properties props;
        props  = new Properties();
        StreamsBuilder builder = new StreamsBuilder();

        final String APP_ID = "stream-nose-records";
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        final File storeFile = Files.createTempDirectory(new File("/tmp").toPath(), APP_ID).toFile();
        props.put(StreamsConfig.STATE_DIR_CONFIG, storeFile.getPath());


        KStream<String, String> source = builder.stream(topic);

        //sink records to of this nose's session
        //KStream<String,NoseRecord> noseRecords =
        source.mapValues(a -> {
            NoseRecord noseRecord = new NoseRecord(a);
            redisSink.sinkNoseRecord(noseRecord);
            return  noseRecord;
        });

        //can use the noseRecords as wished here. e.g feed an AI

        final Topology topology = builder.build();
        stream = new KafkaStreams(topology, props);
    }
    void startStream(){
        this.stream.cleanUp();
        this.stream.start();
    }
    KafkaStreams getStream(){
        return stream;
    }


}
