package com.flequesboard.serviceAPI;
import com.flequesboard.handlersAPI.NoseDeviceAPI;
import com.flequesboard.handlersAPI.NoseRecordsAPI;
import com.flequesboard.handlersAPI.NoseSessionsAPI;
import com.flequesboard.redis.RedisSink;
import org.apache.kafka.streams.KafkaStreams;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.*;

@Path("/")
public class RPCService {
    //private MetadataService metadataService;
    private Server jettyServer;
    private final RedisSink redisSink;
    private String organisation;
    KafkaStreams stream;

    RPCService(RedisSink redisSink, String org, KafkaStreams stream) {
        this.redisSink = redisSink;
        this.organisation = org;
        this.stream = stream;
    }

    /**
     * Start an embedded FlequesJetty Server on the given port
     * @param port    port to run the Server on
     * @throws Exception exceptions include TO_LIST
     */
    void start(final int port) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new NoseDeviceAPI(this.redisSink, this.organisation)), "/noses");
        context.addServlet(new ServletHolder(new NoseSessionsAPI(this.redisSink, this.organisation)), "/sessions");
        context.addServlet(new ServletHolder(new NoseRecordsAPI(this.redisSink, this.organisation)), "/records");


        jettyServer = new Server(port);
        jettyServer.setHandler(context);

        ResourceConfig rc = new ResourceConfig();
        rc.register(this);

        ServletContainer sc = new ServletContainer(rc);
        ServletHolder holder = new ServletHolder(sc);
        context.addServlet(holder, "/*");

        jettyServer.start();
    }

    /**
     * Stop the FlequesJetty Server
     * @throws Exception
     */
    void stop() throws Exception {
        if (jettyServer != null) {
            jettyServer.stop();
        }
    }

}
