FROM openjdk:8-jre-alpine
MAINTAINER Antonio Marin <antonio.marin.jimenez@gmail.com>

######## Container user #######

ENV CONTAINER_USER pika
ENV CONTAINER_HOME /opt

# Specify the user which should be used to execute all commands below
USER root

# Create the user and group used to launch processes
# The user ID 1000 is the default for the first "regular" user,
# so there is a high chance that this ID will be equal to the current user
# making it easier to use volumes (no permission issues)
RUN addgroup -S $CONTAINER_USER \
    && adduser -S -D -u 1000 -g $CONTAINER_USER $CONTAINER_USER

######## Application installation #######

ENV APP_HOME $CONTAINER_HOME/app

# Adding application container files and libs
ADD ./docker/container/app $APP_HOME
ADD ./build/libs/*.jar $APP_HOME/libs/app.jar
RUN chown -R $CONTAINER_USER:$CONTAINER_USER $APP_HOME

# Adding entrypoint script
COPY ./docker/container/entrypoint.sh /usr/bin/entrypoint.sh
RUN chmod +x /usr/bin/entrypoint.sh

VOLUME $APP_HOME/config
VOLUME $APP_HOME/logs

######## Application launch #######

USER $CONTAINER_USER
WORKDIR $APP_HOME

# Application runtime env variables
ENV JAVA_OPTS ""
ENV APP_PROFILE default

EXPOSE 8090

ENTRYPOINT ["entrypoint.sh"]
CMD ["start"]