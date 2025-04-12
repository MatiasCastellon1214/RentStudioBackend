# Use an official Maven image to create the build artifacts
FROM maven:3.8.5-openjdk-17 AS builder

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml y descargar las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y construir la aplicación
COPY src ./src
RUN mvn clean package -DskipTests

# Usar una imagen base para Java 17
FROM eclipse-temurin:17-jre-jammy

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo JAR ejecutable desde la etapa de construcción
COPY --from=builder /app/target/dh-0.0.1-SNAPSHOT.jar /app/dh-0.0.1-SNAPSHOT.jar

# Exponer el puerto 8080
EXPOSE 8080

# Ejecutar el archivo JAR con el perfil de producción
ENTRYPOINT ["java", "-jar", "/app/dh-0.0.1-SNAPSHOT.jar"]
