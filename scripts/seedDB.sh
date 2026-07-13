#!/usr/bin/env bash
# Peuplement DB mise en situation — 1er janvier 2026 au 6 mai 2026
# Prérequis : base vide, backend sur localhost:8080


set -u

# ── Mode (dev | prod) ─────────────────────────────────────────────────

if [[ $# -ne 1 || ( "$1" != "dev" && "$1" != "prod" ) ]]; then
  echo "Usage: ./seedDB.sh [dev|prod]"
  exit 1
fi

MODE="$1"
TOKEN=""

if [[ "$MODE" == "dev" ]]; then
  BASE_URL="http://localhost:8080/api"
else
  BASE_URL="https://nemiahub.eu/api"
  echo -n "Mot de passe admin : "
  read -rs ADMIN_PASSWORD
  echo

  LOGIN_RESPONSE=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" \
    -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -d "{\"username\":\"admin\",\"password\":\"$ADMIN_PASSWORD\"}")

  LOGIN_BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')
  LOGIN_STATUS=$(echo "$LOGIN_RESPONSE" | tail -n 1 | sed 's/HTTP_STATUS://')

  if [[ "$LOGIN_STATUS" != "200" ]]; then
    echo "Echec de l'authentification (HTTP $LOGIN_STATUS)"
    exit 1
  fi

  TOKEN=$(echo "$LOGIN_BODY" | grep -o '"token":"[^"]*"' | sed 's/"token":"//;s/"//')
  if [[ -z "$TOKEN" ]]; then
    echo "Token JWT introuvable dans la reponse de login"
    exit 1
  fi
  echo "Authentification reussie."
fi

FLUX_URL="$BASE_URL/flux"
BIENS_URL="$BASE_URL/biens"
EXERCICES_URL="$BASE_URL/exercices"

# ── Helpers ──────────────────────────────────────────────────────────

post_json() {
  local label="$1"
  local url="$2"
  local payload="$3"

  echo "--------------------------------------------------"
  echo "Insertion: $label"

  local auth_header=()
  if [[ -n "$TOKEN" ]]; then
    auth_header=(-H "Authorization: Bearer $TOKEN")
  fi

  local response
  response=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" \
    -X POST "$url" \
    -H "Content-Type: application/json; charset=UTF-8" \
    "${auth_header[@]}" \
    -d "$payload")

  local body
  body=$(echo "$response" | sed '$d')

  local status
  status=$(echo "$response" | tail -n 1 | sed 's/HTTP_STATUS://')

  echo "HTTP status: $status"
  echo "Response: $body"

  if [[ "$status" != "200" && "$status" != "201" ]]; then
    echo "ERREUR sur: $label"
    exit 1
  fi

  echo "$body"
}

extract_id() {
  echo "$1" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*'
}

# ══════════════════════════════════════════════════════════════════════
# Bien de référence
# ══════════════════════════════════════════════════════════════════════

echo "=================================================="
echo "Creation du bien de reference"
echo "=================================================="

BIEN_RESPONSE=$(post_json "Bien Studio Bordeaux Victoire" "$BIENS_URL" '{
  "nomUsuel": "Studio Bordeaux Victoire",
  "adresseSimplifiee": "12 place de la Victoire, 33000 Bordeaux",
  "statutActivite": "ACTIF",
  "typeLocation": "LMNP_LONGUE_DUREE",
  "regimeVise": "REEL",
  "commentaire": "Studio meuble 25m2 - mise en situation 2026"
}')

BIEN_ID=$(extract_id "$BIEN_RESPONSE")
echo "Bien cree avec id: $BIEN_ID"

# ══════════════════════════════════════════════════════════════════════
# Exercice 2026
# ══════════════════════════════════════════════════════════════════════

echo "=================================================="
echo "Creation de l exercice 2026"
echo "=================================================="

EXERCICE_RESPONSE=$(post_json "Exercice 2026" "$EXERCICES_URL" '{
  "libelleExercice": "2026",
  "dateDebut": "2026-01-01",
  "dateFin": "2026-12-31",
  "statutExercice": "OUVERT",
  "niveauCompletude": "FAIBLE",
  "commentaire": "Exercice de mise en situation Jan-Mai 2026"
}')

EXERCICE_ID=$(extract_id "$EXERCICE_RESPONSE")
echo "Exercice cree avec id: $EXERCICE_ID"

# ══════════════════════════════════════════════════════════════════════
# JANVIER 2026
# ══════════════════════════════════════════════════════════════════════

echo "=================================================="
echo "JANVIER 2026"
echo "=================================================="

post_json "Apport initial compte activite" "$FLUX_URL" "{
  \"date\": \"2026-01-01\",
  \"type\": \"MOUVEMENT_FINANCIER\",
  \"libelle\": \"Apport initial compte activite\",
  \"montant\": 5000.00,
  \"categorie\": \"APPORT\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"HORS_RESULTAT\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Mise de fonds initiale activite LMNP\"
}"

post_json "Loyer janvier 2026" "$FLUX_URL" "{
  \"date\": \"2026-01-02\",
  \"type\": \"RECETTE\",
  \"libelle\": \"Loyer janvier 2026\",
  \"montant\": 650.00,
  \"categorie\": \"LOYER\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"NON_APPLICABLE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Locataire Martin - mois janvier\"
}"

