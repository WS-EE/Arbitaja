FROM postgres:17
COPY ./arbitaja.sql /docker-entrypoint-initdb.d/
