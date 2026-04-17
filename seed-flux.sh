#!/usr/bin/env bash

API_URL="http://localhost:8080/api/flux"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-01",
  "type":"RECETTE",
  "libelle":"Loyer studio Bordeaux",
  "montant":650.00,
  "categorie":"LOYER",
  "modePaiement":"VIREMENT",
  "commentaire":"Locataire avril"
}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-02",
  "type":"DEPENSE",
  "libelle":"Charges copro T2",
  "montant":120.50,
  "categorie":"CHARGES_COPRO",
  "modePaiement":"PRELEVEMENT",
  "commentaire":"Appel de charges"
}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-03",
  "type":"DEPENSE",
  "libelle":"Facture électricité",
  "montant":48.20,
  "categorie":"ELECTRICITE",
  "modePaiement":"PRELEVEMENT",
  "commentaire":"EDF avril"
}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-04",
  "type":"DEPENSE",
  "libelle":"Facture eau",
  "montant":22.90,
  "categorie":"EAU",
  "modePaiement":"PRELEVEMENT",
  "commentaire":"Consommation mars"
}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-05",
  "type":"DEPENSE",
  "libelle":"Internet logement",
  "montant":29.99,
  "categorie":"INTERNET",
  "modePaiement":"CARTE",
  "commentaire":"Box fibre"
}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-06",
  "type":"DEPENSE",
  "libelle":"Assurance PNO",
  "montant":18.75,
  "categorie":"ASSURANCE",
  "modePaiement":"VIREMENT",
  "commentaire":"Mensualité"
}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-07",
  "type":"DEPENSE",
  "libelle":"Intérêts emprunt avril",
  "montant":210.40,
  "categorie":"INTERETS_EMPRUNT",
  "modePaiement":"PRELEVEMENT",
  "commentaire":"Banque"
}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-08",
  "type":"DEPENSE",
  "libelle":"Achat mobilier",
  "montant":89.90,
  "categorie":"MOBILIER",
  "modePaiement":"CARTE",
  "commentaire":"Table de chevet"
}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-09",
  "type":"DEPENSE",
  "libelle":"Taxe foncière provision",
  "montant":95.00,
  "categorie":"TAXE_FONCIERE",
  "modePaiement":"CHEQUE",
  "commentaire":"Provision mensuelle"
}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{
  "date":"2026-04-10",
  "type":"DEPENSE",
  "libelle":"Petits consommables",
  "montant":14.30,
  "categorie":"CONSOMMABLES",
  "modePaiement":"CARTE",
  "commentaire":"Ampoules et produits ménagers"
}'

echo
echo "Seed terminé."
