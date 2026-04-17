#!/usr/bin/env bash

API_URL="http://localhost:8080/api/flux"

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-01","type":"RECETTE","libelle":"Loyer studio Bordeaux","montant":650.00,"categorie":"LOYER","modePaiement":"VIREMENT","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Locataire avril"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-02","type":"DEPENSE","libelle":"Charges copro T2","montant":120.50,"categorie":"CHARGES_COPRO","modePaiement":"PRELEVEMENT","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Appel de charges"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-03","type":"DEPENSE","libelle":"Facture electricite","montant":48.20,"categorie":"ELECTRICITE","modePaiement":"PRELEVEMENT","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"EDF avril"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-04","type":"DEPENSE","libelle":"Facture eau","montant":22.90,"categorie":"EAU","modePaiement":"PRELEVEMENT","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Consommation mars"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-05","type":"DEPENSE","libelle":"Internet logement","montant":29.99,"categorie":"INTERNET","modePaiement":"CARTE","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Box fibre"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-06","type":"DEPENSE","libelle":"Assurance PNO","montant":18.75,"categorie":"ASSURANCE","modePaiement":"VIREMENT","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Mensualite"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-07","type":"DEPENSE","libelle":"Interets emprunt avril","montant":210.40,"categorie":"INTERETS_EMPRUNT","modePaiement":"PRELEVEMENT","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Banque"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-08","type":"DEPENSE","libelle":"Achat mobilier table de chevet","montant":89.90,"categorie":"MOBILIER","modePaiement":"CARTE","occurrence":"PONCTUEL","statutJustificatif":"FOURNI","qualificationPressentie":"A_ARBITRER","statutTraitement":"A_REVOIR","commentaire":"Table de chevet chambre"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-09","type":"DEPENSE","libelle":"Taxe fonciere provision","montant":95.00,"categorie":"TAXE_FONCIERE","modePaiement":"CHEQUE","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Provision mensuelle"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-10","type":"DEPENSE","libelle":"Petits consommables","montant":14.30,"categorie":"CONSOMMABLES","modePaiement":"CARTE","occurrence":"PONCTUEL","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Ampoules et produits"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-11","type":"DEPENSE","libelle":"Honoraires agence gestion","montant":65.00,"categorie":"HONORAIRES","modePaiement":"VIREMENT","occurrence":"RECURRENT","statutJustificatif":"A_FOURNIR","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Frais gestion avril"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-12","type":"DEPENSE","libelle":"Travaux peinture salon","montant":450.00,"categorie":"TRAVAUX","modePaiement":"VIREMENT","occurrence":"PONCTUEL","statutJustificatif":"A_FOURNIR","qualificationPressentie":"A_ARBITRER","statutTraitement":"A_REVOIR","commentaire":"Devis accepte, facture a recevoir"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-13","type":"DEPENSE","libelle":"Frais bancaires tenue de compte","montant":7.50,"categorie":"FRAIS_BANCAIRES","modePaiement":"PRELEVEMENT","occurrence":"RECURRENT","statutJustificatif":"NON_REQUIS","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Releve avril"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-14","type":"RECETTE","libelle":"Remboursement charges locataire","montant":45.00,"categorie":"LOYER","modePaiement":"VIREMENT","occurrence":"PONCTUEL","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Regularisation charges 2025"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-15","type":"DEPENSE","libelle":"Achat aspirateur","montant":129.00,"categorie":"CONSOMMABLES","modePaiement":"CARTE","occurrence":"PONCTUEL","statutJustificatif":"FOURNI","qualificationPressentie":"A_ARBITRER","statutTraitement":"A_REVOIR","commentaire":"A qualifier : charge ou immobilisation"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-16","type":"MOUVEMENT_FINANCIER","libelle":"Apport personnel compte activite","montant":2000.00,"categorie":"AUTRE","modePaiement":"VIREMENT","occurrence":"PONCTUEL","statutJustificatif":"NON_REQUIS","qualificationPressentie":"HORS_RESULTAT","statutTraitement":"BRUT","commentaire":"Apport initial"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-17","type":"DEPENSE","libelle":"Remplacement chauffe-eau","montant":780.00,"categorie":"TRAVAUX","modePaiement":"CHEQUE","occurrence":"PONCTUEL","statutJustificatif":"A_FOURNIR","qualificationPressentie":"A_ARBITRER","statutTraitement":"A_REVOIR","commentaire":"Facture plombier a recevoir"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-17","type":"DEPENSE","libelle":"Assurance loyers impayes","montant":32.00,"categorie":"ASSURANCE","modePaiement":"PRELEVEMENT","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"GLI mensuelle"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-17","type":"DEPENSE","libelle":"Achat refrigerateur","montant":340.00,"categorie":"MOBILIER","modePaiement":"CARTE","occurrence":"PONCTUEL","statutJustificatif":"FOURNI","qualificationPressentie":"A_ARBITRER","statutTraitement":"A_REVOIR","commentaire":"Electromenager cuisine"}'

curl -s -X POST "$API_URL" -H "Content-Type: application/json" -d '{"date":"2026-04-17","type":"RECETTE","libelle":"Loyer mai anticipe","montant":650.00,"categorie":"LOYER","modePaiement":"VIREMENT","occurrence":"RECURRENT","statutJustificatif":"FOURNI","qualificationPressentie":"CHARGE_COURANTE","statutTraitement":"BRUT","commentaire":"Virement anticipe locataire"}'

echo
echo "Seed termine."