post_json "Charges copropriete janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-03\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Charges copropriete janvier 2026\",
  \"montant\": 120.50,
  \"categorie\": \"COPROPRIETE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Appel de charges syndic janvier\"
}"

post_json "Interets emprunt janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Interets emprunt janvier 2026\",
  \"montant\": 210.40,
  \"categorie\": \"EMPRUNT_INTERETS\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Credit immobilier BNP - part interets\"
}"

post_json "Capital emprunt janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-05\",
  \"type\": \"MOUVEMENT_FINANCIER\",
  \"libelle\": \"Remboursement capital emprunt janvier 2026\",
  \"montant\": 540.00,
  \"categorie\": \"EMPRUNT_CAPITAL\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"HORS_RESULTAT\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Part capital mensualite BNP\"
}"

post_json "Assurance emprunt janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Assurance emprunt janvier 2026\",
  \"montant\": 28.00,
  \"categorie\": \"EMPRUNT_ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"ADI Credit Agricole\"
}"

post_json "Electricite janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-08\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture electricite janvier 2026\",
  \"montant\": 72.30,
  \"categorie\": \"ELECTRICITE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"EDF janvier - consommation hivernale elevee\"
}"

post_json "Eau janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture eau janvier 2026\",
  \"montant\": 28.50,
  \"categorie\": \"EAU\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Veolia janvier\"
}"

post_json "Internet logement janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Internet logement janvier 2026\",
  \"montant\": 29.99,
  \"categorie\": \"INTERNET\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Box fibre Orange\"
}"

post_json "Assurance PNO janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Assurance PNO janvier 2026\",
  \"montant\": 18.75,
  \"categorie\": \"ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"MRH proprietaire non occupant - Allianz\"
}"

post_json "GLI janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Garantie loyers impayes janvier 2026\",
  \"montant\": 32.00,
  \"categorie\": \"ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"GLI Garantme - mensualite\"
}"

post_json "Achat matelas chambre" "$FLUX_URL" "{
  \"date\": \"2026-01-12\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Achat matelas chambre\",
  \"montant\": 290.00,
  \"categorie\": \"MOBILIER\",
  \"modePaiement\": \"CARTE\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"IMMOBILISATION\",
  \"statutTraitement\": \"QUALIFIE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Bultex - mobilier meuble LMNP\"
}"

post_json "Frais bancaires janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-15\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Frais bancaires tenue de compte janvier\",
  \"montant\": 7.50,
  \"categorie\": \"FRAIS_BANCAIRES\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Releve bancaire janvier\"
}"

post_json "Honoraires gestion janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-20\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Honoraires agence gestion janvier 2026\",
  \"montant\": 65.00,
  \"categorie\": \"HONORAIRES\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Agence Victoire Immobilier - frais gestion\"
}"

post_json "Provision taxe fonciere janvier" "$FLUX_URL" "{
  \"date\": \"2026-01-25\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Provision mensuelle taxe fonciere janvier\",
  \"montant\": 95.00,
  \"categorie\": \"TAXE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"QUALIFIE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Provision 1/12 taxe fonciere 1140 EUR/an\"
}"

