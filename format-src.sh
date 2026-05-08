#!/usr/bin/env bash

set -u

# Chemins mis à jour pour la racine
PROJECT_ROOT="D:/Dev-perso/nemia-hub"
SRC_DIR="$PROJECT_ROOT"
CONFIG_FILE="$PROJECT_ROOT/.prettierrc"
PRETTIER_BIN="$PROJECT_ROOT/node_modules/.bin/prettier"
CACHE_FILE="$PROJECT_ROOT/.prettier-cache"
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
  local check_out

  # Compte total connu dès le départ, plus besoin de boucle pour l'incrémenter
  CHECKED_COUNT=$(wc -l < "$TMP_FILES" | tr -d '[:space:]')
  BAD_COUNT=0
  CHECK_ERROR_COUNT=0

  # xargs passe tous les fichiers en un seul appel Prettier (batch) :
  # un seul démarrage Node.js au lieu de N.
  # 2>&1 >/dev/null : on capture stderr ([warn]/[error]) et on jette stdout.
  check_out=$(
    xargs -d '\n' < "$TMP_FILES" \
      "$PRETTIER_BIN" \
        --config "$CONFIG_FILE" \
        --plugin prettier-plugin-java \
        --plugin @prettier/plugin-xml \
        --cache \
        --cache-location "$CACHE_FILE" \
        --check 2>&1 >/dev/null
  )

  # Prettier émet une ligne "[warn] <chemin>" pour chaque fichier mal formaté.
  # On exclut la ligne de résumé finale "Code style issues found in X files."
  printf '%s\n' "$check_out" \
    | grep '^\[warn\] ' \
    | sed 's/^\[warn\] //' \
    | grep -v '^Code style issues found' \
    > "$TMP_BAD"

  BAD_COUNT=$(wc -l < "$TMP_BAD" | tr -d '[:space:]')

  # Les lignes [error] signalent de vrais problèmes (fichier non parseable, etc.)
  if printf '%s\n' "$check_out" | grep -q '^\[error\]'; then
    CHECK_ERROR_COUNT=1
    printf '%s\n' "$check_out" | grep '^\[error\]' | while IFS= read -r line; do
      log_detail "  >> $line"
    done
  fi
}

format_files() {
  local fmt_out fmt_exit

  FORMATTED_COUNT=0
  FORMAT_ERROR_COUNT=0

  # Même approche batch : tous les fichiers à corriger en un seul appel --write.
  fmt_out=$(
    xargs -d '\n' < "$TMP_BAD" \
      "$PRETTIER_BIN" \
        --config "$CONFIG_FILE" \
        --plugin prettier-plugin-java \
        --plugin @prettier/plugin-xml \
        --cache \
        --cache-location "$CACHE_FILE" \
        --write 2>&1 >/dev/null
  )
  fmt_exit=$?

  if [ "$fmt_exit" -eq 0 ]; then
    FORMATTED_COUNT=$BAD_COUNT
  else
    FORMAT_ERROR_COUNT=1
    log_err "Prettier --write a rencontré des erreurs (exit=$fmt_exit)"
    printf '%s\n' "$fmt_out" | while IFS= read -r line; do
      log_detail "  >> $line"
    done
  fi
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