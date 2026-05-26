# PILOTAGE — Mémoire Opérationnelle
## nemia-hub — MVP v1.1 — Checkpoint 08/05/2026

---

## 1. Vision & Objectifs du projet

| Élément | Détail |
|---|---|
| **Nom** | nemia-hub |
| **Vision** | Hub central de l'écosystème Nemia |
| **Domaine métier actif** | Gestion des flux financiers LMNP |
| **Concurrent implicite** | Excel |
| **Promesse produit** | Battre Excel sur la clarté, la structure, la fiabilité, le suivi documentaire et la préparation comptable/fiscale |
| **Version courante** | MVP v1.1 — déployé sur https://nemiahub.eu |

**Mission produit**
- Dépasser un simple suivi bancaire
- Structurer la donnée métier
- Améliorer la lisibilité et le pilotage
- Préparer la comptabilité, la fiscalité et les amortissements
- Rester pragmatique, simple et évolutif

**Les 5 usages métier cibles**
1. Pilotage quotidien
2. Préparation comptable
3. Préparation fiscale
4. Préparation des amortissements
5. Support à la décision / optimisation

**Donnée reine du produit**
> Le flux contextualisé — un flux rattaché à un bien, un exercice, une catégorie métier, un statut documentaire, une qualification comptable/fiscale pressentie.

**Doctrine produit**
- Capter proprement
- Qualifier intelligemment
- Signaler les arbitrages
- Préparer la décision — ne pas décider fiscalement à la place de l'utilisateur

**Déploiement actif**
- Application en ligne : https://nemiahub.eu
- Deux utilisateurs : admin, emilie
- VPS OVH Gravelines (4 vCores, 8 Go RAM, 75 Go SSD)
- Sauvegarde automatique quotidienne

---

## 2. Stack technique & contraintes

### Backend
| Élément | Valeur |
|---|---|
| Langage | Java 17 (Amazon Corretto) |
| Framework | Spring Boot REST (4.x) |
| Persistance | Spring Data JPA + Bean Validation |
| Base de données | PostgreSQL 16 via Docker Compose |
| Migrations | Flyway (V0, V1, V2) |
| Sécurité | Spring Security + JWT (HMAC-SHA256, 24h) |
| Tests | JUnit — 179 tests au vert |
| CI | GitHub Actions |

### Frontend
| Élément | Valeur |
|---|---|
| Framework | Angular 21.2.0 standalone |
| Langage | TypeScript |
| Style | SCSS par composant |
| UI | Angular Material |
| Formulaires | Reactive Forms uniquement |
| HTTP | HttpClient + AuthInterceptor |
| Node.js | 22.19.0 |
| npm | 10.9.3 |
| State management | Aucun — pas de state management global |

### Infrastructure
| Élément | Valeur |
|---|---|
| Hébergeur | OVHcloud VPS-1 Gravelines |
| OS VPS | Ubuntu 25.04 (non-LTS — upgrade 26.04 à prévoir) |
| Conteneurs | Docker Compose — 3 services (nemia-db, nemia-api, nemia-web) |
| Domaine | nemiahub.eu |
| HTTPS | Let's Encrypt via Certbot + Nginx |
| Repo | GitHub |
| Sauvegarde | pg_dump quotidien 3h00, rotation 7 jours |

### Conventions actives — à respecter impérativement
- `registerLocaleData(localeFr)` dans le constructeur de tout composant utilisant `| date`, `| currency` ou `| number`
- Pipe `date` toujours avec format explicite `'dd/MM/yyyy'`
- Locale globale : `MAT_DATE_LOCALE: 'fr-FR'`, `LOCALE_ID: 'fr-FR'`, `provideNativeDateAdapter()` dans `app.config.ts`
- Transport backend des dates : `yyyy-MM-dd` inchangé
- Pas de composants partagés nouveaux sauf nécessité évidente
- Pas de state management global
- Versions Node/npm identiques sur toutes les machines de dev (NVM recommandé)
- `npm ci` pour installer les dépendances (jamais `npm install` sur le PC secondaire)
- Mises à jour de dépendances uniquement depuis le PC de référence (PC pro)