# ══════════════════════════════════════════════════════════════════════
# FEVRIER 2026
# ══════════════════════════════════════════════════════════════════════

echo "=================================================="
echo "FEVRIER 2026"
echo "=================================================="

post_json "Loyer fevrier 2026" "$FLUX_URL" "{
  \"date\": \"2026-02-01\",
  \"type\": \"RECETTE\",
  \"libelle\": \"Loyer fevrier 2026\",
  \"montant\": 650.00,
  \"categorie\": \"LOYER\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"NON_APPLICABLE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Locataire Martin - mois fevrier\"
}"

post_json "Charges copropriete fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-03\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Charges copropriete fevrier 2026\",
  \"montant\": 120.50,
  \"categorie\": \"COPROPRIETE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Appel de charges syndic fevrier\"
}"

post_json "Interets emprunt fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Interets emprunt fevrier 2026\",
  \"montant\": 209.80,
  \"categorie\": \"EMPRUNT_INTERETS\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Credit immobilier BNP - part interets\"
}"

post_json "Capital emprunt fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-05\",
  \"type\": \"MOUVEMENT_FINANCIER\",
  \"libelle\": \"Remboursement capital emprunt fevrier 2026\",
  \"montant\": 540.60,
  \"categorie\": \"EMPRUNT_CAPITAL\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"HORS_RESULTAT\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Part capital mensualite BNP\"
}"

post_json "Assurance emprunt fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Assurance emprunt fevrier 2026\",
  \"montant\": 28.00,
  \"categorie\": \"EMPRUNT_ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"ADI Credit Agricole\"
}"

post_json "Electricite fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-07\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture electricite fevrier 2026\",
  \"montant\": 68.90,
  \"categorie\": \"ELECTRICITE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"EDF fevrier\"
}"

post_json "Eau fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-08\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture eau fevrier 2026\",
  \"montant\": 22.40,
  \"categorie\": \"EAU\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Veolia fevrier\"
}"

post_json "Internet logement fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Internet logement fevrier 2026\",
  \"montant\": 29.99,
  \"categorie\": \"INTERNET\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Box fibre Orange\"
}"

post_json "Assurance PNO fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Assurance PNO fevrier 2026\",
  \"montant\": 18.75,
  \"categorie\": \"ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"MRH proprietaire non occupant - Allianz\"
}"

post_json "GLI fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Garantie loyers impayes fevrier 2026\",
  \"montant\": 32.00,
  \"categorie\": \"ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"GLI Garantme - mensualite\"
}"

post_json "Travaux peinture salon" "$FLUX_URL" "{
  \"date\": \"2026-02-12\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Travaux peinture salon\",
  \"montant\": 450.00,
  \"categorie\": \"TRAVAUX\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"A_ARBITRER\",
  \"statutTraitement\": \"A_REVOIR\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Artisan Dupont - facture attendue - charge ou amortissement ?\"
}"

post_json "Frais bancaires fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-15\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Frais bancaires tenue de compte fevrier\",
  \"montant\": 7.50,
  \"categorie\": \"FRAIS_BANCAIRES\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Releve bancaire fevrier\"
}"

post_json "Regularisation charges locataire 2025" "$FLUX_URL" "{
  \"date\": \"2026-02-18\",
  \"type\": \"RECETTE\",
  \"libelle\": \"Regularisation charges locataire 2025\",
  \"montant\": 145.00,
  \"categorie\": \"CHARGES_REFACTUREES\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"NON_APPLICABLE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Regularisation annuelle charges 2025 - solde locataire\"
}"

post_json "Honoraires gestion fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-20\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Honoraires agence gestion fevrier 2026\",
  \"montant\": 65.00,
  \"categorie\": \"HONORAIRES\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Agence Victoire Immobilier - frais gestion\"
}"

post_json "Achat micro-ondes" "$FLUX_URL" "{
  \"date\": \"2026-02-22\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Achat micro-ondes encastrable\",
  \"montant\": 189.00,
  \"categorie\": \"ELECTROMENAGER\",
  \"modePaiement\": \"CARTE\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"IMMOBILISATION\",
  \"statutTraitement\": \"QUALIFIE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Darty - electromenager cuisine meublee\"
}"

