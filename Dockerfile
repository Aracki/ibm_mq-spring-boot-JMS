FROM ibmcom/ibmjava:8-sdk

#---------------Default argument values--------------------
ARG TRUST_STORE_TYPE=pkcs12
ARG TRUST_STORE=/mnt/mqm/MQClient/certs/client_key.p12
ARG TRUST_STORE_PWD=tru5tpassw0rd
ARG KEY_STORE=/mnt/mqm/MQServer/certs/QM1.cert
ARG KEY_STORE_PWD=keypassw0rd

ARG CONN_NAME
ARG CHANNEL
ARG QUEUE_MGR
ARG QUEUE_NAME
ARG SSL_CIPHER_SUITE

ARG FILE_LOCATION="/tmp/msgs"
#----------------------------------------------------------

ENV TRUST_STORE_TYPE=$TRUST_STORE_TYPE \
    TRUST_STORE=$TRUST_STORE \
    TRUST_STORE_PWD=$TRUST_STORE_PWD \
    KEY_STORE=$KEY_STORE \
    KEY_STORE_PWD=$KEY_STORE_PWD \
    CONN_NAME=$CONN_NAME \
    CHANNEL=$CHANNEL \
    QUEUE_MGR=$QUEUE_MGR \
    QUEUE_NAME=$QUEUE_NAME \
    SSL_CIPHER_SUITE=$SSL_CIPHER_SUITE \
    FILE_LOCATION=$FILE_LOCATION

COPY build/libs/ibm.jar app.jar

ENTRYPOINT java \
           -Djavax.net.ssl.trustStoreType=$TRUST_STORE_TYPE \
           -Djavax.net.ssl.trustStore=$TRUST_STORE \
           -Djavax.net.ssl.trustStorePassword=$TRUST_STORE_PWD \
           -Djavax.net.ssl.keyStore=$KEY_STORE \
           -Djavax.net.ssl.keyStorePassword=$KEY_STORE_PWD \
           -jar app.jar \
           --file.location=$FILE_LOCATION \
           --ibm.mq.connName=$CONN_NAME \
           --ibm.mq.channel=$CHANNEL \
           --ibm.mq.queueManager=$QUEUE_MGR \
           --ibm.mq.queueName=$QUEUE_NAME \
           --ibm.mq.sslCipherSuite=$SSL_CIPHER_SUITE
            
