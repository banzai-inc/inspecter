#!/bin/bash

read -rep "Enter your Clojars username (not email): " username
echo -n "Enter your Clojars token: "
read -rs token

echo
echo "Deploying..."

env CLOJARS_USERNAME="$username" CLOJARS_PASSWORD="$token" clojure -X:deploy