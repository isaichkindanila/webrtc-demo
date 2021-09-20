#!/bin/bash

sudo docker rm -f coturn || true
sudo docker run \
    -v $(dirname $(readlink -f $0))/coturn.conf:/etc/coturn/turnserver.conf \
    --network=host \
    --rm -d \
    --name coturn \
    coturn/coturn:alpine
sudo docker logs -f coturn
