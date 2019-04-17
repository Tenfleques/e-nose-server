package com.flequesboard;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")

//requires a json of form
/*
 *  {
 *      org: ru.vsuet,
 *      id: ru.vsuet.nose_123,
 *      date : 1555532348892,
 *      session: 1555532238739,
 *      s0: 60,
 *          ...
 *      sn: 72
 *  }
 */
public class SaveNoseRecord extends HttpServlet {
    private RedisSink redisSink;
    private String organisation;
    SaveNoseRecord(RedisSink redisSink, String organisation){
        this.redisSink = redisSink;
        this.organisation = organisation;
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String output = "{\"status\": \"fail\"}";
        try {

            String nose_stream = request.getParameter("nose_stream");
            if(nose_stream.isEmpty()){ // then it's sent as json
                //sent json, convert json to flat
            }else{
                String org = request.getHeader("org");
                if(org.equals(this.organisation)) {
                    String res = this.redisSink.sinkNoseRecord(new NoseRecord(nose_stream));
                    output = "{\"status\":\"" + res +"\"}";
                }else{
                    output = "{\"status\": \"fail\", \"reason\": \"wrong organisation\"}";
                }
                System.out.println(org);
            }


            //request.getParameterNames().toString();
            //
            //output = "{\"status\":" + res +"}";

            //response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(output);
            response.getWriter().close();
        }
    }

}