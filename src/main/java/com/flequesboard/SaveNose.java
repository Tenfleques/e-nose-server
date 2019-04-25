package com.flequesboard;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
            String org = "";//request.getHeader("org");
            if(!org.isEmpty()){ //can save nose on behalf of an organisation
                this.organisation = org;
            }

            String res = "failed max";
            if(noseID.isEmpty()){
                output = "{\"status\":" + res +"}";
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }

            Map<String, String> nose = new HashMap<>();
            Enumeration<String> keys = request.getParameterNames();

            while(keys.hasMoreElements()){
                String key = keys.nextElement();
                nose.put(key, request.getParameter(key));
            }

            if(nose.keySet().isEmpty()){ //sink using provided ID only
                res = this.redisSink.sinkNose(noseID,this.organisation);
            }else{
                //much appreciated sink point
                res = this.redisSink.sinkNose(nose, this.organisation);
            }

            output = "{\"status\":" + res +"}";

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println(ex);
        } finally {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(output);
            response.getWriter().close();
        }
    }

}