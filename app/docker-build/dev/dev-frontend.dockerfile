FROM node:lts-alpine

# Set working directory
WORKDIR /opt/arbitaja-frontend/

# copy both 'package.json' and 'package-lock.json' (if available)
COPY ./package*.json ./

# install project dependencies
RUN npm install

CMD [ "npm", "dev", "run" ]