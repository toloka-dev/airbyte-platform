ARG JDK_IMAGE=airbyte/airbyte-base-java-image:3.2.2

FROM scratch as builder
WORKDIR /app
ADD airbyte-app.tar /app

FROM ${JDK_IMAGE}
WORKDIR /app
COPY --chown=airbyte:airbyte --from=builder /app /app
USER airbyte:airbyte

ENTRYPOINT ["/bin/bash", "-c", "airbyte-app/bin/airbyte-metrics-reporter"]
