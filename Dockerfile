FROM structurizr/cli:2025.11.09 AS structurizr
WORKDIR /workspace
COPY src/main/resources/architecture/workspace.dsl .
RUN /usr/local/structurizr-cli/structurizr.sh export \
    -workspace workspace.dsl \
    -format static \
    -output static

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src

COPY --from=structurizr /workspace/static src/main/resources/META-INF/resources/

RUN mvn package -DskipTests -Dexec.skip=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache graphviz

COPY --from=build /app/target/quarkus-app/lib/ lib/
COPY --from=build /app/target/quarkus-app/*.jar .
COPY --from=build /app/target/quarkus-app/app/ app/
COPY --from=build /app/target/quarkus-app/quarkus/ quarkus/

EXPOSE 8080

ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0"

CMD ["java", "-jar", "quarkus-run.jar"]
