package com.flequesboard;
import org.apache.kafka.streams.KafkaStreams;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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


    /*
    *     #############################################################################
    *                       JSON Consumables
    *     #############################################################################
    * */

    /**
     * Get all noseIDs of ownerID
     * @param  ownerID the nose identity
     * @return A List representing all of the key-values for the date given
     */


    @GET()
    @Path("/noses/{ownerID}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllNoses(@PathParam("ownerID") final String ownerID) {
        return redisSink.getNoseKeys(ownerID, false);
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
     *                      CSV Consumables                                                 *
     * ######################################################################################
     * */
    /*
     * get the noses feeding this redis deployment
     * */

    /**
     * Get all noses of ownerID
     * @param  ownerID the nose identity
     * @return A List representing all of the key-values for the date given
     */
    @GET()
    @Path("/csv/noses/{ownerID}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllNosesCSV(@PathParam("ownerID") final String ownerID) {
        return redisSink.getNoseKeys(ownerID, true);
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

    /*@GET()
    @Path("/saveRecord")
    @Produces(MediaType.APPLICATION_JSON)
    public String saveRecord() {
        System.out.println(this.stream.state());
        //stream.
        return "[]";
    }*/


    @GET()
    @Path("/getState")
    @Produces(MediaType.APPLICATION_JSON)
    public String getState() {
        return "{\"state\" : \"" +this.stream.state()+ "\" }";
    }

    /**
     * Start an embedded FlequesJetty Server on the given port
     * @param port    port to run the Server on
     * @throws Exception exceptions include TO_LIST
     */
    void start(final int port) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(new SaveNose(this.redisSink, this.organisation)), "/register/*");

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
