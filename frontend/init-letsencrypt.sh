#!/bin/bash

domains=(your-domain.com)
email="your-email@example.com" # Change this to your email
staging=0 # Set to 1 if you're testing your setup

# Create required directories
mkdir -p ./data/certbot/conf
mkdir -p ./data/certbot/www

# Create dummy certificates for nginx to start
echo "### Creating dummy certificates ..."
mkdir -p ./data/certbot/conf/live/${domains[0]}
openssl req -x509 -nodes -newkey rsa:2048 -days 1\
    -keyout './data/certbot/conf/live/${domains[0]}/privkey.pem' \
    -out './data/certbot/conf/live/${domains[0]}/fullchain.pem' \
    -subj '/CN=localhost'

# Start nginx
echo "### Starting nginx ..."
docker-compose up --force-recreate -d nginx

# Delete dummy certificates
echo "### Deleting dummy certificates ..."
docker-compose run --rm --entrypoint "\
    rm -Rf /etc/letsencrypt/live/${domains[0]} && \
    rm -Rf /etc/letsencrypt/archive/${domains[0]} && \
    rm -Rf /etc/letsencrypt/renewal/${domains[0]}.conf" certbot

# Request real certificates
echo "### Requesting Let's Encrypt certificate ..."
docker-compose run --rm --entrypoint "\
    certbot certonly --webroot -w /var/www/certbot \
    $staging_arg \
    --email $email \
    -d ${domains[0]} \
    --rsa-key-size 4096 \
    --agree-tos \
    --force-renewal" certbot

# Reload nginx
echo "### Reloading nginx ..."
docker-compose exec nginx nginx -s reload 