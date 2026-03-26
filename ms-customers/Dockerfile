FROM maven:3.9.9-eclipse-temurin-17 AS builder

ARG UID=1010
ARG GID=1010

RUN groupadd -g "${GID}" group6 \
    && useradd --create-home --no-log-init -u "${UID}" -g group6 group6

WORKDIR /opt/app

COPY --chown=group6:group6 pom.xml .
RUN mvn dependency:go-offline -q

COPY --chown=group6:group6 src ./src

RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre AS runtime

ARG BUILD_DATE=unknown
ARG BUILD_VERSION=unknown
ARG IMAGE_DESCRIPTION=unknown
ARG IMAGE_NAME="Bankcore-customers"
ARG UID=1010
ARG GID=1010

LABEL group6.bankcore-customers.build-date=$BUILD_DATE \
      group6.bankcore-customers.name=$IMAGE_NAME \
      group6.bankcore-customers.description=$IMAGE_DESCRIPTION \
      group6.bankcore-customers.base.image="eclipse-temurin:17-jre" \
      group6.bankcore-customers.version=$BUILD_VERSION \
      maintainer="GROUP6"

RUN groupadd -g "${GID}" group6 \
    && useradd --create-home --no-log-init -u "${UID}" -g group6 group6

WORKDIR /opt/app

COPY --from=builder --chown=group6:group6 /opt/app/target/ms-customers.jar app.jar

USER group6

ENTRYPOINT ["java", "-jar", "app.jar"]