# Install arbitaja

## Setting up the environment
Enviromental values all come from `.env` file.
Required enviromental values are in `.env-example`.

### Explantaion to all the .env variables
---

This is the name of the database
```
POSTGRES_DB=arbitaja
```
---

This is the username and password used in postgres setup
```
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=VeryTrickyPassword
```
---

This is the url used when backend connects to database
```
POSTGRES_URL=jdbc:postgresql://db:5432/arbitaja
```
> Note that `/arbitaja` needs to be the same as `POSTGRES_DB` variable
---

These variables are used for axios.
Axios is used for the backend to call the frontend

They are also used for CORS.
```
VITE_APP_BASE_URL=http://localhost/
VITE_APP_API_ENDPOINT=api/v1/
```
> Unless you change this variable, the website will only be available from `http://localhost`.
> This is fine when developing.

> When in prodtuction change this to `https://yourdomain.ee`.
> The domain name should be the same that you will configure at `Setup nginx reverse proxy for SSL`
---

The default username password to login in with.
```
ADMIN_USERNAME=admin
ADMIN_PASSWORD=arbitaja
```
---

The api key used to insert history or points to the competition/competitor.
```
API_KEY=arbitaja
```

## Development
This will consume more resourceses then the Production build.

This starts the web server on port 80

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
This starts the server on port 8080.

This will make adding an SSL reverse proxy in front easier.
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

## Setup nginx reverse proxy for SSL
This is meant for the **prod build** of this project.
If you want to set it up for dev build, then you need to come up with the configuratsion yourself.
### Firewall rules
Make sure that your current firewall configuratsion allows traffic between `client` > `proxy` > `frontend`.
Here is an example of nftables input chain.
```
#!/usr/sbin/nft -f

flush ruleset

table inet filter {
        chain input {
                type filter hook input priority filter; policy drop
                ct state established,related accept
                iif "lo" accept comment "Allow loopback traffic so proxy > frontend works"
                tcp dport { 22, 80, 443 } accept comment "allow SSH/HTTP/HTTPS traffic"
        }
        chain forward {
                type filter hook forward priority filter;
        }
        chain output {
                type filter hook output priority filter;
        }
}
```
### Nginx config
Hardening the nginx is the responsebility of the administrator.
Here is the BASIC configuratsion for nginx `server{}` block in getting you up and started.
```
# HTTP Server block
server {
        listen 80;
        return 302 https://yourdomain.ee$uri;
        server_name yourdomain.ee;
}

server {
        listen 443 ssl default_server;
        listen [::]:443 ssl default_server;

        # Server name to respond on
        server_name yourdomain.ee;

        # SSL Certificates
        ssl_certificate /etc/nginx/certs/web.crt;
        ssl_certificate_key /etc/nginx/certs/web.key;

        # Reverse proxy against our frontend container
        location / {
                proxy_pass http://localhost:8080/;
        }
}
```
> Make sure you change the domain name `yourdomain.ee` under `server_name` and `return`