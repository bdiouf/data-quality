#!/bin/bash
UNAME_STR=`uname`
CONTAINER_NAME="talend/tdq-es:0.1"
CONTAINER_IP="192.168.59.103"

if [[ "$UNAME_STR" == 'Linux' ]]; then
  #export DOCKER_HOST="tcp://192.168.59.103:2376"
  #export DOCKER_CERT_PATH="$HOME/.boot2docker/certs/boot2docker-vm"
  #export DOCKER_TLS_VERIFY=1
  echo "linuxers need to config docker first."
  exit
elif [[ "$UNAME_STR" == 'Darwin'  ]]; then
  boot2docker up
  CONTAINER_IP=`boot2docker ip`
  $(boot2docker shellinit)
fi


docker build -t $CONTAINER_NAME .
docker run -d -p 9200:9200 -p 9300:9300 $CONTAINER_NAME /elasticsearch/bin/elasticsearch \
       --node.name=tdq
#docker run -i -t $CONTAINER_NAME /usr/bin/curl http://192.168.59.103:9200
docker run --rm -i -t $CONTAINER_NAME /bin/bash

#shut down all nodes at the end
#curl -XPOST 'http://192.168.59.103:9200/_shutdown'
