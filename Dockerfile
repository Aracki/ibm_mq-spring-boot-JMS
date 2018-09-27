FROM ibmcom/ibmjava:8-sdk
COPY build/libs/ibm.jar app.jar
ENTRYPOINT ["java", \
            "-Djavax.net.ssl.trustStoreType=pkcs12", \
            "-Djavax.net.ssl.trustStore=/mnt/mqm/MQClient/certs/client_key.p12", \
            "-Djavax.net.ssl.trustStorePassword=tru5tpassw0rd", \
            "-jar", "app.jar"]
            