### Contraintes PostgreSQL & infrastructure
- Clés étrangères strictes en place sur toutes les relations
- Configuration externalisée via variables d'environnement (.env non versionné)
- PostgreSQL non exposé sur l'extérieur (réseau Docker interne uniquement)
- Profil Spring dev/prod séparé (application-dev.properties non versionné)

---

## 3. Architecture globale décidée

### Repository
- Hébergement : GitLab
- Organisation : mono-repo
- Branches : `develop` comme branche principale de travail

### Structure logique
```
nemia-hub/
  Nemia-Core/        — backend Spring Boot
  Nemia-View/        — frontend Angular
```

### Architecture backend — par domaine métier
```
com.nemia.core/
  flux/              controller, service, repository, model, dto
  bien/              controller, service, repository, model, dto
  exercice/          controller, service, repository, model, dto
  justificatif/      controller, service, repository, model, dto
  travaux/           controller, service, repository, model, dto
  mobilier/          controller, service, repository, model, dto
  emprunt/           controller, service, repository, model, dto
  home/              controller, service (agrégats)
  common/
    exception/       exceptions métier partagées
```

**Principes backend**
- Pas d'interface de service inutile
- Pas de mapper dédié
- Pas d'abstraction prématurée
- Gestion d'erreurs centralisée dans `GlobalExceptionHandler`
- Normalisation systématique : trim, vide → null

### Architecture frontend — par feature
```
src/app/
  app.config.ts
  app.routes.ts
  app.ts / app.html / app.scss

  core/api/
    api.config.ts

  features/
    home/
    flux/
    bien/
    exercice/
    justificatif/
    travaux/
    mobilier/
    emprunt/

  shared/ui/
    confirm-dialog/
```

### Routes frontend actives
```
/                              Home
/flux                          FluxList (paginée, 8 filtres UI)
/flux/nouveau                  FluxForm création
/flux/:id/modifier             FluxForm édition
/biens                         BienList
/biens/nouveau                 BienForm création
/biens/:id                     BienDetail
/biens/:id/modifier            BienForm édition
/exercices                     ExerciceList
/exercices/nouveau             ExerciceForm création
/exercices/:id/modifier        ExerciceForm édition
/exercice                      ExerciceSynthese (exercice en cours)
/justificatifs/nouveau         JustificatifForm création (dialog ou page)
/justificatifs/:id/modifier    JustificatifForm édition
/travaux                       TravauxList
/travaux/nouveau               TravauxForm création
/travaux/:id/modifier          TravauxForm édition
/mobilier                      MobilierList
/mobilier/nouveau              MobilierForm création
/mobilier/:id/modifier         MobilierForm édition
/emprunts                      EmpruntList
/emprunts/nouveau              EmpruntForm création
/emprunts/:id/modifier         EmpruntForm édition
```

### Base URL backend
```
http://localhost:8080/api
```

---

## 4. Fonctionnalités — état MVP v1.0

### Légende
- ✅ Terminé et validé
- ⏳ À faire
- ⚠️ Dette tracée

### Vue d'ensemble

