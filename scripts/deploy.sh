#!/bin/bash

read -rep "Enter your Clojars username (not email): " username
echo -n "Enter your Clojars token: "
read -rs token

echo

clojure -T:build jar

echo "Deploying..."

env CLOJARS_USERNAME="$username" CLOJARS_PASSWORD="$token" clojure -X:deploy