FROM gradle:8.7.0-jdk21 AS build

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build --no-daemon

FROM amazoncorretto:22

WORKDIR /usr/src/app

VOLUME /usr/src/app/.data

COPY --from=build /home/gradle/src/build/libs/*.jar /usr/src/app/executable.jar

ENTRYPOINT [ "java", "-jar", "/usr/src/app/executable.jar" ]