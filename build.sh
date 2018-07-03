#!/usr/bin/env bash

#Download Clojure

curl -LO wget https://download.clojure.org/install/linux-install-1.9.0.381.sh
chmod u+x linux-install-1.9.0.381.sh
./linux-install-1.9.0.381.sh


# Download GRAALVM

curl -LO https://github.com/oracle/graal/releases/download/vm-1.0.0-rc2/graalvm-ce-1.0.0-rc2-linux-amd64.tar.gz
mkdir ~/runtime
tar -zxf graalvm-ce-1.0.0-rc2-linux-amd64.tar.gz -C ~/runtime

