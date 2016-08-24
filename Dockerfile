FROM invenfantasy/scala

# 4 caching
COPY . /cosmos && cd /cosmos && sbt one-jar
WORKDIR /cosmos
