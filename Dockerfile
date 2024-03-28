FROM eclipse-temurin:17

RUN apt-get update && apt-get -y install \
    openjdk-17-jdk \
    --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build/libs/*.jar /app/*.jar

ENTRYPOINT ["java", "-jar", "articulosapi.jar"]
