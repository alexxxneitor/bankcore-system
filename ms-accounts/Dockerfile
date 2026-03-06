FROM eclipse-temurin:17-jdk

ARG BUILD_DATE=unknown
ARG BUILD_VERSION=unknown
ARG IMAGE_DESCRIPTION=unknown
ARG IMAGE_NAME="Bankcore-accounts"
ARG UID=1010
ARG GID=1010

ENV MAVEN_HOME=/usr/share/maven
ENV MAVEN_CONFIG=/home/group6/.m2

LABEL group6.bankcore-accounts.build-date=$BUILD_DATE
LABEL group6.bankcore-accounts.name=$IMAGE_NAME
LABEL group6.bankcore-accounts.description=$IMAGE_DESCRIPTION
LABEL group6.bankcore-accounts.base.image="eclipse-temurin:17-jdk"
LABEL group6.bankcore-accounts.version=$BUILD_VERSION
LABEL maintainer="GROUP6"

RUN groupadd -g "${GID}" group6 \
    && useradd --create-home --no-log-init -u "${UID}" -g group6 group6

COPY --from=maven:3.9.9-eclipse-temurin-17 ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=maven:3.9.9-eclipse-temurin-17 /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY --from=maven:3.9.9-eclipse-temurin-17 /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml

RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

USER group6

WORKDIR /opt/app

COPY --chown=group6:group6 . .

RUN mvn dependency:go-offline \
    && mvn clean package -DskipTests

ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]

CMD ["java","-jar","target/ms-accounts.jar"]
