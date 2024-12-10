FROM postgres:17
COPY ./arbitaja.sql /opt/arbitaja.sql
RUN psql -U postgres -d arbitaja < /opt/arbitaja.sql