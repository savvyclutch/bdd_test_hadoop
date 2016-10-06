FROM ubuntu:14.04

MAINTAINER Bogdan Frankovskyi <bogdan@savvyclutch.com>

RUN apt-get update ; apt-get upgrade -y

### INSTALL JAVA
RUN apt-get install -y default-jdk wget software-properties-common

### ADD HADOOP
RUN mkdir -p /usr/local/hadoop
RUN cd /tmp/ ; wget http://mirrors.sonic.net/apache/hadoop/common/hadoop-2.6.0/hadoop-2.6.0.tar.gz ; tar xzf hadoop-2.6.0.tar.gz ; mv hadoop-2.6.0/* /usr/local/hadoop

### ADD GRADLE
RUN add-apt-repository ppa:cwchien/gradle
RUN apt-get update ; apt-get install -y gradle

ENV GRADLE_USER_HOME /cache

#### ADD THE DIRECTORY FOR CODEBASE
ADD . /opt/bdd_test_hadoop/
WORKDIR /opt/bdd_test_hadoop/

#### DOWNLOAD AND INSTALL DEPENDENCIES
RUN gradle dependencies && gradle install

#### RUN TESTS
CMD gradle cucumber