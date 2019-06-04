package com.flequesboard.handlersAPI;

import com.flequesboard.redis.RedisSink;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class NoseSessionsAPI extends HttpServlet {
    private RedisSink redisSink;
    private String organisation;
    public NoseSessionsAPI(RedisSink redisSink, String organisation){
        this.redisSink = redisSink;
        this.organisation = organisation;
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String output = "{\"status\": \"fail\"}";
        Map<String, String> sessionRecord = new HashMap<>();
        String org = this.organisation;
        try {

            Gson gson = new Gson();

            StringBuilder jb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);

                if(jb.toString().isEmpty()){
                    Enumeration<String> keys = request.getParameterNames();
                    while(keys.hasMoreElements()){
                        String key = keys.nextElement();
                        sessionRecord.put(key, request.getParameter(key));
                    }
                }else{
                    sessionRecord = gson.fromJson(jb.toString(), Map.class);
                }

                try{
                    org = request.getHeader("org");
                }catch (NullPointerException e) {
                    org = this.organisation;
                }

            } catch (Exception e) {
                System.out.println("error " + e.getMessage());
            }
            String res = this.redisSink.sinkNoseSession(sessionRecord, org);
            output = "{\"status\":\"" + res +"\"}";


        } catch (Exception ex) {
            System.out.println("fatal " + ex.getMessage());
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
            String org, noseID = "unknown";
            boolean csv = false;
            try{
                org = request.getHeader("org");
            }catch (NullPointerException e){
                org = this.organisation;
                System.out.println("using default organisation to get sessions");
            }
            Enumeration<String> param_names = request.getParameterNames();

            while(param_names.hasMoreElements()){
                String param_name = param_names.nextElement();
                if(param_name.equals("csv"))
                    csv = Boolean.parseBoolean(request.getParameter("csv"));

                if(param_name.equals("noseID")){
                    noseID = request.getParameter(param_name);
                }
            }

            output = this.redisSink.getSessionsForNose(org, noseID, csv);
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