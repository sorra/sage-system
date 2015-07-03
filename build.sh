echo Build begins...
mvn clean package \
&& echo Clean-copy to TOMCAT... \
&&rm -rf $TOMCAT_HOME/webapps/ROOT \
&& cp -r target/sage $TOMCAT_HOME/webapps/ROOT \
&& echo Build done.