post_json "Provision taxe fonciere fevrier" "$FLUX_URL" "{
  \"date\": \"2026-02-25\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Provision mensuelle taxe fonciere fevrier\",
  \"montant\": 95.00,
  \"categorie\": \"TAXE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"QUALIFIE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Provision 1/12 taxe fonciere 1140 EUR/an\"
}"

# ══════════════════════════════════════════════════════════════════════
# MARS 2026
# ══════════════════════════════════════════════════════════════════════

echo "=================================================="
echo "MARS 2026"
echo "=================================================="

post_json "Loyer mars 2026" "$FLUX_URL" "{
  \"date\": \"2026-03-03\",
  \"type\": \"RECETTE\",
  \"libelle\": \"Loyer mars 2026\",
  \"montant\": 650.00,
  \"categorie\": \"LOYER\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"NON_APPLICABLE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Locataire Martin - virement recu avec 2j de retard\"
}"

post_json "Charges copropriete mars" "$FLUX_URL" "{
  \"date\": \"2026-03-04\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Charges copropriete mars 2026\",
  \"montant\": 120.50,
  \"categorie\": \"COPROPRIETE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Appel de charges syndic mars\"
}"

post_json "Interets emprunt mars" "$FLUX_URL" "{
  \"date\": \"2026-03-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Interets emprunt mars 2026\",
  \"montant\": 209.20,
  \"categorie\": \"EMPRUNT_INTERETS\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Credit immobilier BNP - part interets\"
}"

post_json "Capital emprunt mars" "$FLUX_URL" "{
  \"date\": \"2026-03-05\",
  \"type\": \"MOUVEMENT_FINANCIER\",
  \"libelle\": \"Remboursement capital emprunt mars 2026\",
  \"montant\": 541.20,
  \"categorie\": \"EMPRUNT_CAPITAL\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"HORS_RESULTAT\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Part capital mensualite BNP\"
}"

post_json "Assurance emprunt mars" "$FLUX_URL" "{
  \"date\": \"2026-03-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Assurance emprunt mars 2026\",
  \"montant\": 28.00,
  \"categorie\": \"EMPRUNT_ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"ADI Credit Agricole\"
}"

post_json "Electricite mars" "$FLUX_URL" "{
  \"date\": \"2026-03-07\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture electricite mars 2026\",
  \"montant\": 55.40,
  \"categorie\": \"ELECTRICITE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"EDF mars - baisse consommation printemps\"
}"

post_json "Eau mars" "$FLUX_URL" "{
  \"date\": \"2026-03-08\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture eau mars 2026\",
  \"montant\": 22.40,
  \"categorie\": \"EAU\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Veolia mars\"
}"

post_json "Internet logement mars" "$FLUX_URL" "{
  \"date\": \"2026-03-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Internet logement mars 2026\",
  \"montant\": 29.99,
  \"categorie\": \"INTERNET\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Box fibre Orange\"
}"

post_json "Assurance PNO mars" "$FLUX_URL" "{
  \"date\": \"2026-03-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Assurance PNO mars 2026\",
  \"montant\": 18.75,
  \"categorie\": \"ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"MRH proprietaire non occupant - Allianz\"
}"

post_json "GLI mars" "$FLUX_URL" "{
  \"date\": \"2026-03-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Garantie loyers impayes mars 2026\",
  \"montant\": 32.00,
  \"categorie\": \"ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"GLI Garantme - mensualite\"
}"

post_json "Frais de notaire refinancement" "$FLUX_URL" "{
  \"date\": \"2026-03-12\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Frais de notaire refinancement credit\",
  \"montant\": 1800.00,
  \"categorie\": \"FRAIS_FINANCEMENT\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"A_REVOIR\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Frais liés au refinancement - deductibilite a confirmer avec comptable\"
}"

post_json "Achat table basse salon" "$FLUX_URL" "{
  \"date\": \"2026-03-14\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Achat table basse salon\",
  \"montant\": 149.00,
  \"categorie\": \"MOBILIER\",
  \"modePaiement\": \"CARTE\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"INCOMPLET\",
  \"qualificationPressentie\": \"A_ARBITRER\",
  \"statutTraitement\": \"A_REVOIR\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Maisons du Monde - ticket de caisse seul, facture a demander\"
}"