| Fonctionnalité | Backend | Frontend | Statut global |
|---|---|---|---|
| Socle technique | ✅ | ✅ | ✅ |
| CRUD Flux MVP+1 contextualisé | ✅ | ✅ | ✅ |
| Validation métier Flux (bloquant + warnings) | ✅ | ✅ | ✅ |
| FluxCategory 33 valeurs + matrice compatibilité | ✅ | ✅ | ✅ |
| Référentiels Flux | ✅ | ✅ | ✅ |
| Filtres GET /api/flux (9 filtres + pagination) | ✅ | ✅ | ✅ |
| Filtres UI FluxList (8 filtres + MatPaginator) | ✅ | ✅ | ✅ |
| Home cockpit — synthèse mensuelle | ✅ | ✅ | ✅ |
| Home — navigation entre mois | ✅ | ✅ | ✅ |
| Home — filtre Bien | ✅ | ✅ | ✅ |
| Home — alertes cliquables filtrées | ✅ | ✅ | ✅ |
| CRUD Bien | ✅ | ✅ | ✅ |
| BienDetail avec synthèse financière | ✅ | ✅ | ✅ |
| BienDetail — alertes filtrées par bien | ✅ | ✅ | ✅ |
| CRUD Exercice | ✅ | ✅ | ✅ |
| Warnings Exercice (statut/complétude) | ✅ | ✅ | ✅ |
| Synthèse par exercice (4 blocs) | ✅ | ✅ | ✅ |
| Exercice en cours avec fallback | ✅ | ✅ | ✅ |
| CRUD Justificatif | ✅ | ✅ | ✅ |
| JustificatifForm en dialog modale depuis FluxForm | — | ✅ | ✅ |
| CRUD Travaux | ✅ | ✅ | ✅ |
| TravauxForm en dialog modale depuis FluxForm | — | ✅ | ✅ |
| CRUD Mobilier / Équipement | ✅ | ✅ | ✅ |
| MobilierForm en dialog modale depuis FluxForm | — | ✅ | ✅ |
| CRUD Emprunt | ✅ | ✅ | ✅ |
| EmpruntForm en dialog modale depuis FluxForm | — | ✅ | ✅ |
| Filtre exerciceId Home | ✅ | ⏳ | ⏳ Page dédiée future |
| Upload fichiers justificatifs | ⏳ | ⏳ | ⏳ Reporté à migration PostgreSQL |
| Upload photo Bien (photoUrl) | ⚠️ | ⚠️ | ⚠️ Modèle prêt, reporté |
| Cohérence référentielle FK | ⚠️ | — | ⚠️ Reporté à migration PostgreSQL |
| Objet Immobilisation | ⏳ | ⏳ | ⏳ Reporté après migration PostgreSQL |
| Dossier déclaratif / fiscal | ⏳ | ⏳ | ⏳ |
| Migration PostgreSQL + déploiement | ⏳ | — | ⏳ |

---

## 5. Lettres de mission émises (résumé par thread)

