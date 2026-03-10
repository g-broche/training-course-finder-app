#!/bin/bash
# =============================================================================
# rollback.sh
# =============================================================================
# Purpose:
#   Manually restore the previous version of the application that was backed
#   up in /app/old during the last deployment.
#   Use this when deploy.sh succeeded but the application misbehaves and you
#   need to revert to the previous known-good state.
#
# Prerequisites (must be present on the VPS):
#   - docker (with the compose plugin, i.e. "docker compose")
#   - curl
#   - A completed prior deployment (i.e. /opt/backend/old must exist)
#
# Usage:
#   bash /opt/backend/rollback.sh
# =============================================================================

set -e

# ─── Paths ───────────────────────────────────────────────────────────────────
APP_DIR="/opt/backend"
CURRENT_DIR="$APP_DIR/current"
OLD_DIR="$APP_DIR/old"
LOG_DIR="$APP_DIR/logs"
LOG_FILE="$LOG_DIR/rollback-$(date +%Y%m%d-%H%M%S).log"

# ─── Health-check settings ───────────────────────────────────────────────────
HEALTH_RETRIES=12
HEALTH_INTERVAL=5

# ─── Helpers ─────────────────────────────────────────────────────────────────
log() { echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"; }

get_env_var() {
  grep -E "^${1}=" "$CURRENT_DIR/.env" 2>/dev/null \
    | head -1 \
    | cut -d'=' -f2- \
    | tr -d '"' \
    | tr -d "'"
}

# ─── Trap ────────────────────────────────────────────────────────────────────
on_error() {
  log "ERROR: Rollback failed at line $LINENO. Manual intervention required."
  exit 1
}

trap on_error ERR

# =============================================================================
# 0. Setup
# =============================================================================
mkdir -p "$LOG_DIR"
log "=== Manual rollback started ==="

# =============================================================================
# 1. Checks
# =============================================================================
if ! docker compose version &>/dev/null; then
  log "ERROR: 'docker compose' plugin is not available."
  exit 1
fi

if [ ! -d "$OLD_DIR" ]; then
  log "ERROR: No backup found at $OLD_DIR. Cannot rollback."
  exit 1
fi

if [ ! -f "$OLD_DIR/.env" ]; then
  log "ERROR: $OLD_DIR/.env is missing. The backup may be corrupted."
  exit 1
fi

log "Backup found at $OLD_DIR."

# =============================================================================
# 2. Stop the current stack
# =============================================================================
log "Stopping current stack ..."
if [ -d "$CURRENT_DIR" ]; then
  cd "$CURRENT_DIR"
  docker compose down --remove-orphans 2>>"$LOG_FILE"
  log "Current stack stopped."
else
  log "WARNING: $CURRENT_DIR does not exist, skipping stop."
fi

# =============================================================================
# 3. Restore the backup
# =============================================================================
log "Restoring previous version from $OLD_DIR to $CURRENT_DIR ..."
rm -rf "$CURRENT_DIR"
cp -r "$OLD_DIR" "$CURRENT_DIR"
log "Restore complete. Reverted to commit: $(cd "$CURRENT_DIR" && git rev-parse --short HEAD 2>/dev/null || echo 'unknown')"

# =============================================================================
# 4. Rebuild and restart the stack from the restored old source
#    (--build is intentional: the new deploy overwrites the image tag, so we
#    must rebuild from the old source code to get the correct image back)
# =============================================================================
log "Rebuilding image from previous source and starting stack ..."
cd "$CURRENT_DIR"
docker compose up --build -d 2>>"$LOG_FILE"
log "Previous stack started."

# =============================================================================
# 5. Health check
# =============================================================================
API_PORT=$(get_env_var "API_PORT")
if [ -z "$API_PORT" ]; then
  log "WARNING: API_PORT not found in .env — defaulting to 8080."
  API_PORT=8080
fi
HEALTH_URL="http://localhost:${API_PORT}/api/health"

log "Waiting for application to become healthy at $HEALTH_URL ..."

for i in $(seq 1 "$HEALTH_RETRIES"); do
  log "Health check attempt $i/$HEALTH_RETRIES ..."
  HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_URL" || echo "000")

  if [ "$HTTP_STATUS" = "200" ]; then
    log "Application is healthy (HTTP $HTTP_STATUS)."
    break
  fi

  if [ "$i" -eq "$HEALTH_RETRIES" ]; then
    log "ERROR: Application did not become healthy after $HEALTH_RETRIES attempts (last status: HTTP $HTTP_STATUS)."
    exit 1
  fi

  log "Not ready yet (HTTP $HTTP_STATUS). Retrying in ${HEALTH_INTERVAL}s ..."
  sleep "$HEALTH_INTERVAL"
done

# =============================================================================
# 6. Done
# =============================================================================
log "=== Rollback successful ==="
