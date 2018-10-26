# IBM MQ Spring Boot JMS

This repository contains Spring Boot application with JSM listener which listens on IBM MQ queue and write messages to file.
	
How to configure SSL: [TLS - IBM Developer](https://developer.ibm.com/messaging/learn-mq/mq-tutorials/secure-mq-tls/) <br>
Docker image used: https://hub.docker.com/r/ibmcom/ibmjava/  <br>
Java libraries used: [ibm-mq-allclient](https://mvnrepository.com/artifact/com.ibm.mq/com.ibm.mq.allclient/9.1.0.0) & [mq-jms-spring](https://mvnrepository.com/artifact/com.ibm.mq/mq-jms-spring-boot-starter/2.0.0)  <br>
MQ JMS Spring documentation: [mq-jms-spring](https://github.com/ibm-messaging/mq-jms-spring) <br>

## Generating certificates
### Run temporary SERVER container without TLS to generate keys:

1. `docker volume create qmdata` 
2. `docker network create mq-demo-network`
3. `docker run -ti --entrypoint=/bin/bash --volume qmdata:/mnt/mqm ibmcom/mq:9.1.0.0`

``` bash
cd /mnt/mqm 
mkdir -p MQServer/certs
cd MQServer/certs

# Create a key database (also called the keyStore or certificate store), and add and stash the password for it.
runmqakm -keydb -create -db key.p12 -pw k3ypassw0rd -type pkcs12 -expire 1000 -stash

# Use the runmqakm tool to create a self-signed certificate. The command line options we are using determine where the certificate should be held (key.p12), the label attached to the certificate (ibmwebspheremqqm1), the distinguished name to be included in the certificate (cn=qm,o=ibm,c=uk) and the keysize (2048 bits).
runmqakm -cert -create -db key.p12 -label ibmwebspheremqqm1 -dn "cn=qm,o=ibm,c=uk" -size 2048 -default_cert yes -stashed

# If we look at the contents of the keyStore now, we’ll find that this self-signed certificate has been generated and added.
runmqakm -cert -list all -db key.p12 -stashed

# We now need to extract the public key that the client application will need to be able to communicate with the queue manager. This is saved to a file named QM1.cert.
runmqakm -cert -extract -db key.p12 -stashed -label ibmwebspheremqqm1 -target QM1.cert
```

### Run temporary CLIENT to generate client key store
`docker run -ti --entrypoint=/bin/bash --volume qmdata:/mnt/mqm --network mq-demo-network ibmcom/mq:9.1.0.0`

``` bash
cd /mnt/mqm
mkdir -p MQClient/certs
cd MQClient/certs

# Use runmqakm to create a client trustStore.
runmqakm -keydb -create -db client_key.p12 -pw tru5tpassw0rd -type pkcs12 -expire 1000

# From the /mnt/mqm/MQClient/certs folder, run the command to add the public key certificate to the client’s trustStore.
runmqakm -cert -add -label QM1.cert -db client_key.p12 -type pkcs12 -pw tru5tpassw0rd -trust enable -file ../../MQServer/certs/QM1.cert

# Inspecting the contents of the trustStore will now show the queue manager’s public key.
runmqakm -cert -list all -db client_key.p12 -pw tru5tpassw0rd
```

## Run server
### Run SERVER with TLS:

Create docker network if it’s not created already: `docker network create mq-demo-network`

```
docker run --name mq-manager \
--volume qmdata:/mnt/mqm \
-p 1414:1414 \
-p 9443:9443 \
--network mq-demo-network \
--network-alias qmgr --detach \
--env MQ_APP_PASSWORD=passw0rd \
--env MQ_TLS_KEYSTORE=/mnt/mqm/MQServer/certs/key.p12 \
--env MQ_TLS_PASSPHRASE=k3ypassw0rd \
--env LICENSE=accept \
--env MQ_QMGR_NAME=QM1 \
ibmcom/mq:9.1.0.0
```

## Run client

Before running docker container you will need to build Spring Boot project and Docker image:
1. `./gradlew build`
2. `docker build -t spring-mq-client .` from the root of the project

### Run spring-mq-client with Docker

```
docker run -it --name spring-mq-client --network mq-demo-network --volume qmdata:/mnt/mqm \
-e CONN_NAME=qmgr \
-e USER=app \
-e USER_PWD=passw0rd \
-e CHANNEL=DEV.APP.SVRCONN \
-e QUEUE_MGR=QM1 \
-e QUEUE_NAME=DEV.QUEUE.1 \
-e SSL_CIPHER_SUITE=SSL_RSA_WITH_AES_128_CBC_SHA256 \
--rm spring-mq-client
```

## Misc
* For debugging add this:  `-Djavax.net.debug=ssl`
* Default CACERT file path: `/opt/ibm/java/jre/lib/security/cacerts`
* Run queue manager console: `runmqsc QM1` 
* See information about queue manager:  `DISPLAY CHANNEL(DEV.APP.SVRCONN)` 
* Creates a new queue: `DEFINE QLOCAL('testQueue')` 
