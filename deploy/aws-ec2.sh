#!/bin/bash
# Simplest AWS deployment: ONE EC2 instance (Ubuntu 22.04/24.04, t3.medium = 4 GB RAM or bigger, because Oracle XE needs memory) running docker compose.
# Security group: allow 22 (your IP only), 80 and 443 (everyone). Do NOT open 3306 or 8080 to the world.
# Run this on the instance after copying the project folder to it (scp -r ecart-pro ubuntu@<ip>:~).
set -e
sudo apt-get update
sudo apt-get install -y docker.io docker-compose-v2 nginx
sudo systemctl enable --now docker
cd ~/ecart-pro
cp -n .env.example .env && echo ">> EDIT .env NOW (passwords!), then re-run this script" && [ ! -f .env.edited ] && nano .env && touch .env.edited
sudo docker compose --env-file .env up -d --build

# nginx reverse proxy on port 80 -> Tomcat on 8080
sudo tee /etc/nginx/sites-available/ecart >/dev/null <<'NGINX'
server {
    listen 80 default_server;
    server_name _;
    client_max_body_size 5m;
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
NGINX
sudo ln -sf /etc/nginx/sites-available/ecart /etc/nginx/sites-enabled/ecart
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl reload nginx
echo "Done. Open http://<your-ec2-public-ip>/"
echo "For HTTPS: point a domain at the instance, then: sudo apt-get install -y certbot python3-certbot-nginx && sudo certbot --nginx -d yourdomain.com"
