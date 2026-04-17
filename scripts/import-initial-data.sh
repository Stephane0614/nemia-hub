#!/usr/bin/env bash

set -u

API_URL="http://localhost:8080/api/flux"

post_flux() {
  local label="$1"
  local payload="$2"

  echo "--------------------------------------------------"
  echo "Insertion: $label"
  echo "Payload: $payload"

  local response
  response=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" \
    -X POST "$API_URL" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -d "$payload")

  local body
  body=$(echo "$response" | sed '$d')

  local status
  status=$(echo "$response" | tail -n 1 | sed 's/HTTP_STATUS://')

  echo "HTTP status: $status"
  echo "Response: $body"

  if [[ "$status" != "200" && "$status" != "201" ]]; then
    echo "Erreur sur l'insertion: $label"
    exit 1
  fi
}

echo "Debut import initial data..."
echo "API cible: $API_URL"

post_flux "Loyer studio Bordeaux" '{
  "date":"2026-04-01",
  "type":"RECETTE",
  "libelle":"Loyer studio Bordeaux",
  "montant":650.00,
  "categorie":"LOYER",
  "modePaiement":"VIREMENT",
  "commentaire":"Locataire avril"
}'

post_flux "Charges copro T2" '{
  "date":"2026-04-02",
  "type":"DEPENSE",
  "libelle":"Charges copro T2",
  "montant":120.50,
  "categorie":"CHARGES_COPRO",
  "modePaiement":"PRELEVEMENT",
  "commentaire":"Appel de charges"
}'

post_flux "Facture electricite" '{
  "date":"2026-04-03",
  "type":"DEPENSE",
  "libelle":"Facture electricite",
  "montant":48.20,
  "categorie":"ELECTRICITE",
  "modePaiement":"PRELEVEMENT",
  "commentaire":"EDF avril"
}'

post_flux "Facture eau" '{
  "date":"2026-04-04",
  "type":"DEPENSE",
  "libelle":"Facture eau",
  "montant":22.90,
  "categorie":"EAU",
  "modePaiement":"PRELEVEMENT",
  "commentaire":"Consommation mars"
}'

post_flux "Internet logement" '{
  "date":"2026-04-05",
  "type":"DEPENSE",
  "libelle":"Internet logement",
  "montant":29.99,
  "categorie":"INTERNET",
  "modePaiement":"CARTE",
  "commentaire":"Box fibre"
}'

post_flux "Assurance PNO" '{
  "date":"2026-04-06",
  "type":"DEPENSE",
  "libelle":"Assurance PNO",
  "montant":18.75,
  "categorie":"ASSURANCE",
  "modePaiement":"VIREMENT",
  "commentaire":"Mensualite"
}'

post_flux "Interets emprunt avril" '{
  "date":"2026-04-07",
  "type":"DEPENSE",
  "libelle":"Interets emprunt avril",
  "montant":210.40,
  "categorie":"INTERETS_EMPRUNT",
  "modePaiement":"PRELEVEMENT",
  "commentaire":"Banque"
}'

post_flux "Achat mobilier" '{
  "date":"2026-04-08",
  "type":"DEPENSE",
  "libelle":"Achat mobilier",
  "montant":89.90,
  "categorie":"MOBILIER",
  "modePaiement":"CARTE",
  "commentaire":"Table de chevet"
}'

post_flux "Taxe fonciere provision" '{
  "date":"2026-04-09",
  "type":"DEPENSE",
  "libelle":"Taxe fonciere provision",
  "montant":95.00,
  "categorie":"TAXE_FONCIERE",
  "modePaiement":"CHEQUE",
  "commentaire":"Provision mensuelle"
}'

post_flux "Petits consommables" '{
  "date":"2026-04-10",
  "type":"DEPENSE",
  "libelle":"Petits consommables",
  "montant":14.30,
  "categorie":"CONSOMMABLES",
  "modePaiement":"CARTE",
  "commentaire":"Ampoules et produits menagers"
}'

echo "--------------------------------------------------"
echo "Import termine avec succes."