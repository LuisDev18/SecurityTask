FROM eclipse-temurin:17

WORKDIR /app

COPY build/libs/articulosapi-0.0.1-SNAPSHOT.jar /app/articulosapi.jar

ENTRYPOINT ["java", "-jar", "articulosapi.jar"]
