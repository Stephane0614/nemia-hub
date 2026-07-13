# NEMIA-HUB — GUIDE UTILISATEUR
## Premiers pas en régime réel LMNP
### Version MVP v1.0 — Mai 2026

---

## Avant de commencer — ce que tu dois savoir


je teste la mise en place d'un nouveau workflow de travail.

Tu passes du micro-BIC au régime réel. Concrètement, ça change tout :

En micro-BIC, tu déclarais tes loyers et l'administration appliquait un abattement forfaitaire de 50%. Tu n'avais rien à justifier, rien à classer, rien à calculer.

En régime réel, tu dois déclarer tes recettes ET tes charges réelles. Chaque dépense que tu veux déduire doit être justifiée par une pièce (facture, ticket, relevé). Certaines dépenses ne se déduisent pas en une fois — elles s'amortissent sur plusieurs années. C'est plus de travail, mais c'est souvent plus avantageux fiscalement.

nemia-hub est là pour t'aider à structurer tout ça proprement, pas pour remplacer un comptable. L'outil capte, classe et prépare. La décision fiscale finale, c'est toi ou ton comptable qui la prenez.

---

## Étape 1 — Créer ton bien

C'est la première chose à faire. Tout dans nemia-hub est rattaché à un bien.

**Où :** menu Biens → bouton Créer

**Ce qu'il faut renseigner :**

Le **nom usuel** — c'est le nom que tu donnes au bien pour le reconnaître. Exemple : "Studio Marseille" ou "T2 Lyon 3e". Choisis quelque chose de court et parlant.

L'**adresse simplifiée** — pas besoin de l'adresse postale complète. "12 rue des Lilas, Lyon" suffit.

Le **statut d'activité** — choisis ACTIF puisque ton bien est déjà en location.

Le **type de location** — LMNP_LONGUE_DUREE si tu loues à l'année, LMNP_COURTE_DUREE si tu fais du saisonnier.

Le **régime visé** — REEL puisque tu viens de passer au réel.

**Pourquoi c'est important :**
Sans bien, impossible de rattacher tes opérations. Et sans rattachement, impossible de lire tes données par bien, par exercice, ou de préparer quoi que ce soit pour le comptable.

---

## Étape 2 — Créer ton exercice comptable

Un exercice, c'est la période sur laquelle tu vas regrouper tes recettes et tes charges. En LMNP, c'est généralement l'année civile.

**Où :** menu Exercices → bouton Créer

**Ce qu'il faut renseigner :**

Le **libellé** — simplement "2026" ou "Exercice 2026".

La **date de début** — 01/01/2026.

La **date de fin** — 31/12/2026.

Le **statut** — OUVERT.

**Pourquoi c'est important :**
Toutes les synthèses comptables se lisent par exercice. La page Synthèse affiche automatiquement l'exercice en cours. Sans exercice, cette page ne fonctionne pas.

**Question fréquente : et l'exercice 2025 ?**
Si ton passage au réel est rétroactif sur 2025, crée aussi un exercice 2025 et saisis les opérations de cette année-là. Vérifie la date d'effet sur la confirmation DGFIP.

---

## Étape 3 — Saisir tes opérations (flux)

C'est le cœur du produit. Chaque mouvement d'argent lié à ton bien doit être saisi ici.

**Où :** menu Opérations → bouton Créer

### Les champs essentiels

**Date** — la date de l'opération. Facture, prélèvement, virement — la date du document.

**Date de valeur** — optionnelle. Si l'argent est arrivé ou parti à une date différente de la date du document, renseigne-la ici. Utile pour la trésorerie.

**Type de flux** — le sens brut du mouvement :
- RECETTE : de l'argent qui entre (loyers, charges refacturées)
- DEPENSE : de l'argent qui sort (factures, abonnements, travaux)
- MOUVEMENT_FINANCIER : un mouvement qui n'est ni une charge ni une recette au sens comptable (remboursement de capital d'emprunt, virement interne)
- REGULARISATION : une correction ou un ajustement