post_json "Frais bancaires mars" "$FLUX_URL" "{
  \"date\": \"2026-03-15\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Frais bancaires tenue de compte mars\",
  \"montant\": 7.50,
  \"categorie\": \"FRAIS_BANCAIRES\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Releve bancaire mars\"
}"

post_json "Menage entre locataires" "$FLUX_URL" "{
  \"date\": \"2026-03-16\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Prestation menage entre locataires\",
  \"montant\": 80.00,
  \"categorie\": \"MENAGE\",
  \"modePaiement\": \"ESPECES\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Prestataire independant - recu especes a obtenir\"
}"

post_json "Honoraires gestion mars" "$FLUX_URL" "{
  \"date\": \"2026-03-20\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Honoraires agence gestion mars 2026\",
  \"montant\": 65.00,
  \"categorie\": \"HONORAIRES\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Agence Victoire Immobilier - frais gestion\"
}"

post_json "Decoration murale tableau" "$FLUX_URL" "{
  \"date\": \"2026-03-22\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Achat decoration murale - tableaux\",
  \"montant\": 65.00,
  \"categorie\": \"DECORATION\",
  \"modePaiement\": \"CARTE\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"MIXTE_OU_A_VENTILER\",
  \"statutTraitement\": \"A_REVOIR\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Achat personnel partiellement affecte au bien - ventilation a faire\"
}"

post_json "Provision taxe fonciere mars" "$FLUX_URL" "{
  \"date\": \"2026-03-25\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Provision mensuelle taxe fonciere mars\",
  \"montant\": 95.00,
  \"categorie\": \"TAXE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"QUALIFIE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Provision 1/12 taxe fonciere 1140 EUR/an\"
}"

post_json "Petits consommables mars" "$FLUX_URL" "{
  \"date\": \"2026-03-28\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Consommables entretien - ampoules et produits\",
  \"montant\": 18.60,
  \"categorie\": \"CONSOMMABLES\",
  \"modePaiement\": \"CARTE\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Ampoules LED et produits nettoyage\"
}"

post_json "Regularisation taxe fonciere 2025" "$FLUX_URL" "{
  \"date\": \"2026-03-30\",
  \"type\": \"REGULARISATION\",
  \"libelle\": \"Regularisation taxe fonciere 2025\",
  \"montant\": 180.00,
  \"categorie\": \"REGULARISATION\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Ajustement provisions vs montant reel taxe fonciere 2025\"
}"

# ══════════════════════════════════════════════════════════════════════
# AVRIL 2026
# ══════════════════════════════════════════════════════════════════════

echo "=================================================="
echo "AVRIL 2026"
echo "=================================================="

post_json "Loyer avril 2026" "$FLUX_URL" "{
  \"date\": \"2026-04-01\",
  \"type\": \"RECETTE\",
  \"libelle\": \"Loyer avril 2026\",
  \"montant\": 650.00,
  \"categorie\": \"LOYER\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"NON_APPLICABLE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Locataire Martin avril\"
}"

post_json "Charges copropriete T2 2026" "$FLUX_URL" "{
  \"date\": \"2026-04-02\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Charges copropriete T2 2026\",
  \"montant\": 120.50,
  \"categorie\": \"COPROPRIETE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Appel de charges syndic - trimestre 2\"
}"

post_json "Facture electricite avril" "$FLUX_URL" "{
  \"date\": \"2026-04-03\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture electricite avril 2026\",
  \"montant\": 48.20,
  \"categorie\": \"ELECTRICITE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"EDF avril 2026\"
}"

post_json "Eau avril" "$FLUX_URL" "{
  \"date\": \"2026-04-04\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture eau avril 2026\",
  \"montant\": 22.90,
  \"categorie\": \"EAU\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Veolia avril\"
}"

post_json "Interets emprunt avril" "$FLUX_URL" "{
  \"date\": \"2026-04-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Interets emprunt avril 2026\",
  \"montant\": 208.60,
  \"categorie\": \"EMPRUNT_INTERETS\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Credit immobilier BNP - part interets\"
}"

