#!/usr/bin/env bash

set -u

# Chemins mis à jour pour la racine
PROJECT_ROOT="/c/Perso/projet_dev_perso/nemia-hub"
SRC_DIR="$PROJECT_ROOT"
CONFIG_FILE="$PROJECT_ROOT/.prettierrc"
PRETTIER_BIN="$PROJECT_ROOT/node_modules/.bin/prettier"
HEARTBEAT_INTERVAL=12

# Fichier de log des erreurs
ERROR_LOG="$(mktemp /tmp/prettier-errors-XXXXXX.log)"

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

log_detail() {
  printf '[%s] [ERR] %s\n' "$(timestamp)" "$1" >&2
  printf '[%s] [ERR] %s\n' "$(timestamp)" "$1" >> "$ERROR_LOG"
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
  # On ignore node_modules, target (Java), dist (Angular) et .git
  find "$SRC_DIR" -type d \( -path "*/node_modules" -o -path "*/target" -o -path "*/dist" -o -path "*/.git" \) -prune -o \
  -type f \( \
    -name "*.java" -o \
    -name "*.ts" -o \
    -name "*.html" -o \
    -name "*.scss" -o \
    -name "*.json" -o \
    -name "*.xml" -o \
    -name "*.md" \
  \) -print | sort
}

check_files() {
  local file
  local check_exit
  local err_out

  BAD_COUNT=0
  CHECK_ERROR_COUNT=0

  while IFS= read -r file; do
    [ -z "$file" ] && continue
    CHECKED_COUNT=$((CHECKED_COUNT + 1))

    # --- CORRECTION ICI : Pas de --write pendant le --check ---
    err_out=$( \
      "$PRETTIER_BIN" "$file" \
        --config "$CONFIG_FILE" \
        --plugin prettier-plugin-java \
        --plugin @prettier/plugin-xml \
        --check 2>&1 >/dev/null \
    )
    check_exit=$?

    case "$check_exit" in
      0) ;;
      1)
        BAD_COUNT=$((BAD_COUNT + 1))
        printf '%s\n' "$file" >> "$TMP_BAD"
        ;;
      *)
        CHECK_ERROR_COUNT=$((CHECK_ERROR_COUNT + 1))
        printf '%s\n' "$file" >> "$TMP_BAD"
        log_detail "check (exit=$check_exit) : $file"
        if [ -n "$err_out" ]; then
          printf '%s\n' "$err_out" | while IFS= read -r line; do
            log_detail "  >> $line"
          done
        fi
        ;;
    esac
  done < "$TMP_FILES"
}

format_files() {
  local file
  local write_exit
  local fmt_err

  FORMATTED_COUNT=0
  FORMAT_ERROR_COUNT=0

  while IFS= read -r file; do
    [ -z "$file" ] && continue

    fmt_err=$( \
      "$PRETTIER_BIN" "$file" \
        --config "$CONFIG_FILE" \
        --plugin prettier-plugin-java \
        --plugin @prettier/plugin-xml \
        --write 2>&1 >/dev/null \
    )
    write_exit=$?

    if [ "$write_exit" -eq 0 ]; then
      FORMATTED_COUNT=$((FORMATTED_COUNT + 1))
    else
      FORMAT_ERROR_COUNT=$((FORMAT_ERROR_COUNT + 1))
      log_detail "format (exit=$write_exit) : $file"
      if [ -n "$fmt_err" ]; then
        printf '%s\n' "$fmt_err" | while IFS= read -r line; do
          log_detail "  >> $line"
        done
      fi
    fi
  done < "$TMP_BAD"
}

# ── Vérifications préalables ───────────────────────────────────────────────────

require_cmd find
require_cmd mktemp
require_cmd sort
require_cmd wc
require_cmd tr

require_path "$PROJECT_ROOT"
require_path "$CONFIG_FILE"
require_path "$PRETTIER_BIN"

cd "$PROJECT_ROOT" || exit 1

TMP_FILES="$(mktemp)"
TMP_BAD="$(mktemp)"

cleanup() {
  stop_heartbeat
  rm -f "$TMP_FILES" "$TMP_BAD"
  if [ -s "$ERROR_LOG" ]; then
    log_info "Log des erreurs complet : $ERROR_LOG"
  else
    rm -f "$ERROR_LOG"
  fi
}
trap cleanup EXIT

# ── Collecte ───────────────────────────────────────────────────────────────────

collect_files > "$TMP_FILES"
TOTAL_FILES="$(wc -l < "$TMP_FILES" | tr -d '[:space:]')"

log_info "Début analyse"
log_info "Fichiers candidats: $TOTAL_FILES"

if [ "$TOTAL_FILES" -eq 0 ]; then
  log_warn "Aucun fichier candidat trouvé."
  exit 0
fi

# ── Vérification plugin Java ───────────────────────────────────────────────────
log_info "Vérification prettier-plugin-java..."
plugin_check_err=$(
  "$PRETTIER_BIN" --plugin prettier-plugin-java --version 2>&1 >/dev/null
)
plugin_check_exit=$?
if [ $plugin_check_exit -ne 0 ]; then
  log_err "prettier-plugin-java inaccessible."
  log_err "Fix : cd $PROJECT_ROOT && npm install --save-dev prettier-plugin-java"
  exit 1
fi
log_ok "prettier-plugin-java OK"


# ── Vérification plugin XML ────────────────────────────────────────────────────
log_info "Vérification @prettier/plugin-xml..."
plugin_xml_check_err=$(
  "$PRETTIER_BIN" --plugin @prettier/plugin-xml --version 2>&1 >/dev/null
)
plugin_xml_check_exit=$?
if [ $plugin_xml_check_exit -ne 0 ]; then
  log_err "@prettier/plugin-xml inaccessible."
  log_err "Fix : cd $PROJECT_ROOT && npm install --save-dev @prettier/plugin-xml"
  exit 1
fi
log_ok "@prettier/plugin-xml OK"

# ── Check ──────────────────────────────────────────────────────────────────────

CHECKED_COUNT=0
start_heartbeat "Check"
check_files
stop_heartbeat

log_info "Fin check"
log_info "Résumé : analysés=$CHECKED_COUNT, à corriger=$BAD_COUNT, erreurs=$CHECK_ERROR_COUNT"

if [ "$BAD_COUNT" -eq 0 ]; then
  log_ok "Tout est déjà bien formaté."
  exit 0
fi

# ── Format ─────────────────────────────────────────────────────────────────────

log_info "Début reformatage de $BAD_COUNT fichiers..."
start_heartbeat "Format"
format_files
stop_heartbeat

log_info "Fin format"
log_info "Résumé : corrigés=$FORMATTED_COUNT, erreurs=$FORMAT_ERROR_COUNT"

if [ "$FORMAT_ERROR_COUNT" -eq 0 ]; then
  log_ok "Reformatage terminé avec succès."
  exit 0
fi

log_err "Terminé avec $FORMAT_ERROR_COUNT erreur(s) — voir log : $ERROR_LOG"
exit 1