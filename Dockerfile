FROM invenfantasy/scala

# 4 caching
RUN git clone https://github.com/invenfantasy/cosmos && cd cosmos && sbt one-jar
