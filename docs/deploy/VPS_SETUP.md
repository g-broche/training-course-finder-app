# VPS Setup & Deployment Guide

## 1. Connect to the VPS

```bash
ssh root@<vps_ip_address>
```

You will be prompted for the root password: `<vps_root_password>`

> After initial setup it is recommended to configure SSH key authentication and disable password login.

---

## 2. Install Prerequisites

Run the following commands once on a fresh VPS (assumes a Debian/Ubuntu distribution).

### Update package index

```bash
apt update && apt upgrade -y
```

### Install packages

```bash
sudo apt-get install -y ca-certificates curl gnupg lsb-release
```

### Install Docker

```bash
sudo mkdir -p /etc/apt/keyrings curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu jammy stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null”
```

```bash
sudo apt-get install -y docker-ce docker-ce-cli containerd.io
```

### Verify Docker and the Compose plugin are available

```bash
docker --version
docker compose version
```

---

## 3. First-Time Project Setup

### Create the application directory

```bash
mkdir -p /opt/backend
```

### Clone the repository into /opt/backend/current

```bash
git clone <repository_url> /opt/backend/current
```

### Create and fill in the environment file

```bash
cp /opt/backend/current/.env.example /opt/backend/current/.env
nano /opt/backend/current/.env
```

> Fill in all required values: `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_PORT`, `API_PORT`, `ENV_PROFILE`, `API_DOMAIN`, `ALLOWED_ORIGINS`.

### Upload the deploy and rollback scripts to the VPS

From your **local machine**, run:

```bash
scp deploy.sh rollback.sh root@<vps_ip_address>:/opt/backend/
```

### Make the scripts executable

Back on the VPS:

```bash
chmod +x /opt/backend/deploy.sh /opt/backend/rollback.sh
```

---

## 4. Deploy

```bash
bash /opt/backend/deploy.sh
```

Logs are saved to `/opt/backend/logs/deploy-<timestamp>.log`.

---

## 5. Manual Rollback (if needed after a deploy)

```bash
bash /opt/backend/rollback.sh
```

Logs are saved to `/opt/backend/logs/rollback-<timestamp>.log`.
