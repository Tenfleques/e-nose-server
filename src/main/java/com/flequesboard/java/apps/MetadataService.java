package com.flequesboard.java.apps;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.StreamsMetadata;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Looks up StreamsMetadata from KafkaStreams and converts the results to JSON strings.
 */
public class MetadataService {
    private final KafkaStreams streams;
    MetadataService(final KafkaStreams st) {
        this.streams = st;
    }
    /**
     * Get the metadata for all of the instances of this Kafka Streams application
     * @return List of {@link HostStoreInfo}
     */
    String streamsMetadata() {
        // Get metadata for all of the instances of this Kafka Streams application
        final Collection<StreamsMetadata> metadata = streams.allMetadata();
        return  jsonMetadata(metadata);
    }

    /**
     * Get the metadata for all instances of this Kafka Streams application that currently
     * has the provided store.
     * @param store   The store to locate
     * @return  List of {@link HostStoreInfo}
     */
    public String streamsMetadataForStore(final  String store) {
        // Get metadata for all of the instances of this Kafka Streams application hosting the store
        final Collection<StreamsMetadata> metadata = streams.allMetadataForStore(store);
        return jsonMetadata(metadata);
    }

    /**
     * Find the metadata for the instance of this Kafka Streams Application that has the given
     * store and would have the given key if it exists.
     * @param store   Store to find
     * @param key     The key to find
     * @return {@link HostStoreInfo}
     */
    public String streamsMetadataForStoreAndKey(final String store,
                                                           final String key,
                                                           final Serializer<String> serializer) {
        final StreamsMetadata metadata = streams.metadataForKey(store, key, serializer);
        if (metadata == null) {
            throw new NotFoundException();
        }

        return new HostStoreInfo(metadata.host(),
                metadata.port(),
                metadata.stateStoreNames()).toString();
    }

    private String jsonMetadata(final Collection<StreamsMetadata> metadatas) {
        String json = "";
        int i = 0;
        for (StreamsMetadata metadata : new ArrayList<>(metadatas)) {
            if (i != 0)
                json += ",";
            json += new HostStoreInfo(metadata.host(),
                    metadata.port(),
                    metadata.stateStoreNames()).toString();
        }
        return  json;
    }
}
