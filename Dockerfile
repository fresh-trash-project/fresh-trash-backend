FROM amazoncorretto:17
RUN mkdir -p deploy
WORKDIR /deploy
COPY ./build/libs/fresh-trash-backend-0.1.jar api.jar
ENTRYPOINT ["java", "-jar", "/deploy/api.jar", "-DSpring.prifiles.active=local"]
