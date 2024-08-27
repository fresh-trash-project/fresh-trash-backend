FROM amazoncorretto:17
RUN mkdir -p deploy/imgs
WORKDIR /deploy

ARG API_VERSION
COPY ./build/libs/fresh-trash-backend-${API_VERSION}.jar api.jar
ENTRYPOINT ["java", "-jar", "/deploy/api.jar", "--spring.profiles.active=local", "--logging.level.root=error"]