**Libellé** — une description courte. "Loyer mars 2026", "Facture électricien", "Assurance PNO".

**Montant** — toujours en positif. C'est le type de flux qui donne le sens.

**Catégorie** — c'est le classement métier. Elle dit à quoi correspond la dépense ou la recette. Quelques exemples courants :
- LOYER — ton revenu locatif
- ASSURANCE — ton assurance propriétaire non occupant (PNO)
- COPROPRIETE — tes charges de copro
- TAXE — taxe foncière
- ENTRETIEN_COURANT — petites réparations, maintenance
- TRAVAUX — travaux plus importants
- EMPRUNT_INTERETS — les intérêts de ton prêt (déductibles)
- EMPRUNT_CAPITAL — le remboursement du capital (non déductible)

**Bien rattaché** — sélectionne ton bien.

**Exercice rattaché** — sélectionne l'exercice correspondant.

### Les champs de qualification

**Récurrent / Ponctuel** — est-ce que cette dépense revient régulièrement ? Le loyer est récurrent. Un achat de canapé est ponctuel. Ça aide à lire les charges structurelles.

**Statut justificatif** — as-tu la pièce qui prouve cette dépense ?
- FOURNI : tu as la facture ou le ticket
- A_FOURNIR : tu sais qu'il faut une pièce mais tu ne l'as pas encore
- NON_REQUIS : pas de pièce attendue (rare en régime réel)

