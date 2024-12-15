FROM node:lts-alpine

# Set working directory
WORKDIR /opt/arbitaja-frontend/

# Copy all root packages
COPY frontend/. .
COPY .env .

# install project dependencies
RUN npm install

CMD [ "npm", "run", "dev" ]