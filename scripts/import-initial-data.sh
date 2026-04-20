#!/usr/bin/env bash

set -u

BASE_URL="http://localhost:8080/api"
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

  local response
  response=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" \
    -X POST "$url" \
    -H "Content-Type: application/json; charset=UTF-8" \
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

# ── Création du bien de référence ────────────────────────────────────

echo "=================================================="
echo "Creation du bien de reference"
echo "=================================================="

BIEN_RESPONSE=$(post_json "Bien Studio Bordeaux Victoire" "$BIENS_URL" '{
  "nomUsuel": "Studio Bordeaux Victoire",
  "adresseSimplifiee": "12 place de la Victoire, 33000 Bordeaux",
  "statutActivite": "ACTIF",
  "typeLocation": "LMNP_LONGUE_DUREE",
  "regimeVise": "REEL",
  "commentaire": "Studio meuble 25m2 - bien de test"
}')

BIEN_ID=$(extract_id "$BIEN_RESPONSE")
echo "Bien cree avec id: $BIEN_ID"

# ── Création de l'exercice de référence ──────────────────────────────

echo "=================================================="
echo "Creation de l exercice de reference"
echo "=================================================="

EXERCICE_RESPONSE=$(post_json "Exercice 2026" "$EXERCICES_URL" '{
  "libelleExercice": "2026",
  "dateDebut": "2026-01-01",
  "dateFin": "2026-12-31",
  "statutExercice": "OUVERT",
  "niveauCompletude": "FAIBLE",
  "commentaire": "Exercice de test"
}')

EXERCICE_ID=$(extract_id "$EXERCICE_RESPONSE")
echo "Exercice cree avec id: $EXERCICE_ID"

# ── Insertion des flux ────────────────────────────────────────────────

echo "=================================================="
echo "Insertion des flux"
echo "=================================================="

# 1. Recette — Loyer
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

# 2. Dépense — Charges copropriété
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
  \"commentaire\": \"Appel de charges syndic\"
}"

# 3. Dépense — Électricité
post_json "Facture electricite avril" "$FLUX_URL" "{
  \"date\": \"2026-04-03\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Facture electricite avril\",
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

# 4. Dépense — Taxe
post_json "Taxe fonciere provision avril" "$FLUX_URL" "{
  \"date\": \"2026-04-04\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Taxe fonciere provision avril\",
  \"montant\": 95.00,
  \"categorie\": \"TAXE\",
  \"modePaiement\": \"CHEQUE\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"A_FOURNIR\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"QUALIFIE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Provision mensuelle taxe fonciere\"
}"

# 5. Dépense — Réparation importante
post_json "Remplacement robinetterie salle de bain" "$FLUX_URL" "{
  \"date\": \"2026-04-05\",
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
  \"commentaire\": \"Plombier - a qualifier charge ou immobilisation\"
}"

# 6. Dépense — Mobilier
post_json "Achat canape convertible" "$FLUX_URL" "{
  \"date\": \"2026-04-06\",
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
  \"commentaire\": \"Ikea - mobilier meuble\"
}"

# 7. Dépense — Emprunt intérêts
post_json "Interets emprunt avril 2026" "$FLUX_URL" "{
  \"date\": \"2026-04-07\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Interets emprunt avril 2026\",
  \"montant\": 210.40,
  \"categorie\": \"EMPRUNT_INTERETS\",
  \"modePaiement\": \"PRELEVEMENT\",
  \"occurrence\": \"RECURRENT\",
  \"statutJustificatif\": \"FOURNI\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"VALIDE\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Credit immobilier BNP\"
}"

# 8. Mouvement financier — Capital emprunt
post_json "Remboursement capital emprunt avril" "$FLUX_URL" "{
  \"date\": \"2026-04-07\",
  \"type\": \"MOUVEMENT_FINANCIER\",
  \"libelle\": \"Remboursement capital emprunt avril\",
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

# 9. Dépense — Honoraires
post_json "Honoraires comptable 2025" "$FLUX_URL" "{
  \"date\": \"2026-04-10\",
  \"type\": \"DEPENSE\",
  \"libelle\": \"Honoraires comptable 2025\",
  \"montant\": 350.00,
  \"categorie\": \"HONORAIRES\",
  \"modePaiement\": \"VIREMENT\",
  \"occurrence\": \"PONCTUEL\",
  \"statutJustificatif\": \"A_VERIFIER\",
  \"qualificationPressentie\": \"CHARGE_COURANTE\",
  \"statutTraitement\": \"A_REVOIR\",
  \"bienId\": $BIEN_ID,
  \"exerciceId\": $EXERCICE_ID,
  \"commentaire\": \"Cabinet Durand - facture a verifier\"
}"

# 10. Recette — Indemnité reçue
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
  \"commentaire\": \"Remboursement sinistre - a qualifier\"
}"

echo "=================================================="
echo "Import termine avec succes."
echo "Bien id: $BIEN_ID"
echo "Exercice id: $EXERCICE_ID"
echo "=================================================="