**Qualification pressentie** — c'est le champ le plus important pour la suite comptable. Il dit comment tu penses que cette dépense sera traitée :
- CHARGE_COURANTE : dépense déductible immédiatement (assurance, entretien, intérêts d'emprunt)
- IMMOBILISATION : dépense qui sera amortie sur plusieurs années (gros travaux, mobilier important)
- HORS_RESULTAT : mouvement sans impact sur le résultat (remboursement capital emprunt)
- A_ARBITRER : tu ne sais pas encore — et c'est normal

**Statut traitement** — où en est cette ligne dans ton suivi :
- BRUT : saisie mais pas encore relue
- QUALIFIE : tu as vérifié le classement
- A_REVOIR : quelque chose te semble douteux
- VALIDE : tu considères que c'est prêt

---

## Étape 4 — Rattacher les justificatifs

En régime réel, chaque dépense déductible doit être justifiable. Le produit ne stocke pas encore les fichiers (prévu dans une version future), mais il te permet déjà de suivre l'état documentaire.

**Depuis le formulaire d'opération :**
- Clique sur "+ Créer un justificatif"
- Une fenêtre s'ouvre sans perdre ta saisie
- Renseigne le type de pièce (FACTURE, TICKET, RELEVE, etc.)
- Renseigne le statut (FOURNI, A_FOURNIR, etc.)
- Valide — le justificatif est automatiquement rattaché à ton opération

**En attendant l'upload de fichiers :**
Range tes pièces dans un dossier sur ton ordinateur, organisé par année et par mois. Le jour où l'upload sera disponible, tu pourras les rattacher directement dans l'application.

---

## Étape 5 — Saisir tes travaux, mobilier, emprunts

Ces objets existent pour sortir certaines dépenses du flux générique et leur donner une lecture propre.

### Travaux
Si tu as fait des travaux sur ton bien, crée un objet Travaux.

**Où :** menu Travaux → bouton Créer

**Pourquoi :** la finalité des travaux détermine leur traitement comptable. Un entretien courant (repeindre) est une charge déductible immédiatement. Une amélioration (installer une cuisine) est probablement une immobilisation à amortir.

**Le champ clé :** la finalité pressentie — ENTRETIEN_COURANT, REPARATION, AMELIORATION, CREATION, REMISE_EN_ETAT, ou A_ARBITRER si tu ne sais pas.

### Mobilier
Chaque meuble ou équipement significatif mérite une ligne.

**Où :** menu Mobilier → bouton Créer

**Pourquoi :** en location meublée, le mobilier est au cœur de l'activité. Les meubles de valeur sont amortis — c'est une déduction fiscale importante.

**Règle de base :** un meuble à moins de 500€ peut souvent être passé en charge directe. Au-dessus, il sera probablement amorti. Mais c'est ton comptable qui tranchera — toi tu renseignes A_ARBITRER si tu hésites.

### Emprunt
Si tu as un prêt immobilier, crée un objet Emprunt.

**Où :** menu Emprunts → bouton Créer

**Pourquoi :** chaque mensualité de prêt contient du capital (non déductible), des intérêts (déductibles) et parfois de l'assurance (déductible). Il faut les distinguer. Les flux de remboursement se saisissent avec les catégories EMPRUNT_CAPITAL, EMPRUNT_INTERETS, EMPRUNT_ASSURANCE et se rattachent à l'emprunt.

---

## Étape 6 — Lire tes données

### La Home
C'est ton tableau de bord mensuel. Tu y vois :
- le total des recettes et dépenses du mois
- le solde de trésorerie (attention : ce n'est pas le résultat fiscal)
- la répartition des dépenses par catégorie
- les alertes : opérations sans justificatif, à arbitrer, à revoir

Navigue entre les mois avec les flèches. Filtre par bien si tu en as plusieurs.

### La Synthèse exercice
C'est ta vue annuelle de préparation comptable. Tu y vois :
- le bilan financier de l'exercice
- la complétude documentaire — combien de flux n'ont pas de justificatif
- la répartition par qualification — combien en charge courante, en immobilisation, à arbitrer
- le taux de justification global

**C'est cette page que tu présenteras à ton comptable.**

### La liste des opérations
Utilise les 8 filtres pour retrouver ce que tu cherches. Par bien, par exercice, par catégorie, par statut justificatif, par qualification. C'est ta vue de travail quotidienne.

---

## Étape 7 — Le cycle de travail régulier

### Chaque mois
1. Saisis tes opérations du mois — loyer reçu, charges payées, remboursement emprunt
2. Rattache les justificatifs — marque FOURNI ou A_FOURNIR
3. Qualifie — charge courante, immobilisation, ou à arbitrer
4. Vérifie la Home — les alertes te disent ce qui manque

### Chaque trimestre
1. Vérifie la Synthèse exercice — le taux de justification doit monter
2. Passe en revue les flux A_ARBITRER — tranche ce que tu peux
3. Passe les flux vérifiés en statut VALIDE

### En fin d'exercice
1. La Synthèse exercice te donne la vue complète
2. Objectif : zéro flux A_FOURNIR, minimum de flux A_ARBITRER
3. Les données sont prêtes pour ton comptable

---

## Ce que nemia-hub ne fait PAS (et c'est normal)

- Il ne calcule pas ton résultat fiscal — c'est le travail du comptable
- Il ne calcule pas tes amortissements — il prépare la base
- Il ne génère pas ta déclaration — il prépare les données
- Il ne stocke pas encore les fichiers — prévu en version future
- Il ne remplace pas un comptable — il te rend autonome sur la préparation

---

## Vocabulaire rapide

| Terme produit | Ce que ça veut dire |
|---|---|
| Opération | Un mouvement d'argent — entrée ou sortie |
| Flux | Le nom technique d'une opération |
| Qualification pressentie | Comment tu penses que la dépense sera traitée comptablement |
| Charge courante | Dépense déductible immédiatement |
| Immobilisation | Dépense amortie sur plusieurs années |
| Hors résultat | Mouvement sans impact sur le résultat (ex : capital emprunt) |
| À arbitrer | Tu ne sais pas encore — zone de doute normale |
| Statut justificatif | As-tu la pièce qui prouve cette dépense ? |
| Statut traitement | Où en est cette ligne dans ton suivi personnel |
| Exercice | Période comptable — généralement l'année civile |

---

*Guide utilisateur — MVP v1.0 — Mai 2026*
