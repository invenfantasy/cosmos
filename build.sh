#!/bin/bash 

docker run -it -v $PWD:/cosmos invenfantasy/cosmos sbt one-jar
