all: build run

build: image
	docker run --rm -it -v $(PWD):/cosmos invenfantasy/cosmos:sy sbt one-jar

image:
	docker pull invenfantasy/cosmos:sy

run:
	mkdir -p /tmp/cosmos
	java -jar cosmos-server/target/scala-2.11/cosmos-server_2.11-0.1.5-one-jar.jar \
		-com.mesosphere.cosmos.marathonUri=http://10.132.47.122/service/marathon-user \
		-com.mesosphere.cosmos.mesosMasterUri=http://10.132.47.122/mesos \
		-com.mesosphere.cosmos.zookeeperUri=zk://10.132.47.122:2181/cosmos \
		-com.mesosphere.cosmos.dataDir=/tmp/cosmos
