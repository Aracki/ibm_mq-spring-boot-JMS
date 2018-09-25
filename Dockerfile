FROM java:openjdk-8-jre-alpine
COPY build/libs/ibm-0.0.1-SNAPSHOT.jar app.jar
#CMD cd /mnt/mqm \
#    && mkdir -p MQClient/certs \
#    && cd MQClient/certs \
#    && runmqakm -keydb -create -db client_key.p12 -pw tru5tpassw0rd -type pkcs12 -expire 1000 \
#    && runmqakm -cert -list all -db client_key.p12 -pw tru5tpassw0rd \
#    && runmqakm -cert -add -label QM1.cert -db client_key.p12 -type pkcs12 -pw tru5tpassw0rd -trust enable -file ../../MQServer/certs/QM1.cert \
ENTRYPOINT ["java", "-jar", "app.jar", "queue.name=$queue_name"]