post_json "Capital emprunt avril" "$FLUX_URL" "{
  \"date\": \"2026-04-05\",
  \"type\": \"MOUVEMENT_FINANCIER\",
  \"libelle\": \"Remboursement capital emprunt avril 2026\",
  \"montant\": 542.00,
  \"categorie\": \"EMPRUNT_CAPITAL\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"HORS_RESULTAT\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Part capital mensualite BNP\"
}"

post_json "Assurance emprunt avril" "$FLUX_URL" "{
  \"date\": \"2026-04-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Assurance emprunt avril 2026\",
  \"montant\": 28.00,
  \"categorie\": \"EMPRUNT_ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"ADI Credit Agricole\"
}"

post_json "Remplacement robinetterie salle de bain" "$FLUX_URL" "{
  \"date\": \"2026-04-07\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Remplacement robinetterie salle de bain\",
  \"montant\": 380.00,
  \"categorie\": \"REPARATION_IMPORTANTE\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"A_ARBITRER\",
  \"statutTraitement\": \"A_REVOIR\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Plombier Legrand - a qualifier charge ou immobilisation\"
}"

post_json "Internet logement avril" "$FLUX_URL" "{
  \"date\": \"2026-04-08\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Internet logement avril 2026\",
  \"montant\": 29.99,
  \"categorie\": \"INTERNET\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Box fibre Orange\"
}"

post_json "Assurance PNO avril" "$FLUX_URL" "{
  \"date\": \"2026-04-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Assurance PNO avril 2026\",
  \"montant\": 18.75,
  \"categorie\": \"ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"MRH proprietaire non occupant - Allianz\"
}"

post_json "GLI avril" "$FLUX_URL" "{
  \"date\": \"2026-04-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Garantie loyers impayes avril 2026\",
  \"montant\": 32.00,
  \"categorie\": \"ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"GLI Garantme - mensualite\"
}"

post_json "Honoraires comptable 2025" "$FLUX_URL" "{
  \"date\": \"2026-04-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Honoraires comptable bilan 2025\",
  \"montant\": 350.00,
  \"categorie\": \"HONORAIRES\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"A_VERIFIER\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"A_REVOIR\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Cabinet Durand - facture a verifier montant\"
}"

post_json "Frais bancaires avril" "$FLUX_URL" "{
  \"date\": \"2026-04-15\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Frais bancaires tenue de compte avril\",
  \"montant\": 7.50,
  \"categorie\": \"FRAIS_BANCAIRES\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Releve bancaire avril\"
}"

post_json "Indemnite assurance degat des eaux" "$FLUX_URL" "{
  \"date\": \"2026-04-15\",
  \"type\": \"RECETTE\",
  \"libelle\": \"Indemnite assurance degat des eaux\",
  \"montant\": 1200.00,
  \"categorie\": \"INDEMNITE_RECUE\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"A_ARBITRER\",
  \"statutTraitement\": \"A_REVOIR\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Sinistre jan 2026 - remboursement assureur - qualification fiscale a confirmer\"
}"

post_json "Achat canape convertible" "$FLUX_URL" "{
  \"date\": \"2026-04-17\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Achat canape convertible\",
  \"montant\": 490.00,
  \"categorie\": \"MOBILIER\",
  \"modePaiement\": \"CARTE\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"IMMOBILISATION\",
  \"statutTraitement\": \"QUALIFIE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Ikea - mobilier meuble - bien classe LMNP\"
}"

post_json "Achat refrigerateur" "$FLUX_URL" "{
  \"date\": \"2026-04-18\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Achat refrigerateur\",
  \"montant\": 340.00,
  \"categorie\": \"ELECTROMENAGER\",
  \"modePaiement\": \"CARTE\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"IMMOBILISATION\",
  \"statutTraitement\": \"QUALIFIE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Beko - electromenager cuisine meublee\"
}"

post_json "Honoraires gestion avril" "$FLUX_URL" "{
  \"date\": \"2026-04-20\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Honoraires agence gestion avril 2026\",
  \"montant\": 65.00,
  \"categorie\": \"HONORAIRES\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Agence Victoire Immobilier - frais gestion\"
}"

post_json "Provision taxe fonciere avril" "$FLUX_URL" "{
  \"date\": \"2026-04-25\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Provision mensuelle taxe fonciere avril\",
  \"montant\": 95.00,
  \"categorie\": \"TAXE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"QUALIFIE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Provision 1/12 taxe fonciere 1140 EUR/an\"
}"

