# IBM MQ Spring Boot JMS

This repository contains Spring Boot application with JSM listener which listens on IBM MQ queue and write messages to file.
	
How to configure SSL: [TLS - IBM Developer](https://developer.ibm.com/messaging/learn-mq/mq-tutorials/secure-mq-tls/) <br>
Docker image used: https://hub.docker.com/r/ibmcom/ibmjava/  <br>
Java libraries used: [ibm-mq-allclient](https://mvnrepository.com/artifact/com.ibm.mq/com.ibm.mq.allclient/9.1.0.0) & [mq-jms-spring](https://mvnrepository.com/artifact/com.ibm.mq/mq-jms-spring-boot-starter/2.0.0)  <br>
MQ JMS Spring documentation: [mq-jms-spring](https://github.com/ibm-messaging/mq-jms-spring) <br>

## Generating certificates
### Run temporary SERVER container without TLS to generate keys:
`docker volume create qmdata`  <br>
`docker network create mq-demo-network`  <br>

`docker run -ti --entrypoint=/bin/bash --volume qmdata:/mnt/mqm ibmcom/mq:9.1.0.0`

``` bash
cd /mnt/mqm 
mkdir -p MQServer/certs
cd MQServer/certs

runmqakm -keydb -create -db key.p12 -pw k3ypassw0rd -type pkcs12 -expire 1000 -stash

runmqakm -cert -create -db key.p12 -label ibmwebspheremqqm1 -dn "cn=qm,o=ibm,c=uk" -size 2048 -default_cert yes -stashed

runmqakm -cert -list all -db key.p12 -stashed

runmqakm -cert -extract -db key.p12 -stashed -label ibmwebspheremqqm1 -target QM1.cert
```

### Run temporary CLIENT to generate client key store
`docker run -ti --entrypoint=/bin/bash --volume qmdata:/mnt/mqm --network mq-demo-network ibmcom/mq:9.1.0.0`

``` bash
cd /mnt/mqm
mkdir -p MQClient/certs
cd MQClient/certs

runmqakm -keydb -create -db client_key.p12 -pw tru5tpassw0rd -type pkcs12 -expire 1000

runmqakm -cert -add -label QM1.cert -db client_key.p12 -type pkcs12 -pw tru5tpassw0rd -trust enable -file ../../MQServer/certs/QM1.cert

runmqakm -cert -list all -db client_key.p12 -pw tru5tpassw0rd
```

## Run server
### Run SERVER with TLS:
`docker run --name mq-manager --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --volume qmdata:/mnt/mqm -p 1414:1414 -p 9443:9443 --network mq-demo-network --network-alias qmgr --detach --env MQ_APP_PASSWORD=passw0rd --env MQ_TLS_KEYSTORE=/mnt/mqm/MQServer/certs/key.p12 --env MQ_TLS_PASSPHRASE=k3ypassw0rd ibmcom/mq:9.1.0.0`
**Need to create proper queue** inside the server.

## Run client

Before running docker commands:
1. `./gradlew build`
2. `docker build -t spring-mq-client .`

### Run spring-mq-client with Docker
`docker run -it --name spring-mq-client --network mq-demo-network --volume qmdata:/mnt/mqm --rm spring-mq-client`
Values that can be overriden with **-e**: 
* CONN_NAME
* TRUST_STORE_TYPE
* TRUST_STORE
* TRUST_STORE_PWD
* …

### Run spring-mq-client without Docker
`java -Djavax.net.debug=ssl -Djavax.net.ssl.trustStoreType=pkcs12 -Djavax.net.ssl.trustStore=client_key.p12 -Djavax.net.ssl.trustStorePassword=tru5tpassw0rd -jar build/libs/ibm-0.0.1-SNAPSHOT.jar`

### ibm-client which can make SSL - based on IBM developer article
``` bash
docker run --name ibm-client --volume qmdata:/mnt/mqm --network mq-demo-network -d ibmcom/mq:9.1.0.0

cd /mnt/mqm/MQClient/opt
export PATH=$(pwd)/ibm-java-x86_64-80/bin:$PATH
cd ..

java -Djavax.net.ssl.trustStoreType=pkcs12 -Djavax.net.ssl.trustStore=./certs/client_key.p12 -Djavax.net.ssl.trustStorePassword=tru5tpassw0rd -cp ./libs/com.ibm.mq.allclient-9.0.4.0.jar:./libs/javax.jms-api-2.0.1.jar:. com.ibm.mq.samples.jms.JmsPutGet
```

## Misc
* For debugging add this:  `-Djavax.net.debug=ssl`
* Default CACERT file path: `/opt/ibm/java/jre/lib/security/cacerts`
* Run queue manager console: `runmqsc QM1` 
* See information about queue manager:  `DISPLAY CHANNEL(DEV.APP.SVRCONN)` 
* Creates a new queue: `DEFINE QLOCAL('testQueue')` 
