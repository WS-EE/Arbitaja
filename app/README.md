# Install arbitaja

## Setting up the environment
Enviromental values all come from `.env` file.
Required enviromental values are in `.env-example`.
Make sure you changes the `POSTGRES_PASSWORD` variable.

## Development
This will consume more resourceses then the Production build.

### Setup Vue
```
~$ cd app/frontend
~$ npm install
```

### Start docker compose in dev profile
#### Linux
```
~$ cd app
~$ sudo docker compose --profile dev build
~$ sudo docker compose up -d
~$ sudo docker exec -i postgresql psql -U postgres -d arbitaja < docker-build/arbitaja.sql
~$ sudo docker compose --profile dev up -d
~$ sudo docker compose restart
```
#### Windows
```
> cd app
> docker compose --profile dev build
> docker compose --profile dev build
> docker compose up -d
> docker cp .\docker-build\arbitaja.sql postgresql:/tmp/arbitaja.sql
> docker exec -i postgresql psql -U postgres -d arbitaja -f /tmp/arbitaja.sql
> docker compose --profile dev up -d
> docker compose restart
```

## Production
### Start docker compose in prod profile
#### Linux
```
~$ cd app
~$ sudo docker compose --profile prod build
~$ sudo docker compose up -d
~$ sudo docker exec -i postgresql psql -U postgres -d arbitaja < docker-build/arbitaja.sql
~$ sudo docker compose --profile prod up -d
~$ sudo docker compose restart
```
#### Windows
```
> cd app
> docker compose --profile prod build
> docker compose up -d
> docker cp .\docker-build\arbitaja.sql postgresql:/tmp/arbitaja.sql
> docker exec -i postgresql psql -U postgres -d arbitaja -f /tmp/arbitaja.sql
> docker compose --profile prod up -d
> docker compose restart
```