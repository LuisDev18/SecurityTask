# -----------------------------------------------------------
# Etapa 1: Builder - Usamos el JDK para compilar la aplicación con Gradle
# -----------------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS builder

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos del build de Gradle
# Copia el wrapper de Gradle
COPY gradle ./gradle
# Copia los archivos de configuración de Gradle
COPY gradlew build.gradle settings.gradle ./

# Copia los archivos de código fuente
COPY src ./src

# Otorga permisos de ejecución al wrapper de Gradle
RUN chmod +x ./gradlew

# Compila y empaqueta la aplicación usando el wrapper de Gradle
# El task 'bootJar' es el estándar de Spring Boot para crear el JAR ejecutable
RUN ./gradlew bootJar

# -----------------------------------------------------------
# Etapa 2: Desarrollo - Usamos el JDK para la ejecución
# -----------------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS development

ARG APP_VERSION
# Metadatos OCI
LABEL org.opencontainers.image.title="articulos-api" \
      org.opencontainers.image.description="API de artículos para desarrollo en Java 21" \
      org.opencontainers.image.source="https://github.com/LuisDev18/SecurityTask" \
      org.opencontainers.image.version="${APP_VERSION}"

# Crea un usuario no-root por seguridad
RUN groupadd -g 10001 app && useradd -u 10000 -g app -s /usr/sbin/nologin -m app

# Establece el directorio de trabajo para la aplicación
WORKDIR /app

# Copia el archivo JAR compilado desde la etapa 'builder'
# El JAR se encuentra en build/libs por defecto
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Cambia a usuario no-root para ejecutar la aplicación
USER app

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
