# -----------------------------------------------------------------------------
# Maven package - Reccomendation Service
#-----------------------------------------------------------------------------
FROM maven:3.8.6-eclipse-temurin-17-alpine as builder
COPY . /tmp/
WORKDIR tmp
RUN mvn clean package -D skipTests

# -----------------------------------------------------------------------------
# Extract Spring jar layers
#-----------------------------------------------------------------------------
FROM maven:3.8.6-eclipse-temurin-17-alpine as extracted
WORKDIR application
ARG JAR_FILE=/tmp/target/*.jar
COPY --from=builder ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# -----------------------------------------------------------------------------
# Create Final Docker Image
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR application
COPY --from=extracted application/dependencies/ ./
COPY --from=extracted application/spring-boot-loader/ ./
COPY --from=extracted application/snapshot-dependencies/ ./
COPY --from=extracted application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]