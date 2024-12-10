# production stage
FROM nginx:stable-alpine
COPY ./nginx.conf /etc/nginx/conf.d/arbitaja.conf
RUN rm /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]