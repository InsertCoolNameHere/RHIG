FROM openjdk:8-jre-alpine

MAINTAINER Saptashwa Mitra,sapmitra@colostate.edu
ADD env_var.sh .
RUN ./env_var.sh

ENV IRODS_USER='radix_subterra'
ENV IRODS_PASSWORD='roots&radix2018'

ADD ./dist/RHIG-1.0.jar .

