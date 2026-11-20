
FROM eclipse-temurin:21-jre

LABEL org.opencontainers.image.vendor="Ioza"

ENV TZ=America/Mexico_City

RUN groupadd --system kingdom \
    && useradd --system \
        --gid kingdom \
        --home-dir /app \
        --shell /usr/sbin/nologin \
        kingdom \
    && mkdir -p /app/tavern \
    && chown -R kingdom:kingdom /app

WORKDIR /app

COPY --chown=kingdom:kingdom app.jar app.jar

USER kingdom

VOLUME ["/app/tavern"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