| # | Thread | Objet | Statut |
|---|---|---|---|
| 1 | Backend | Flux contextualisé MVP+1 — enrichissement modèle, enums, validation | ✅ |
| 2 | Frontend | Flux contextualisé MVP+1 — modèles TS, formulaire sections, warnings, liste badges | ✅ |
| 3 | Backend | Agrégats Home synthèse — GET /api/home/synthese, 6 blocs | ✅ |
| 4 | Frontend | Home branchée — données réelles, navigation mois, alertes, dernières opérations | ✅ |
| 5 | Backend | Feature Bien — CRUD, enums, référentiels, unicité | ✅ |
| 6 | Frontend | Correctif dates — dd/MM/yyyy partout | ✅ |
| 7 | Frontend | Feature Bien — BienList cards, BienForm, BienDetail, select bienId FluxForm | ✅ |
| 8 | Backend | Feature Exercice + correctif unicité Bien | ✅ |
| 9 | Frontend | Feature Exercice — ExerciceList, ExerciceForm, select exerciceId FluxForm | ✅ |
| 10 | Backend | Consolidation validations — fix doublon Bien 500→409, validations Exercice | ✅ |
| 11 | Frontend | Consolidation mineure — warnings Exercice, correctif BienForm 409 | ✅ |
| 12 | Backend | Filtre bienId/exerciceId Home synthèse | ✅ |
| 13 | Frontend | Filtre Bien Home + Synthèse BienDetail | ✅ |
| 14 | Backend + Frontend | Navigation filtrée FluxList — GET /api/flux avec filtres, FluxList query params | ✅ |
| 15 | Frontend | Correctif alertes contextuelles — bienId propagé depuis BienDetail et Home | ✅ |
| 16 | Backend | Feature Justificatif — CRUD, enums, référentiels, justificatifId dans Flux | ✅ |
| 17 | Frontend | Feature Justificatif — JustificatifForm, intégration FluxForm dialog | ✅ |
| 18 | Backend | Enrichissement FluxCategory 33 valeurs + règles compatibilité MVP | ✅ |
| 19 | Frontend | Adaptation FluxCategory — enum TS, badges 6 familles | ✅ |
| 20 | Backend | Extension filtres GET /api/flux (9 filtres) + pagination | ✅ |
| 21 | Frontend | Correction bug JustificatifForm + filtres UI FluxList + pagination | ✅ |
| 22 | Backend | Synthèse par exercice — 4 blocs + endpoint en-cours | ✅ |
| 23 | Frontend | Page ExerciceSynthese — 4 blocs, exercice en cours, select navigation | ✅ |
| 24 | Backend | Objet Travaux — CRUD, enums, filtre bienId, validation travauxId | ✅ |
| 25 | Frontend | Feature Travaux — liste, formulaire, intégration FluxForm dialog | ✅ |
| 26 | Backend | Objet Mobilier — CRUD, enums, filtre bienId, validation mobilierId | ✅ |
| 27 | Frontend | Feature Mobilier — liste, formulaire, intégration FluxForm dialog | ✅ |
| 28 | Backend | Correction FinalitePressentie ENTRETIEN → ENTRETIEN_COURANT | ✅ |
| 29 | Backend | Objet Emprunt — CRUD, enum, filtre bienId, validation empruntId | ✅ |
| 30 | Frontend | Feature Emprunt — liste, formulaire, intégration FluxForm dialog | ✅ |
| 31 | Backend | Migration SQLite → PostgreSQL 16 Docker Compose | ✅ |
| — | Infra | Dockerisation backend (Maven+JRE) + frontend (Node+Nginx) + docker-compose.yml | ✅ |
| 32 | Backend | FK strictes Flyway + GlobalExceptionHandler 409 sur violations FK | ✅ |
| 33 | Backend | Authentification Spring Security + JWT (login, filtre, DataInitializer) | ✅ |
| 34 | Frontend | Page login + AuthInterceptor + AuthGuard + toolbar conditionnelle | ✅ |
| 35 | Infra | Externalisation secrets + Pipeline CI GitHub Actions (3 jobs) | ✅ |
| 36 | Infra | Déploiement VPS OVH + HTTPS Let's Encrypt + sauvegarde pg_dump | ✅ |

---

## 6. Décisions structurantes prises (et pourquoi)

