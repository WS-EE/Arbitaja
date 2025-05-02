# build stage
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY ./frontend/package*.json ./
RUN npm install
COPY frontend/. .
COPY .env .
RUN npm run build
RUN rm .env

# production stage
FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY ./docker-build/prod/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
