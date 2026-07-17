 # NEMIA-HUB — SCÉNARIOS DE MISE EN SITUATION
## Cas réels LMNP — Test MVP v1.0
### Mai 2026

---

## Comment utiliser ce document

Chaque scénario est un cas concret que tu vas rencontrer dans ta gestion LMNP.
Pour chaque cas, tu trouveras :
- la situation décrite en une phrase
- exactement quoi saisir dans nemia-hub
- pourquoi chaque champ est renseigné comme ça
- ce que tu dois vérifier après la saisie

Fais-les dans l'ordre. Les premiers sont simples, les derniers plus subtils.

---

## Prérequis — à faire une seule fois

Avant de commencer les scénarios, assure-toi d'avoir :

1. Créé ton bien (cf. Guide utilisateur — Étape 1)
2. Créé ton exercice 2026 (cf. Guide utilisateur — Étape 2)
3. Créé ton emprunt si tu en as un (cf. Guide utilisateur — Étape 5)

---

## SCÉNARIO 1 — Tu reçois le loyer du mois

**Situation :** ton locataire te vire 650€ de loyer le 5 du mois.

**Saisie dans Opérations → Créer :**

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 05/05/2026 | Date du virement reçu |
| Type de flux | RECETTE | De l'argent qui entre |
| Libellé | Loyer mai 2026 | Court et explicite |
| Montant | 650 | Toujours positif |
| Catégorie | LOYER | C'est un revenu locatif |
| Mode de paiement | VIREMENT | Le locataire paie par virement |
| Bien rattaché | Ton bien | Toujours rattacher |
| Exercice | 2026 | L'exercice en cours |
| Récurrent / Ponctuel | RECURRENT | Le loyer revient chaque mois |
| Statut justificatif | FOURNI | Tu as le relevé bancaire |
| Qualification | NON_APPLICABLE | Une recette n'a pas de qualification comptable de charge/immobilisation |
| Statut traitement | QUALIFIE | Rien d'ambigu ici |

**Justificatif :** crée un justificatif de type RELEVE, statut FOURNI.

**Vérification après saisie :**
- Va sur la Home → le total des recettes du mois a augmenté de 650€
- Le solde a évolué en conséquence

**Répète ce scénario chaque mois.** C'est la base de ta tenue.

---

## SCÉNARIO 2 — Tu paies les charges de copropriété

**Situation :** prélèvement trimestriel de 180€ pour les charges de copro.

**Saisie :**

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 01/04/2026 | Date du prélèvement |
| Type de flux | DEPENSE | De l'argent qui sort |
| Libellé | Charges copro T2 2026 | Identifie le trimestre |
| Montant | 180 | |
| Catégorie | COPROPRIETE | Charges de copropriété |
| Mode de paiement | PRELEVEMENT | Prélèvement automatique |
| Bien | Ton bien | |
| Exercice | 2026 | |
| Récurrent / Ponctuel | RECURRENT | Trimestriel = récurrent |
| Statut justificatif | A_FOURNIR | Tu recevras l'appel de fonds |
| Qualification | CHARGE_COURANTE | Les charges courantes de copro sont déductibles |
| Statut traitement | BRUT | À revoir quand tu auras l'appel de fonds |

**Vérification :**
- Home → les dépenses du mois incluent 180€
- Alerte "flux sans justificatif" → normal, tu n'as pas encore l'appel de fonds

**Quand tu reçois l'appel de fonds :** modifie l'opération, crée un justificatif FACTURE / FOURNI, passe le statut justificatif à FOURNI et le statut traitement à QUALIFIE.

---

## SCÉNARIO 3 — Tu paies ta mensualité d'emprunt

**Situation :** ta mensualité de prêt est de 520€ dont 180€ d'intérêts, 310€ de capital et 30€ d'assurance.

**Attention — c'est trois opérations distinctes, pas une seule :**

### Opération 1 — Les intérêts (déductibles)

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 05/05/2026 | |
| Type de flux | DEPENSE | Les intérêts sont une charge |
| Libellé | Intérêts emprunt mai 2026 | |
| Montant | 180 | |
| Catégorie | EMPRUNT_INTERETS | Intérêts du prêt |
| Récurrent / Ponctuel | RECURRENT | |
| Qualification | CHARGE_COURANTE | Les intérêts sont déductibles |
| Emprunt rattaché | Ton emprunt | |
| Statut justificatif | FOURNI | Tu as l'échéancier |
| Statut traitement | QUALIFIE | |

### Opération 2 — Le capital (non déductible)

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 05/05/2026 | |
| Type de flux | MOUVEMENT_FINANCIER | Le capital n'est pas une charge |
| Libellé | Capital emprunt mai 2026 | |
| Montant | 310 | |
| Catégorie | EMPRUNT_CAPITAL | Remboursement du capital |
| Récurrent / Ponctuel | RECURRENT | |
| Qualification | HORS_RESULTAT | Pas d'impact sur le résultat fiscal |
| Emprunt rattaché | Ton emprunt | |
| Statut justificatif | FOURNI | |
| Statut traitement | QUALIFIE | |