| Décision | Pourquoi | Date |
|---|---|---|
| Flux contextualisé comme donnée reine | Sans bien, exercice, qualification, les agrégats sont décoratifs | Avant MVP |
| Vocabulaire produit fermé (enums) | Filtres, agrégats et cohérence impossibles avec valeurs libres | Avant MVP |
| Architecture backend par domaine métier | Scalabilité, lisibilité, cohérence avec la future migration | Thread Bien |
| Pas de clé étrangère stricte en SQLite | Compatible migration PostgreSQL future — validation en service | Thread Bien |
| Boolean repository pour anti-doublons | Évite IncorrectResultSizeDataAccessException sur Optional | Thread consolidation |
| Filtre Exercice Home → page dédiée | Conflit UX avec navigation mois — deux modes incompatibles | Thread filtre Home |
| Home filtre Bien avec option "Tous les biens" | Lecture quotidienne par bien naturelle, option neutre indispensable | Thread filtre Home |
| Upload justificatifs reporté à migration PostgreSQL | SQLite inadapté au stockage binaire, risque perte en migration | Checkpoint 19/04 |
| Stockage fichiers en base PostgreSQL (futur) | Application déployée, deux utilisateurs, sauvegardes cohérentes | Checkpoint 19/04 |
| Warnings non bloquants (Flux, Exercice) | Ne pas bloquer la saisie — signaler sans interdire | Thread Flux MVP+1 |
| Navigation filtrée via query params | Couplage léger — pas de state management global nécessaire | Thread navigation filtrée |
| Pas de liste globale Justificatifs | Accès contextuel uniquement depuis FluxForm | Thread #17 |
| JustificatifForm en dialog modale | Évite perte de données FluxForm lors de création justificatif | Thread #21 |
| Badges catégorie par famille visuelle | 6 familles — pas par valeur individuelle | Thread #19 |
| FluxForm passe en mode édition après premier create | Évite bug doublon flux après warning | Thread #25 |
| Qualifications pressenties en dur dans Travaux/Mobilier | Pas de dépendance au référentiel flux | Thread #25/#27 |
| Lien travauxId dans Flux — maintenu provisoirement | Décision finale après retours d'utilisation réelle | Thread #25 |
| Immobilisation reporté après migration PostgreSQL | Prend son sens après épreuve Travaux/Mobilier en réel | Thread pilotage |
| Refonte UX FluxForm — dette assumée | Formulaire trop dense — décision après retours utilisateur | Thread pilotage |
| Ventilation emprunt portée par le Flux | Via catégories EMPRUNT_CAPITAL/INTERETS/ASSURANCE existantes | Thread #29 |
| PostgreSQL via Docker Compose (pas instance locale) | Isolation totale des bases pro existantes | Mission #31 |
| Flyway pour migrations versionnées | Schéma reproductible et traçable (V0, V1, V2) | Mission #32 |
| FK strictes + validation service maintenue | Double filet : service pour messages métier, FK pour intégrité | Mission #32 |
| Auth JWT stateless sans rôles | Deux utilisateurs, pas besoin de RBAC | Mission #33 |
| CORS dans SecurityConfig (WebConfig supprimé) | Spring Security gère CORS — évite les conflits | Mission #33 |
| standaloneSetup bypass sécurité dans tests | Pas besoin de @WithMockUser — 179 tests inchangés | Mission #33 |
| Token JWT en localStorage | Suffisant pour 2 utilisateurs, simplicité | Mission #34 |
| API_BASE_URL = '/api' (URL relative) | Nginx reverse proxy en prod, Angular proxy en dev | Mission #36 |
| Profil Spring dev/prod séparé | application-dev.properties non versionné | Mission #35 |
| GitHub (pas GitLab) pour le repo | Config entreprise bloquante sur GitLab perso | Mission #35 |
| Pipeline CI : bloquer sur critical + high | Sécurité sans faux positifs excessifs | Mission #35 |
| Déploiement simple git pull + docker compose | Suffisant pour 2 utilisateurs — registry Docker reporté | Mission #36 |
| Sauvegarde pg_dump locale 7 jours | Minimum viable — backup externe reporté | Mission #36 |
| Node.js identique sur toutes les machines (NVM) | Évite les diffs package-lock.json entre PC pro et perso | Post-mission #35 |

---

## 7. Dettes techniques & fonctionnelles

### Dettes résolues en v1.1
| Dette | Résolution |
|---|---|
| ~~Migration SQLite → PostgreSQL~~ | PostgreSQL 16 Docker Compose — mission #31 |
| ~~Cohérence référentielle FK~~ | FK strictes Flyway — mission #32 |
| ~~Clés étrangères strictes~~ | Flyway V1 — mission #32 |
| ~~Secrets en dur dans le code~~ | Variables d'environnement — mission #35 |
| ~~Pas d'authentification~~ | Spring Security + JWT — missions #33/#34 |
| ~~Pas d'interceptor HTTP global~~ | AuthInterceptor Angular — mission #34 |

