package com.flequesboard.handlersAPI;

import com.flequesboard.redis.RedisSink;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class NoseDeviceAPI extends HttpServlet {
    private RedisSink redisSink;
    private String organisation;
    public NoseDeviceAPI(RedisSink redisSink, String org){
        this.redisSink = redisSink;
        this.organisation = org;
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String output = "{\"status\": \"fail\"}";
        try {

            final String noseID = request.getParameter("noseID");
            String org = request.getHeader("org");
            if(!org.isEmpty()){ //can save nose on behalf of an organisation
                this.organisation = org;
            }

            String res = "failed max";
            if(noseID.isEmpty()){
                output = "{\"status\":\"" + res +"\"}";
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

            output = "{\"status\":\"" + res +"\"}";

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            System.out.println(ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(output);
            response.getWriter().close();
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String output = "{\"status\": \"fail\"}";
        try {
            String org = "";
            try{
                org = request.getHeader("org");
            }catch (NullPointerException e){
                org = this.organisation;
                System.out.println("using default organisation to get noses");
            }
            output = this.redisSink.getNoses(org, false);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            System.out.println(ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(output);
            response.getWriter().close();
        }
    }

}