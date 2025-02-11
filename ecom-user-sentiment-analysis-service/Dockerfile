FROM openjdk:17

ENV APP_HOME=/app

WORKDIR ${APP_HOME}

ARG JAR_FILE=ecom-user-sentiment-analysis-service-*-SNAPSHOT.jar

COPY target/${JAR_FILE} /app/ecom-user-sentiment-analysis-service.jar

RUN groupadd --gid 98871 appgroup && \
    useradd --uid 98871 --gid appgroup --create-home --home-dir ${APP_HOME} appuser && \
    chown -R appuser:appgroup ${APP_HOME}

USER appuser

EXPOSE 8990

ENTRYPOINT ["java", "-jar", "/app/ecom-user-sentiment-analysis-service.jar"]