### Dettes fonctionnelles actives
| Dette | Impact |
|---|---|
| Upload fichiers justificatifs | Suivi documentaire déclaratif uniquement — débloqué par PostgreSQL |
| Upload photo Bien | photoUrl toujours null — débloqué par PostgreSQL |
| Refonte UX FluxForm | Formulaire trop dense avec les objets riches |
| Filtre exerciceId Home non implémenté UI | Mode analyse exercice indisponible sur Home |
| niveauConfiance absent du modèle Flux | Solidité de l'interprétation non tracée |
| Sous-catégories Flux | Niveau d'analyse plus fin indisponible |
| Pagination sur Travaux/Mobilier/Emprunt | Performance si grand volume |

### Dettes techniques actives
| Dette | Impact |
|---|---|
| Tests unitaires violations FK | Non écrits lors mission #32 |
| Erreur 500 "Frais de notaire refinancement" | Payload suspect — à investiguer |
| OWASP Dependency-Check pipeline | Abandonné (trop lent) — Trivy à envisager |
| Ubuntu 25.04 non-LTS sur VPS | Support fin janvier 2026 — upgrade 26.04 LTS à prévoir |
| node_modules racine pour Prettier | Non documenté — dédié à Prettier + plugin Java |

### Dettes UX/UI
| Dette | Impact |
|---|---|
| returnTo encore en place dans JustificatifForm | Code mort à nettoyer |
| FinalitePressentie en dur dans composants | Pattern assumé mais à surveiller |

### Dettes reportées explicitement
| Dette | Horizon |
|---|---|
| Immobilisation | Après retours utilisateurs |
| Dossier déclaratif / fiscal | Après upload fichiers + objets riches |
| Multi-biens avancé / comparaisons | Après stabilisation |
| Simulations d'optimisation | Dernier horizon |
| Export PDF synthèse exercice | Moyen terme |
| Page archive exercices clos | Moyen terme |
| Indicateur "prêt à clôturer" exercice | Moyen terme |
| Warning clôture — liste flux bloquants | Moyen terme |
| Backup externe (hors VPS) | Moyen terme |
| CI/CD automatique (déploiement auto) | Moyen terme |

---

## 8. Prochaines étapes prévues

### Phase actuelle — Mise en situation réelle en ligne
Application déployée sur https://nemiahub.eu le 08/05/2026.
Deux utilisateurs : admin (développeur) et emilie.

| Étape | Nature | Statut |
|---|---|---|
| 1 | Migration SQLite → PostgreSQL | ✅ Mission #31 |
| 2 | FK strictes | ✅ Mission #32 |
| 3 | Authentification backend | ✅ Mission #33 |
| 4 | Authentification frontend | ✅ Mission #34 |
| 5 | Externalisation secrets + Pipeline CI | ✅ Mission #35 |
| 6 | Déploiement VPS + HTTPS + sauvegarde | ✅ Mission #36 |
| 7 | Test avec données réelles | ⏳ En cours |
| 8 | Collecte des retours d'utilisation | ⏳ |

### Après retours d'utilisation
| Priorité | Chantier | Nature |
|---|---|---|
| 1 | Refonte UX FluxForm | Frontend — simplifier selon retours |
| 2 | Upload fichiers justificatifs | Backend + Frontend |
| 3 | Upload photo Bien | Backend + Frontend |
| 4 | Export PDF synthèse exercice | Frontend |
| 5 | Indicateur "prêt à clôturer" | Backend + Frontend |

### Long terme — montée en puissance
- Objet Immobilisation
- Dossier déclaratif / préparation fiscale
- Multi-biens avancé
- Simulations d'optimisation
- Backup externe
- CI/CD automatique

---

## Contrats d'API actifs (résumé)

