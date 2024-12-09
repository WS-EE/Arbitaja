# Install arbitaja

## Setting up the environment
Enviromental values all come from `.env` file.
Required enviromental values are in `.env-example`.
Make sure you changes the `POSTGRES_PASSWORD` variable.

## Development
This will consume more resourceses then the prodaction build.

### Setup Vue
```
~$ cd app/frontend
~$ npm install
```

### Start docker compose in dev profile
```
~$ cd app
~$ sudo docker compose --profile dev build 
~$ sudo docker compose --profile dev up -d
~$ sudo docker exec -i postgresql psql -U postgres -d arbitaja < docker-build/arbitaja.sql
~$ sudo docker compose restart
```

## Prodaction
```
~$ cd app
~$ sudo docker compose --profile prod build 
~$ sudo docker compose --profile prod up -d
~$ sudo docker exec -i postgresql psql -U postgres -d arbitaja < docker-build/arbitaja.sql
~$ sudo docker compose restart
```