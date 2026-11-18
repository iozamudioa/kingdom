FROM eclipse-temurin:21-jre

LABEL org.opencontainers.image.title="Kingdom Host"
LABEL org.opencontainers.image.vendor="Ioza"
LABEL org.opencontainers.image.version="0.0.1-SNAPSHOT"

ENV TZ=America/Mexico_City

RUN groupadd --system kingdom \
    && useradd --system \
       --gid kingdom \
       --home-dir /app \
       --shell /usr/sbin/nologin \
       kingdom \
    && mkdir -p /app \
    && chown -R kingdom:kingdom /app

WORKDIR /app

COPY --chown=kingdom:kingdom \
    kingdom-tavern-host/target/kingdom-tavern-host-0.0.1-SNAPSHOT.jar \
    app.jar

USER kingdom

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]