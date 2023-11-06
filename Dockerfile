FROM tomcat:9.0

ADD target/socialnet.war /usr/local/tomcat/webapps

RUN /usr/local/tomcat/bin/shutdown.sh
RUN /usr/local/tomcat/bin/startup.sh
