FROM ibmcom/ibmjava:8-sdk
COPY build/libs/ibm.jar app.jar

#---Default argument values---
ARG CONN_NAME=qmgr
ARG TRUST_STORE_TYPE=pkcs12
ARG TRUST_STORE=/mnt/mqm/MQClient/certs/client_key.p12
ARG TRUST_STORE_PWD=tru5tpassw0rd
#-----------------------------

ENV CONN_NAME=$CONN_NAME
ENV TRUST_STORE_TYPE=$TRUST_STORE_TYPE
ENV TRUST_STORE=$TRUST_STORE
ENV TRUST_STORE_PWD=$TRUST_STORE_PWD

ENTRYPOINT java \
           -Djavax.net.ssl.trustStoreType=$TRUST_STORE_TYPE \
           -Djavax.net.ssl.trustStore=$TRUST_STORE \
           -Djavax.net.ssl.trustStorePassword=$TRUST_STORE_PWD \
           -jar app.jar \
            --ibm.mq.connName=$CONN_NAME
            