### Opération 3 — L'assurance emprunteur (déductible)

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 05/05/2026 | |
| Type de flux | DEPENSE | L'assurance est une charge |
| Libellé | Assurance emprunt mai 2026 | |
| Montant | 30 | |
| Catégorie | EMPRUNT_ASSURANCE | |
| Récurrent / Ponctuel | RECURRENT | |
| Qualification | CHARGE_COURANTE | L'assurance emprunteur est déductible |
| Emprunt rattaché | Ton emprunt | |
| Statut justificatif | FOURNI | |
| Statut traitement | QUALIFIE | |

**Pourquoi trois opérations ?**
Parce que le capital et les intérêts n'ont pas le même traitement fiscal. Si tu saisis tout en une seule ligne de 520€, personne ne pourra distinguer la partie déductible de la partie non déductible. C'est tout l'intérêt de nemia-hub par rapport à Excel.

**Vérification :**
- Home → les dépenses incluent 210€ (intérêts + assurance), pas 520€
- Le mouvement financier de 310€ apparaît séparément
- Synthèse exercice → le bloc qualification montre des montants en CHARGE_COURANTE et en HORS_RESULTAT

---

## SCÉNARIO 4 — Tu paies la taxe foncière

**Situation :** tu reçois l'avis de taxe foncière de 450€.

**Saisie :**

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 15/10/2026 | Date de paiement |
| Type de flux | DEPENSE | |
| Libellé | Taxe foncière 2026 | |
| Montant | 450 | |
| Catégorie | TAXE | |
| Mode de paiement | PRELEVEMENT | |
| Récurrent / Ponctuel | RECURRENT | Annuelle = structurellement récurrente |
| Qualification | CHARGE_COURANTE | La taxe foncière est déductible |
| Statut justificatif | FOURNI | Tu as l'avis d'imposition |
| Statut traitement | QUALIFIE | |

**Justificatif :** crée un justificatif FACTURE (ou RELEVE), statut FOURNI. L'avis d'imposition est ta pièce.

---

## SCÉNARIO 5 — Tu paies l'assurance PNO

**Situation :** prélèvement mensuel de 25€ pour l'assurance propriétaire non occupant.

**Saisie :**

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 01/05/2026 | |
| Type de flux | DEPENSE | |
| Libellé | Assurance PNO mai 2026 | |
| Montant | 25 | |
| Catégorie | ASSURANCE | |
| Récurrent / Ponctuel | RECURRENT | |
| Qualification | CHARGE_COURANTE | L'assurance PNO est déductible |
| Statut justificatif | FOURNI | Tu as l'échéancier ou l'attestation |
| Statut traitement | QUALIFIE | |

---

## SCÉNARIO 6 — Tu fais une petite réparation

**Situation :** le robinet de la salle de bain fuit. Tu appelles un plombier, facture 120€.

**Saisie :**

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 12/05/2026 | Date de la facture |
| Type de flux | DEPENSE | |
| Libellé | Plombier — réparation robinet SDB | |
| Montant | 120 | |
| Catégorie | ENTRETIEN_COURANT | Petite réparation courante |
| Mode de paiement | CARTE | |
| Récurrent / Ponctuel | PONCTUEL | Intervention ponctuelle |
| Qualification | CHARGE_COURANTE | Réparation simple = charge déductible |
| Statut justificatif | FOURNI | Tu as la facture du plombier |
| Statut traitement | QUALIFIE | Pas d'ambiguïté |

**Justificatif :** FACTURE, statut FOURNI.

---

## SCÉNARIO 7 — Tu achètes un meuble

**Situation :** tu achètes un canapé à 800€ pour remplacer l'ancien.

**Saisie opération :**

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 20/05/2026 | |
| Type de flux | DEPENSE | |
| Libellé | Canapé remplacement | |
| Montant | 800 | |
| Catégorie | MOBILIER | |
| Récurrent / Ponctuel | PONCTUEL | |
| Qualification | A_ARBITRER | 800€ — peut-être charge, peut-être immobilisation |
| Statut justificatif | FOURNI | |
| Statut traitement | BRUT | À revoir avec ton comptable |

**Saisie mobilier :** crée aussi un objet Mobilier.

| Champ | Valeur |
|---|---|
| Désignation | Canapé 3 places |
| Bien | Ton bien |
| Montant | 800 |
| Catégorie mobilier | MEUBLE |
| État | REMPLACEMENT |
| Qualification | A_ARBITRER |

**Rattachement :** dans l'opération, sélectionne le mobilier créé dans le champ mobilierId.

**Pourquoi A_ARBITRER ?**
Au-dessus de 500€, un meuble est souvent amorti plutôt que passé en charge directe. Mais la décision dépend de ton comptable et de ta stratégie. Renseigner A_ARBITRER est la bonne posture — tu signales le doute sans trancher à sa place.

**Vérification :**
- Synthèse exercice → le bloc qualification montre un montant en A_ARBITRER
- C'est exactement ce signal qui t'aidera en fin d'année

---

## SCÉNARIO 8 — Tu fais des travaux importants