post_json "Achat aspirateur - a qualifier" "$FLUX_URL" "{
  \"date\": \"2026-04-27\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Achat aspirateur balai\",
  \"montant\": 129.00,
  \"categorie\": \"EQUIPEMENT\",
  \"modePaiement\": \"CARTE\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"A_ARBITRER\",
  \"statutTraitement\": \"A_REVOIR\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Seuil 500 EUR non atteint - charge ou immobilisation a trancher\"
}"

post_json "Retrait tresorerie proprietaire" "$FLUX_URL" "{
  \"date\": \"2026-04-28\",
  \"type\": \"MOUVEMENT_FINANCIER\",
  \"libelle\": \"Retrait tresorerie vers compte personnel\",
  \"montant\": 800.00,
  \"categorie\": \"RETRAIT\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"HORS_RESULTAT\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Virement proprietaire compte personnel\"
}"

# ══════════════════════════════════════════════════════════════════════
# MAI 2026 (1 au 6 mai)
# ══════════════════════════════════════════════════════════════════════

echo "=================================================="
echo "MAI 2026 (1 au 6 mai)"
echo "=================================================="

post_json "Loyer mai 2026" "$FLUX_URL" "{
  \"date\": \"2026-05-01\",
  \"type\": \"RECETTE\",
  \"libelle\": \"Loyer mai 2026\",
  \"montant\": 650.00,
  \"categorie\": \"LOYER\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"NON_APPLICABLE\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Locataire Martin mai - recu, non encore traite\"
}"

post_json "Charges copropriete mai" "$FLUX_URL" "{
  \"date\": \"2026-05-02\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Charges copropriete mai 2026\",
  \"montant\": 120.50,
  \"categorie\": \"COPROPRIETE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Appel syndic - releve a recevoir\"
}"

post_json "Electricite mai" "$FLUX_URL" "{
  \"date\": \"2026-05-03\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture electricite mai 2026\",
  \"montant\": 38.90,
  \"categorie\": \"ELECTRICITE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"EDF mai - en attente de releve\"
}"

post_json "Eau mai" "$FLUX_URL" "{
  \"date\": \"2026-05-04\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture eau mai 2026\",
  \"montant\": 22.40,
  \"categorie\": \"EAU\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Veolia mai\"
}"

post_json "Interets emprunt mai" "$FLUX_URL" "{
  \"date\": \"2026-05-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Interets emprunt mai 2026\",
  \"montant\": 208.00,
  \"categorie\": \"EMPRUNT_INTERETS\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Credit immobilier BNP - releve a recevoir\"
}"

post_json "Capital emprunt mai" "$FLUX_URL" "{
  \"date\": \"2026-05-05\",
  \"type\": \"MOUVEMENT_FINANCIER\",
  \"libelle\": \"Remboursement capital emprunt mai 2026\",
  \"montant\": 542.40,
  \"categorie\": \"EMPRUNT_CAPITAL\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"HORS_RESULTAT\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Part capital mensualite BNP\"
}"

post_json "Assurance emprunt mai" "$FLUX_URL" "{
  \"date\": \"2026-05-05\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Assurance emprunt mai 2026\",
  \"montant\": 28.00,
  \"categorie\": \"EMPRUNT_ASSURANCE\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"ADI Credit Agricole\"
}"

post_json "Internet logement mai" "$FLUX_URL" "{
  \"date\": \"2026-05-06\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Internet logement mai 2026\",
  \"montant\": 29.99,
  \"categorie\": \"INTERNET\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"NON_REQUIS\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Box fibre Orange\"
}"

post_json "Fournitures entretien mai" "$FLUX_URL" "{
  \"date\": \"2026-05-06\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Fournitures entretien logement\",
  \"montant\": 22.80,
  \"categorie\": \"FOURNITURES\",
  \"modePaiement\": \"CARTE\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"BRUT\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Brico depot - visserie et consommables entretien\"
}"

echo "=================================================="
echo "Import termine avec succes."
echo "Bien id: $BIEN_ID"
echo "Exercice id: $EXERCICE_ID"
echo "Total flux inseres: 84"
echo "Periode couverte: 2026-01-01 au 2026-05-06"
echo "=================================================="
