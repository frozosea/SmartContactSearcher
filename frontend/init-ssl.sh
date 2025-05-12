#!/bin/bash

# Replace with your domain
DOMAIN="your-domain.com"
EMAIL="your-email@example.com"

# Install certbot
apt-get update
apt-get install -y certbot python3-certbot-nginx

# Generate SSL certificate
certbot --nginx \
  --non-interactive \
  --agree-tos \
  --email $EMAIL \
  --domains $DOMAIN \
  --redirect

# Create directory for SSL certificates
mkdir -p /etc/nginx/ssl

# Copy certificates to the correct location
cp /etc/letsencrypt/live/$DOMAIN/fullchain.pem /etc/nginx/ssl/
cp /etc/letsencrypt/live/$DOMAIN/privkey.pem /etc/nginx/ssl/

# Set proper permissions
chmod 600 /etc/nginx/ssl/* 