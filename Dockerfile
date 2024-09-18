FROM alpine:3.20.0

RUN apk update && \
    apk upgrade && \
    apk add imagemagick

RUN apk add openjdk21

VOLUME /tmp
ARG DEPENDENCY=target/dependency

COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

RUN apk add --no-cache tzdata
ENV TZ=America/Detroit
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENTRYPOINT ["java", "-cp","app:app/lib/*","org.miktmc.Application"]
