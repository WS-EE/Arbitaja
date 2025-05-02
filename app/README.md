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
        return 302 https://arbitaja.ee$uri;
        server_name arbitaja.ee;
}

server {
        listen 443 ssl default_server;
        listen [::]:443 ssl default_server;

        # Server name to respond on
        server_name arbitaja.ee;

        # SSL Certificates
        ssl_certificate /etc/nginx/certs/web.crt;
        ssl_certificate_key /etc/nginx/certs/web.key;

        # Reverse proxy against our frontend container
        location / {
                proxy_pass http://localhost:8080/;
        }
}
```
> Make sure you change the domain name `arbitaja.ee` under `server_name` and `return`