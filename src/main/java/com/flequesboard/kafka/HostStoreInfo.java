package com.flequesboard.kafka;
import com.flequesboard.serviceAPI.RPCService;
import com.flequesboard.redis.AdministrativeStores;

import java.util.Objects;
import java.util.Set;
/**
 * A simple bean that can be JSON serialized via Jersey. Represents a KafkaStreams instance
 * that has a set of state stores. See {@link RPCService} for how it is used.
 *
 * We use this JavaBean based approach as it fits nicely with JSON serialization provided by
 * jax-rs/jersey
 */
public class HostStoreInfo {

    private String host;
    private int port;
    private Set<String> storeNames;

    public HostStoreInfo(){}

    public HostStoreInfo(final String host, final int port, final Set<String> storeNames) {
        this.host = host;
        this.port = port;
        this.storeNames = storeNames;
    }
    @Override
    public String toString() {
        String storenames = "[";
        int i = 0;
        for(String s : storeNames) {
            if(AdministrativeStores.LIVE_DATES.getValue().equals(s))//skip administrative stores
                continue;
            if (i++ != 0)
                storenames += ",";
            storenames += "\"" + s + "\"";
        }
        storenames += "]";
        return "{ "
                    +"\"host\": \"" + host + "\""
                    +",\"port\": \"" + port + "\""
                    +",\"storeNames\": " + storenames
                + "}";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HostStoreInfo that = (HostStoreInfo) o;
        return port == that.port &&
                Objects.equals(host, that.host) &&
                Objects.equals(storeNames, that.storeNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, storeNames);
    }
}
