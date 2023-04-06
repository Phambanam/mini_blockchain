FROM maven:3.6-jdk-8 as build
WORKDIR /app
COPY . .
RUN mvn verify package

FROM openjdk:11

COPY --from=build /app/target/mini_blockchain-1.0-jar-with-dependencies.jar /usr/local/lib/mini_blockchain.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/mini_blockchain.jar"]