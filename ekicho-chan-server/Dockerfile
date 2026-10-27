# syntax=docker/dockerfile:1

FROM eclipse-temurin:25.0.4_7-jdk-noble AS builder

WORKDIR /workspace

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew

# Source is copied after the wrapper files so dependency-related layers can be reused.
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test && \
    cp "$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -print -quit)" /workspace/app.jar

FROM eclipse-temurin:25.0.4_7-jre-noble

RUN apt-get update && \
    apt-get install --yes --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd --system ekicho && \
    useradd --system --gid ekicho --home-dir /app --shell /usr/sbin/nologin ekicho

WORKDIR /app
COPY --from=builder --chown=ekicho:ekicho /workspace/app.jar /app/app.jar

USER ekicho
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=production \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl --fail --silent --show-error "http://127.0.0.1:${PORT:-8080}/actuator/health" || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
