package com.flequesboard.java.apps;
import org.apache.kafka.streams.KafkaStreams;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 *  Rest endpoint to broadcast the computed statistics in the application
 *  available endpoints :
 *      url:port/stats/{storeName}/all
 *      url:port/instances
 *
 */
@Path("/")
public class RPCService {
    private final KafkaStreams streams;
    private final MetadataService metadataService;
    private Server jettyServer;
    private final RedisSink redisSink;
    private List<String> archiveStores;

    RPCService(final KafkaStreams streams, final RedisSink redisSink) {
        this.streams = streams;
        this.metadataService = new MetadataService(streams);
        this.redisSink = redisSink;
    }
    public void setArchiveStores(List<String> archiveStores){
        this.archiveStores = archiveStores;
    }
    @GET()
    @Path("/records/stores")
    @Produces(MediaType.APPLICATION_JSON)
    public String getArchiveStores() {
        return new StreamJSON(this.archiveStores).getJson();
    }

    /**
     * Get all for a given nose ID and session
     * @param  noseId
     * @param session  to query
     * @return A List representing all of the key-values for the date given
     */
    @GET()
    @Path("/session/{noseId}/{session}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSessionRecordsForNose(@PathParam("noseId") final String noseId,
                                        @PathParam("session") final String session) {
        try {
            return redisSink.getSessionRecordsForNoseKey(new NoseRecord(noseId,session).getKey());
        }catch (Exception e){
            return "[]";
        }
    }
    /**
     * Get all sessions saved for a given nose ID
     * @param  noseId
     * @return A List representing all of the key-values for the date given
     */
    @GET()
    @Path("/sessions/{noseId}/")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSessionsForNose(@PathParam("noseId") final String noseId) {
        try {
            return redisSink.getSessionsForNose(noseId);
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    /**
     * Get all for a given nose ID
     * @param  noseId
     * @return A List representing all of the key-values for the noseId given
     */
    @GET()
    @Path("/records/{noseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRecordsForNose(@PathParam("noseId") final String noseId) {
        try {
            return redisSink.getAllRecordsForNoseID(noseId);
        }catch (Exception e){


            System.out.println(e.getMessage());
            return "[]";
        }
    }


    /**
     * send test data
     * get nose read and commit to kafka and redis
     * @param noseId
     * @param sessionID
     * @param timestamp
     * @param flag
     * @param nose
     */
    @GET()
    @Path("/write")
    @Produces(MediaType.APPLICATION_JSON)
    public String setRecordsForNose(@PathParam("noseId") final String noseId) {
        try {
            return redisSink.getAllRecordsForNoseID(noseId);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return "[]";
        }
    }

    @GET()
    @Path("/noses")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllNoses() {
        return redisSink.getNoseKeys();
    }

    @GET()
    @Path("/instances")
    @Produces(MediaType.APPLICATION_JSON)
    public String streamsMetadata() {
        return metadataService.streamsMetadata().toString();
    }

    /**
     * Start an embedded Jetty Server on the given port
     * @param port    port to run the Server on
     * @throws Exception
     */
    void start(final int port) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

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
     * Stop the Jetty Server
     * @throws Exception
     */
    void stop() throws Exception {
        if (jettyServer != null) {
            jettyServer.stop();
        }
    }

}