**Situation :** tu fais refaire la salle de bain complète pour 4 500€ — nouvel équipement, carrelage, plomberie.

**Saisie travaux :** d'abord, crée l'objet Travaux.

| Champ | Valeur | Pourquoi |
|---|---|---|
| Libellé | Réfection salle de bain complète | |
| Bien | Ton bien | |
| Montant total | 4500 | |
| Finalité pressentie | AMELIORATION | Création de valeur — pas un simple entretien |
| Qualification | IMMOBILISATION | Travaux importants = probablement amortis |
| Statut | BRUT | À valider avec le comptable |
| Date début | 01/05/2026 | |
| Date fin | 15/05/2026 | |

**Saisie opération :**

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 15/05/2026 | Date de la facture finale |
| Type de flux | DEPENSE | |
| Libellé | Travaux SDB — facture finale | |
| Montant | 4500 | |
| Catégorie | TRAVAUX | |
| Qualification | IMMOBILISATION | Cohérent avec l'objet travaux |
| Travaux rattaché | Réfection salle de bain | |
| Statut justificatif | FOURNI | Tu as la facture de l'artisan |
| Statut traitement | BRUT | Comptable tranchera |

**Pourquoi IMMOBILISATION ?**
Une réfection complète de salle de bain crée de la valeur dans le bien. Ce n'est pas un simple entretien. Le comptable l'amortira probablement sur 10 à 15 ans. Tu ne déduis pas 4 500€ d'un coup — tu déduis un peu chaque année.

**Si les travaux sont payés en plusieurs fois :**
Crée une opération par paiement (acompte, solde). Rattache chaque opération au même objet Travaux. Le montant total des travaux reste 4 500€.

---

## SCÉNARIO 9 — Tu reçois la régularisation de charges

**Situation :** le syndic t'envoie le décompte annuel. Tu as trop payé de 45€, il te rembourse.

**Saisie :**

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | 10/06/2026 | Date du remboursement |
| Type de flux | RECETTE | Tu reçois de l'argent |
| Libellé | Régularisation charges copro 2025 | |
| Montant | 45 | |
| Catégorie | COPROPRIETE | Ça concerne les charges de copro |
| Récurrent / Ponctuel | PONCTUEL | Une régularisation est ponctuelle |
| Qualification | CHARGE_COURANTE | Ajustement de charge |
| Statut justificatif | FOURNI | Tu as le décompte du syndic |
| Statut traitement | QUALIFIE | |

**Note :** un warning peut apparaître — RECETTE avec catégorie COPROPRIETE est inhabituel. C'est normal ici, c'est une régularisation en ta faveur.

---

## SCÉNARIO 10 — Tu ne sais pas comment classer une dépense

**Situation :** tu as payé 150€ de "frais de gestion" à une plateforme de location. C'est déductible ? C'est quoi comme catégorie ?

**Saisie :**

| Champ | Valeur | Pourquoi |
|---|---|---|
| Date | La date du paiement | |
| Type de flux | DEPENSE | |
| Libellé | Frais plateforme location mai | |
| Montant | 150 | |
| Catégorie | HONORAIRES | Des frais de service / gestion |
| Qualification | A_ARBITRER | Tu n'es pas sûr |
| Statut traitement | A_REVOIR | À vérifier |

**C'est OK de ne pas savoir.**
C'est exactement à ça que servent A_ARBITRER et A_REVOIR. L'opération est saisie, tracée, et elle remontera dans la synthèse exercice comme "à traiter". Ton comptable ou toi-même trancherez plus tard.

---

## Vérifications finales après tous les scénarios

### Sur la Home
- Les totaux recettes et dépenses reflètent ce que tu as saisi
- Les alertes signalent les flux sans justificatif et à arbitrer
- La répartition des dépenses par catégorie est lisible

### Sur la Synthèse exercice
- Le bloc financier montre le bon total
- Le bloc complétude documentaire indique les pièces manquantes
- Le bloc qualification montre la répartition : charges courantes, immobilisations, à arbitrer, hors résultat
- Les éléments à arbitrer sont visibles et quantifiés

### Sur la liste des opérations
- Filtre par statut justificatif A_FOURNIR → tu vois ce qu'il te reste à documenter
- Filtre par qualification A_ARBITRER → tu vois ce qu'il te reste à trancher
- Filtre par statut traitement A_REVOIR → tu vois ce qui nécessite une relecture

---

## Ce que tu apprends en faisant ces scénarios

1. **Chaque mouvement d'argent a un sens comptable** — recette, charge déductible, immobilisation, ou mouvement hors résultat
2. **Le doute est normal** — A_ARBITRER est une valeur légitime, pas un aveu d'incompétence
3. **La rigueur documentaire paie** — en fin d'année, tu seras content d'avoir tout justifié
4. **La ventilation de l'emprunt est essentielle** — sans elle, tu déduis trop ou pas assez
5. **Les travaux importants ne sont pas des charges** — ils s'amortissent, et c'est souvent plus avantageux

---

*Scénarios de mise en situation — MVP v1.0 — Mai 2026*
