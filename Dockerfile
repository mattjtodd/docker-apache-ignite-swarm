FROM maven:3.5-jdk-8-alpine as builder

WORKDIR /build

COPY pom.xml .

RUN mvn -Dmaven.repo.local=/repo package

COPY src src

RUN mvn -o -Dmaven.repo.local=/repo -Djar.finalName=app -Dassembly.skipAssembly=true package

FROM openjdk:8-jre-alpine

WORKDIR /app

EXPOSE 11211 47100 47500 49112

COPY --from=builder /build/target/app.jar .

ENTRYPOINT ["java", "-jar", "app.jar"]