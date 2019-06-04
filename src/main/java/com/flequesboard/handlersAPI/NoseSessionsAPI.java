package com.flequesboard.handlersAPI;

import com.flequesboard.redis.RedisSink;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
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
        try {
            String org, res;
            StringBuffer jb = new StringBuffer();
            String line;
            try {
                BufferedReader reader = request.getReader();
                Gson gson = new Gson();
                String [] keys = {"short_description",
                        "sessionTitle",
                        "noseID",
                        "sessionID"};

                Map<String, String> session = new HashMap<>();

                while ((line = reader.readLine()) != null)
                    jb.append(line);

                if(jb.toString().isEmpty()){
                    for (String i: keys) {
                        session.put(i,request.getParameter(i));
                    }
                }else{
                    session = gson.fromJson(jb.toString(), new TypeToken<Map<String, String>>(){}.getType());
                }

                try{
                    org = request.getHeader("org");
                }catch (NullPointerException e) {
                    org = this.organisation;
                }
                if(session.keySet().containsAll(Arrays.asList(keys))){
                    res = this.redisSink.sinkNoseSession(session, org);
                }else {
                    res = "missing key";
                }
                output = "{\"status\":\"" + res +"\"}";

            } catch (Exception e) {
                System.out.println("error " + e);
            }

            response.setStatus(HttpServletResponse.SC_OK);


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