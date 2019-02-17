package com.flequesboard.java.apps;
import org.apache.kafka.streams.KafkaStreams;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 *  Rest endpoint
 *  available endpoints :
 *       url:port/instances
 * 	     url:port/noses
 * 	     url:port/sessions/{nose_id}
 * 	     url:port/session/{nose_id}/{session_id}
 *
 */
@Path("/")
public class RPCService {
    private final MetadataService metadataService;
    private Server jettyServer;
    private final RedisSink redisSink;

    RPCService(final KafkaStreams streams, final RedisSink redisSink) {
        KafkaStreams streams1 = streams;
        this.metadataService = new MetadataService(streams);
        this.redisSink = redisSink;
    }


    /*TODO
     *   let app get nose and server params from post
     * */

    @POST()
    @Path("/params")
    @Produces(MediaType.APPLICATION_JSON)
    public String setStreamParams(@PathParam("noseId") final String noseId) {

        return "[]";
    }

    /**
     * send data
     * get nose read and commit to redis
     * @param noseID the nose identity
     * @param sessionID the session identity to save
     * @param details additional details associated with dump
     * @param readings the readings array of maps
     */
    @POST()
    @Path("/write")
    @Produces(MediaType.APPLICATION_JSON)
    public String setRecordsForNose(
            @PathParam("noseID") final String noseID,
            @PathParam("sessionID") final String sessionID,
            @PathParam("details") final String details,
            @PathParam("readings") final String readings) {

        return "[]";
    }


    @GET()
    @Path("/instances")
    @Produces(MediaType.APPLICATION_JSON)
    public String streamsMetadata() {
        return metadataService.streamsMetadata();
    }

    /*
    * get the noses feeding this redis deployment
    * */

    @GET()
    @Path("/noses")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllNoses() {
        return redisSink.getNoseKeys(false);
    }



    /**
     * Get all sessions saved for a given nose ID
     * @param  noseID the nose identity
     * @return A List representing all of the key-values for the date given
     */
    @GET()
    @Path("/sessions/{noseID}/")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSessionsForNose(@PathParam("noseID") final String noseID) {
        try {
            return redisSink.getSessionsForNose(noseID, false);
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    /**
     * Get models saved for noses
     * @return A List representing saved ML models relative to the model.
     */
    @GET()
    @Path("/models")
    @Produces(MediaType.APPLICATION_JSON)
    public String getModels() {
        try {
            return "[]";
        }catch (Exception e){
            return "[]";
        }
    }

    /**
     * Get all for a given nose ID and session
     * @param  noseID the nose identity
     * @param session  to query
     * @return A List representing all of the key-values for the nose and session given
     */
    @GET()
    @Path("/session/{noseID}/{session}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSessionRecordsForNose(@PathParam("noseID") final String noseID,
                                           @PathParam("session") final String session) {
        try {
            return redisSink.getSessionRecordsForNoseSession(noseID,session, false);
        }catch (Exception e){
            return "[]";
        }
    }


    /*
     * ######################################################################################
     *                      FOR CSV DATA                                                    *
     * ######################################################################################
     * */
    /*
     * get the noses feeding this redis deployment
     * */

    @GET()
    @Path("/csv/noses")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllNosesCSV() {
        return redisSink.getNoseKeys(true);
    }


    /**
     * Get all sessions saved for a given nose ID
     * @param  noseID the nose identity
     * @return A List representing all of the key-values for the date given
     */
    @GET()
    @Path("/csv/sessions/{noseID}/")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSessionsForNoseCSV(@PathParam("noseID") final String noseID) {
        try {
            return redisSink.getSessionsForNose(noseID, true);
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    /**
     * Get models saved for noses
     * @return A List representing saved ML models relative to the model.
     */
    @GET()
    @Path("/csv/models")
    @Produces(MediaType.TEXT_PLAIN)
    public String getModelsCSV() {
        try {
            return "[]";
        }catch (Exception e){
            return "[]";
        }
    }

    /**
     * Get all for a given nose ID and session
     * @param  noseID the nose identity
     * @param session  to query
     * @return A List representing all of the key-values for the nose and session given
     */
    @GET()
    @Path("/csv/session/{noseID}/{session}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSessionRecordsForNoseCSV(@PathParam("noseID") final String noseID,
                                           @PathParam("session") final String session) {
        try {
            return redisSink.getSessionRecordsForNoseSession(noseID,session, true);
        }catch (Exception e){
            return "[]";
        }
    }


    /**
     * Start an embedded Jetty Server on the given port
     * @param port    port to run the Server on
     * @throws Exception exceptions include TO_LIST
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
