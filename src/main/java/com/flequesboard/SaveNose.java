package com.flequesboard;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class SaveNose extends HttpServlet {
    private RedisSink redisSink;
    private String organisation;
    SaveNose(RedisSink redisSink, String org){
        this.redisSink = redisSink;
        this.organisation = org;
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String output = "{\"status\": \"fail\"}";
        try {

            final String noseID = request.getParameter("noseID");

            System.out.println(noseID);

            String res = this.redisSink.sinkNose(noseID,this.organisation) > 0 ? "success" : "exists";
            output = "{\"status\":" + res +"}";

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(output);
            response.getWriter().close();
        }
    }

}