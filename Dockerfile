FROM java:openjdk-8-jdk

#add the server
WORKDIR /app

ADD target/*with-dependencies.jar /app/server
ADD resources/config.json /app/
ADD scripts/start-server.sh /usr/bin/nose-server.sh

EXPOSE 7070 
CMD ["bash", "/usr/bin/nose-server.sh"]