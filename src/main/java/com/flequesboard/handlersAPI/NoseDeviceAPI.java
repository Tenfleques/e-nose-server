package com.flequesboard.handlersAPI;

import com.flequesboard.nose.NoseRecord;
import com.flequesboard.redis.RedisSink;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

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
            String org, res;
            StringBuffer jb = new StringBuffer();
            String line;
            try {
                BufferedReader reader = request.getReader();
                Gson gson = new Gson();
                String [] keys = {"short_description",
                        "date_reg",
                        "noseID",
                        "title"};

                Map<String, String> nose = new HashMap<>();

                while ((line = reader.readLine()) != null)
                    jb.append(line);

                if(jb.toString().isEmpty()){
                    for (String i: keys) {
                        nose.put(i,request.getParameter(i));
                    }
                }else{
                    nose = gson.fromJson(jb.toString(), new TypeToken<Map<String, String>>(){}.getType());
                }

                try{
                    org = request.getHeader("org");
                }catch (NullPointerException e) {
                    org = this.organisation;
                }
                if(nose.keySet().containsAll(Arrays.asList(keys))){
                    res = this.redisSink.sinkNose(nose, org);
                }else {
                    res = "missing key";
                }
                output = "{\"status\":\"" + res +"\"}";


            } catch (Exception e) {
                System.out.println("error " + e);
            }

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