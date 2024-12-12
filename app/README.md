# Install arbitaja

## Setting up the environment
Enviromental values all come from `.env` file.
Required enviromental values are in `.env-example`.
Make sure you changes the `POSTGRES_PASSWORD` variable.

## Development
This will consume more resourceses then the Production build.

### Start docker compose in dev profile
#### Linux
```
~$ cd app
~$ sudo docker compose --profile dev build
~$ sudo docker compose --profile dev up -d
```
#### Windows
```
> cd app
> docker compose --profile dev build
> docker compose --profile dev up -d
```

## Production
### Start docker compose in prod profile
#### Linux
```
~$ cd app
~$ sudo docker compose --profile prod build
~$ sudo docker compose --profile prod up -d
```
#### Windows
```
> cd app
> docker compose --profile prod build
> docker compose --profile prod up -d
```