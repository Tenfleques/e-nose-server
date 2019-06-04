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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoseRecordsAPI extends HttpServlet {
    private RedisSink redisSink;
    private String organisation;
    public NoseRecordsAPI(RedisSink redisSink, String organisation){
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
                String [] keys = {"sessionID",
                        "date",
                        "noseID",
                        "sensors"};

                NoseRecord noseRecord;

                while ((line = reader.readLine()) != null)
                    jb.append(line);


                if(jb.toString().isEmpty()){
                    Map<String, String> kv = new HashMap<>();
                    for (String i: keys) {
                        kv.put(i, request.getParameter(i));
                    }
                    noseRecord = new NoseRecord(kv);
                }else{
                    noseRecord = gson.fromJson(jb.toString(), NoseRecord.class);
                }

                try{
                    org = request.getHeader("org");
                }catch (NullPointerException e) {
                    org = this.organisation;
                }
                if(!noseRecord.isEmpty()){
                    res = this.redisSink.sinkNoseRecord(noseRecord, org);
                }else {
                    res = "missing key";
                }
                output = "{\"status\":\"" + res +"\"}";


            } catch (Exception e) {
                System.out.println("error " + e);
            }

            response.setStatus(HttpServletResponse.SC_OK);


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(output);
            response.getWriter().close();
        }
    }
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String output = "{\"status\": \"fail\"}", res;
        try {
            String org;
            Gson gson = new Gson();
            StringBuffer jb = new StringBuffer();
            String line;
            try {
                List<NoseRecord> noseRecordList;

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);

                String raw_json;
                if(jb.toString().isEmpty()){
                    raw_json = request.getParameter("matrix");
                }else{
                    raw_json = jb.toString();
                }

                noseRecordList = gson.fromJson(raw_json, new TypeToken<List<NoseRecord>>(){}.getType());

                try{
                    org = request.getHeader("org");
                }catch (NullPointerException e) {
                    org = this.organisation;
                }

                if(!noseRecordList.isEmpty()) {
                    res = this.redisSink.sinkNoseRecord(noseRecordList, org);
                }else {
                    res = "missing key";
                }
                output = "{\"status\":\"" + res + "\"}";

            } catch (Exception e) {
                System.out.println("error " + e);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
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
            String org, noseID, sessionID;
            boolean csv;
            try{
                Enumeration<String> param_names = request.getParameterNames();
                Map<String,String> params = new HashMap<>();

                while(param_names.hasMoreElements()){
                    String param_name = param_names.nextElement();
                    params.put(param_name,request.getParameter(param_name));
                }

                org = params.getOrDefault("org", this.organisation);
                noseID = params.getOrDefault("noseID", "unknown");
                sessionID = params.getOrDefault("sessionID", "unknown");

                csv = Boolean.parseBoolean(params.getOrDefault("csv", "false"));

            }catch (NullPointerException e){
                org = this.organisation;
                noseID = "unknown";
                sessionID = "unknown";
                csv = false;
                System.out.println("using default organisation to get noses");
            }

            output = this.redisSink.getRecordsForNoseSession(org, noseID, sessionID, csv);
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