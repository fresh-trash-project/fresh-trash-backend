FROM amazoncorretto:17
RUN mkdir -p deploy/imgs
WORKDIR /deploy
COPY ./build/libs/fresh-trash-backend-1.0.0.jar api.jar
ENTRYPOINT ["java", "-jar", "/deploy/api.jar", "--spring.profiles.active=local", "--logging.level.root=error"]
