package com.flequesboard;

import java.util.concurrent.CountDownLatch;

class NoseServer {
    NoseServer(String brokers, String topic, String rpcEndpoint, Integer
            rpcPort, String redishost, int redisport) throws Exception {

        RedisSink redisSink = new RedisSink(redishost, redisport);
        StreamKafka streamKafka = new StreamKafka(brokers, topic, redisSink);
        streamKafka.startStream();

        final RPCService
                restService = new RPCService(redisSink, topic);

        restService.start(rpcPort);

        printEndPoints(rpcEndpoint,rpcPort);



        final CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streamKafka.getStream().close();
                redisSink.close();
                try {
                    restService.stop();
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
                latch.countDown();
            }
        });
        System.exit(0);
    }

    private void printEndPoints(String rpcEndpoint, int rpcPort){
        StringBuilder info = new StringBuilder("\n" +
                "*** available endpoints ***\n");

        //info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append("/instances\n");

        info.append("\n\t POST \n");
        info.append("\t   http://").append(rpcEndpoint).append(":").append(rpcPort).append("/register/\n");

        info.append("{\n\t\"noseID\": noseID, \n\t \"groupID\" : groupID\n}\n");

        info.append("\n\t GET \n");

        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append("/noses\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append("/csv/noses\n");

        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/sessions/{nose_id}\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/csv/sessions/{nose_id}\n");


        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/session/{nose_id}/{session_id}\n");
        info.append("*\t     http://").append(rpcEndpoint).append(":").append(rpcPort).append
                ("/csv/session/{nose_id}/{session_id}\n");


        System.out.print(info);
    }
}
