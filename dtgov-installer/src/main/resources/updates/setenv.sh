CATALINA_OPTS="-Xmx512M -XX:MaxPermSize=512m -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n -Dbtm.root=$CATALINA_HOME -Dbitronix.tm.configuration=$CATALINA_HOME/conf/btm-config.properties"