| Endpoint | Description |
|---|---|
| `POST /api/auth/login` | Login — retourne JWT |
| `GET /api/flux` | Liste paginée, 9 filtres + page, size — **Auth requise** |
| `GET /api/flux/{id}` | Flux par id |
| `POST /api/flux` | Création |
| `PUT /api/flux/{id}` | Modification |
| `DELETE /api/flux/{id}` | Suppression — 409 si FK violée |
| `GET /api/flux/referentials` | Enums Flux |
| `GET /api/biens` | Liste biens |
| `GET /api/biens/{id}` | Bien par id |
| `POST /api/biens` | Création bien |
| `PUT /api/biens/{id}` | Modification bien |
| `DELETE /api/biens/{id}` | Suppression bien — 409 si référencé |
| `GET /api/biens/referentials` | Enums Bien |
| `GET /api/exercices` | Liste exercices |
| `GET /api/exercices/{id}` | Exercice par id |
| `GET /api/exercices/en-cours` | Exercice en cours avec fallback |
| `GET /api/exercices/{id}/synthese` | Synthèse exercice — 4 blocs |
| `POST /api/exercices` | Création exercice |
| `PUT /api/exercices/{id}` | Modification exercice |
| `DELETE /api/exercices/{id}` | Suppression exercice — 409 si référencé |
| `GET /api/exercices/referentials` | Enums Exercice |
| `GET /api/justificatifs` | Liste justificatifs |
| `GET /api/justificatifs/{id}` | Justificatif par id |
| `POST /api/justificatifs` | Création justificatif |
| `PUT /api/justificatifs/{id}` | Modification justificatif |
| `DELETE /api/justificatifs/{id}` | Suppression justificatif — 409 si référencé |
| `GET /api/justificatifs/referentials` | Enums Justificatif |
| `GET /api/travaux` | Liste travaux — filtre optionnel bienId |
| `GET /api/travaux/{id}` | Travaux par id |
| `POST /api/travaux` | Création travaux |
| `PUT /api/travaux/{id}` | Modification travaux |
| `DELETE /api/travaux/{id}` | Suppression travaux — 409 si référencé |
| `GET /api/travaux/referentials` | Enums Travaux |
| `GET /api/mobilier` | Liste mobilier — filtre optionnel bienId |
| `GET /api/mobilier/{id}` | Mobilier par id |
| `POST /api/mobilier` | Création mobilier |
| `PUT /api/mobilier/{id}` | Modification mobilier |
| `DELETE /api/mobilier/{id}` | Suppression mobilier — 409 si référencé |
| `GET /api/mobilier/referentials` | Enums Mobilier |
| `GET /api/emprunts` | Liste emprunts — filtre optionnel bienId |
| `GET /api/emprunts/{id}` | Emprunt par id |
| `POST /api/emprunts` | Création emprunt |
| `PUT /api/emprunts/{id}` | Modification emprunt |
| `DELETE /api/emprunts/{id}` | Suppression emprunt — 409 si référencé |
| `GET /api/emprunts/referentials` | Enums Emprunt |
| `GET /api/home/synthese` | Synthèse Home — params : mois, bienId, exerciceId |

> Toutes les routes sauf `POST /api/auth/login` exigent le header `Authorization: Bearer <token>`.

---

## Organisation de travail

### Threads
- 1 thread pilotage central (ici)
- 1 thread backend (clos pour v1.1)
- 1 thread frontend (clos pour v1.1)
- 1 thread infra / déploiement (clos pour v1.1)
- Lettres de mission émises depuis le thread pilotage
- Mémoires opérationnelles backend et frontend dans le projet

### Méthode d'implémentation
- On cadre une seule tâche
- Claude propose le code
- Le développeur lit, comprend, implémente
- Il teste
- On corrige si besoin
- Quand c'est OK, on passe à la suite

### Environnement de développement
- PC pro : machine principale de dev (Node 22.19.0, npm 10.9.3, Java 17)
- PC perso : accès SSH au VPS, dev de secours (NVM pour aligner les versions)
- Mises à jour de dépendances : uniquement depuis le PC pro
- `npm ci` sur le PC secondaire (jamais `npm install`)

### Procédure de déploiement
```
1. SSH sur le VPS
2. cd ~/nemia-hub
3. git pull
4. docker compose up -d --build
5. docker compose logs -f
```

---

*Document mis à jour le 08/05/2026 — MVP v1.1 déployé sur https://nemiahub.eu*
*À mettre à jour à chaque clôture de thread ou décision structurante*
