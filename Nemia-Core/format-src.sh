# file: C:/Perso/projet_dev_perso/nemia-hub/Nemia-Core/format-src.sh

#!/usr/bin/env bash

set -u

PROJECT_ROOT="/c/Perso/projet_dev_perso/nemia-hub"
SRC_DIR="$PROJECT_ROOT/Nemia-Core/src"
CONFIG_FILE="/c/Perso/projet_dev_perso/nemia-hub/.prettierrc"
PRETTIER_BIN="$PROJECT_ROOT/Nemia-View/node_modules/.bin/prettier"  
HEARTBEAT_INTERVAL=15

timestamp() {
  date +"%Y-%m-%d %H:%M:%S"
}

log_info() {
  printf '[%s] [INFO] %s\n' "$(timestamp)" "$1"
}

log_warn() {
  printf '[%s] [WARN] %s\n' "$(timestamp)" "$1"
}

log_ok() {
  printf '[%s] [OK] %s\n' "$(timestamp)" "$1"
}

log_err() {
  printf '[%s] [ERR] %s\n' "$(timestamp)" "$1" >&2
}

require_path() {
  [ -e "$1" ] || {
    log_err "Introuvable: $1"
    exit 1
  }
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    log_err "Commande introuvable: $1"
    exit 1
  }
}

start_heartbeat() {
  local label="$1"
  (
    while true; do
      sleep "$HEARTBEAT_INTERVAL"
      printf '[%s] [INFO] %s en cours...\n' "$(timestamp)" "$label"
    done
  ) &
  HEARTBEAT_PID=$!
}

stop_heartbeat() {
  if [ -n "${HEARTBEAT_PID:-}" ]; then
    kill "$HEARTBEAT_PID" >/dev/null 2>&1 || true
    wait "$HEARTBEAT_PID" 2>/dev/null || true
    unset HEARTBEAT_PID
  fi
}

collect_files() {
  find "$SRC_DIR" -type f \( \
    -name "*.java" -o \
    -name "*.xml" -o \
    -name "*.js" -o \
    -name "*.jsx" -o \
    -name "*.ts" -o \
    -name "*.tsx" -o \
    -name "*.json" -o \
    -name "*.css" -o \
    -name "*.scss" -o \
    -name "*.less" -o \
    -name "*.html" -o \
    -name "*.md" -o \
    -name "*.mdx" -o \
    -name "*.yml" -o \
    -name "*.yaml" -o \
    -name "*.graphql" -o \
    -name "*.gql" \
  \) | sort
}

check_files() {
  local file
  local check_exit=0

  BAD_COUNT=0
  CHECK_ERROR_COUNT=0

  while IFS= read -r file; do
    [ -z "$file" ] && continue

    CHECKED_COUNT=$((CHECKED_COUNT + 1))

    "$PRETTIER_BIN" "$file" \
      --config "$CONFIG_FILE" \
      --plugin prettier-plugin-java \
      --check >/dev/null 2>&1

    check_exit=$?

    case "$check_exit" in
      0)
        ;;
      1)
        BAD_COUNT=$((BAD_COUNT + 1))
        printf '%s\n' "$file" >> "$TMP_BAD"
        ;;
      *)
        CHECK_ERROR_COUNT=$((CHECK_ERROR_COUNT + 1))
        printf '%s\n' "$file" >> "$TMP_BAD"
        ;;
    esac
  done < "$TMP_FILES"
}

format_files() {
  local file
  local write_exit=0

  FORMATTED_COUNT=0
  FORMAT_ERROR_COUNT=0

  while IFS= read -r file; do
    [ -z "$file" ] && continue

    "$PRETTIER_BIN" "$file" \
      --config "$CONFIG_FILE" \
      --plugin prettier-plugin-java \
      --write >/dev/null 2>&1

    write_exit=$?

    if [ "$write_exit" -eq 0 ]; then
      FORMATTED_COUNT=$((FORMATTED_COUNT + 1))
    else
      FORMAT_ERROR_COUNT=$((FORMAT_ERROR_COUNT + 1))
    fi
  done < "$TMP_BAD"
}

require_cmd find
require_cmd mktemp
require_cmd sort
require_cmd wc
require_cmd tr

require_path "$PROJECT_ROOT"
require_path "$SRC_DIR"
require_path "$CONFIG_FILE"
require_path "$PRETTIER_BIN"

cd "$PROJECT_ROOT" || exit 1

TMP_FILES="$(mktemp)"
TMP_BAD="$(mktemp)"

cleanup() {
  stop_heartbeat
  rm -f "$TMP_FILES" "$TMP_BAD"
}
trap cleanup EXIT

collect_files > "$TMP_FILES"

TOTAL_FILES="$(wc -l < "$TMP_FILES" | tr -d '[:space:]')"
CHECKED_COUNT=0
BAD_COUNT=0
CHECK_ERROR_COUNT=0
FORMATTED_COUNT=0
FORMAT_ERROR_COUNT=0

log_info "Début analyse"
log_info "Dossier source : $SRC_DIR"
log_info "Config Prettier : $CONFIG_FILE"
log_info "Prettier binaire : $PRETTIER_BIN"
log_info "Fichiers candidats : $TOTAL_FILES"

if [ "$TOTAL_FILES" -eq 0 ]; then
  log_warn "Aucun fichier candidat trouvé."
  exit 0
fi

start_heartbeat "Check"
check_files
stop_heartbeat

log_info "Fin check"
log_info "Résumé check : analysés=$CHECKED_COUNT, à corriger=$BAD_COUNT, erreurs=$CHECK_ERROR_COUNT"

if [ "$BAD_COUNT" -eq 0 ]; then
  log_ok "Aucun fichier mal formaté trouvé."
  exit 0
fi

log_info "Début format"
log_info "Fichiers à corriger : $BAD_COUNT"

start_heartbeat "Format"
format_files
stop_heartbeat

log_info "Fin format"
log_info "Résumé format : corrigés=$FORMATTED_COUNT, erreurs=$FORMAT_ERROR_COUNT"

if [ "$FORMAT_ERROR_COUNT" -eq 0 ]; then
  log_ok "Reformatage terminé avec succès."
  exit 0
fi

log_err "Reformatage terminé avec erreurs."
exit 1