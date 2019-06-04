package com.flequesboard.handlersAPI;

import com.flequesboard.nose.NoseRecord;
import com.flequesboard.redis.RedisSink;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

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
        String output = "{\"status\": \"fail\"}", res;
        try {
            Enumeration<String> param_names = request.getParameterNames();
            boolean has_nose_stream = false;
            String org;

            while(param_names.hasMoreElements()){
                if(param_names.nextElement().equals("nose_stream")){
                    has_nose_stream = true;
                    break;
                }
            }

            if(!has_nose_stream){ // then it's sent as json
                Gson gson = new Gson();

                StringBuffer jb = new StringBuffer();
                String line;
                try {
                    NoseRecord noseRecord;
                    BufferedReader reader = request.getReader();
                    while ((line = reader.readLine()) != null)
                        jb.append(line);


                    if(jb.toString().isEmpty()){
                        StringBuilder noseMap = new StringBuilder();

                        noseMap.append("{");
                        Enumeration<String> keys = request.getParameterNames();
                        int i = 0;
                        while(keys.hasMoreElements()){
                            String key = keys.nextElement();
                            if(i > 0)
                                noseMap.append(",");
                            noseMap.append("\"").append(key).append("\": ");

                            String val = request.getParameter(key);
                            if(val.contains("{")){ //it's an object stringified
                                noseMap.append(val);
                            }else{
                                noseMap.append("\"").append(val).append("\"");
                            }

                            ++i;
                        }
                        noseMap.append("}");
                        noseRecord = gson.fromJson(noseMap.toString(), NoseRecord.class);

                    }else {
                        noseRecord = gson.fromJson(jb.toString(), NoseRecord.class);
                    }

                    try{
                        org = request.getHeader("org");
                    }catch (NullPointerException e) {
                        org = this.organisation;
                    }
                    res = this.redisSink.sinkNoseRecord(noseRecord, org);
                    output = "{\"status\":\"" + res +"\"}";

                } catch (Exception e) {
                    System.out.println("error " + e.getMessage());
                }

            }else{
                String nose_stream = request.getParameter("nose_stream");
                org = request.getHeader("org");
                if(org.equals(this.organisation)) {
                    res = this.redisSink.sinkNoseRecord(new NoseRecord(nose_stream), this.organisation);
                    output = "{\"status\":\"" + res +"\"}";
                }else{
                    output = "{\"status\": \"fail\", \"reason\": \"wrong organisation\"}";
                }
                System.out.println(org);
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String output = "{\"status\": \"fail\"}", res;
        try {
            String org;
            Gson gson = new Gson();
            StringBuffer jb = new StringBuffer();
            String line;
            try {
                Map<String, NoseRecord> noseRecordMap = new HashMap<>();

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);

                Map<String, String> req_body = gson.fromJson(jb.toString(), Map.class);

                String raw_json = req_body.getOrDefault("matrix","{}");

                if(raw_json.isEmpty()){
                    raw_json = request.getParameter("matrix");
                }

                noseRecordMap = gson.fromJson(raw_json, Map.class);

                try{
                    org = request.getHeader("org");
                }catch (NullPointerException e) {
                    org = this.organisation;
                }

                //System.out.println(noseRecordMap);
                res = this.redisSink.sinkNoseRecord(noseRecordMap, org);
                //output = "{\"status\":\"" + res +"\"}";

            } catch (Exception e) {
                System.out.println("error " + e.getMessage());
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