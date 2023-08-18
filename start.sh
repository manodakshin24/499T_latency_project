#!/bin/bash

# Make scripts executable
chmod +x scripts/docker-setup.sh
chmod +x scripts/client-1.sh
chmod +x scripts/client-2.sh
chmod +x shutdown.sh

echo "Scripts setup complete."

# Run the docker-setup.sh script
./scripts/docker-setup.sh ; \

 echo "Giving 1 minutes for the servers to start up" ; \

# Countdown for 1 minutes
for ((i = 60; i >= 1; i--)); do
    echo -ne "Running client-1 in $i seconds...\r"
    sleep 1
done
echo ; \

# Run the client scripts after docker setup
./scripts/client-1.sh ; \

# Countdown for 10 seconds
for ((i = 10; i >= 1; i--)); do
    echo -ne "Running client-2 in $i seconds...\r"
    sleep 1
done
echo ; \

./scripts/client-2.sh

echo "Please run ./shutdown.sh to clean up and remove all docker containers/images/networks."

