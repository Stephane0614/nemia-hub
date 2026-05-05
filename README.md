# Nemia Hub

Application de gestion des flux financiers LMNP (Location Meublée Non Professionnelle). Remplace les workflows Excel par une gestion structurée, fiable et documentaire des données comptables et fiscales.

**Statut :** MVP v1.0 — fonctionnalités core complètes et validées.

---

## Architecture

Monorepo composé de deux applications indépendantes :

| Composant | Répertoire | Technologie | Port |
|---|---|---|---|
| Backend | `Nemia-Core/` | Spring Boot 4 (Java 17) | 8080 |
| Frontend | `Nemia-View/` | Angular 21 (TypeScript) | 4200 |

```
nemia-hub/
├── Nemia-Core/          # API REST Spring Boot
├── Nemia-View/          # SPA Angular standalone
├── conception_docs/     # Documentation fonctionnelle et technique
└── scripts/             # Scripts de seeding et utilitaires
```

---

## Stack technique

**Backend**
- Java 17 + Spring Boot 4.0.5
- Spring Data JPA + Bean Validation + Lombok
- SQLite (`nemia.db`, créée automatiquement au démarrage)
- Maven (wrapper `mvnw` inclus)
- Tests : JUnit 5 + Mockito (179 tests)

**Frontend**
- Angular 21 standalone (sans NgModules)
- Angular Material 21 + SCSS
- Reactive Forms exclusivement
- Locale `fr-FR` configurée globalement
- Tests : Vitest 4

---

## Démarrage rapide

### Prérequis

- Java 17+
- Node.js 20+ et npm 11+

### Backend

```bash
cd Nemia-Core
./mvnw spring-boot:run
```

L'API est disponible sur `http://localhost:8080/api`. La base SQLite `nemia.db` est créée automatiquement à la racine de `Nemia-Core/`.

### Frontend

```bash
cd Nemia-View
npm install
npm start
```

L'application est disponible sur `http://localhost:4200`.

### Données de test

Après démarrage du backend, injecter des données initiales :

```bash
# Jeu de données structuré (recommandé)
bash scripts/import-initial-data.sh

# Ou uniquement des flux en masse
bash Nemia-Core/seed_flux.sh
```

---

## Fonctionnalités

### Flux financiers

Transactions contextualisées avec : type (RECETTE / DEPENSE / MOUVEMENT_FINANCIER), catégorie (33 valeurs), mode de paiement, occurrence. Filtrage sur 9 paramètres, pagination configurable (10 / 20 / 50).

### Biens immobiliers

CRUD avec contrainte d'unicité `(nomUsuel, adresseSimplifiee)`, suivi de statut (EN_PREPARATION → ACTIF → SUSPENDU → CLOTURE), synthèse financière par bien.

### Exercices comptables

Workflow de statut OUVERT → EN_PREPARATION_DE_CLOTURE → CLOTURE. Suivi de complétude (FAIBLE / MOYEN / AVANCE / COMPLET). Synthèse en 4 blocs (identité, financier, complétude, qualification). Endpoint "exercice courant" avec logique de fallback.

### Justificatifs

Liés aux flux, avec type (FACTURE, TICKET, RELEVE, etc.) et statut documentaire (A_FOURNIR → FOURNI → INCOMPLET → A_VERIFIER → REJETE). Upload de fichiers prévu après migration PostgreSQL.

### Travaux, Mobilier, Emprunts

CRUD complet avec suivi de statut, catégorisation et fiches de synthèse financière pour chaque domaine.

### Tableau de bord

Cockpit avec synthèse mensuelle, alertes contextuelles, et navigation filtrée vers chaque domaine.

---

## API

Base URL : `http://localhost:8080/api`

| Domaine | Endpoint |
|---|---|
| Flux | `GET/POST/PUT/DELETE /flux` |
| Biens | `GET/POST/PUT/DELETE /biens` |
| Exercices | `GET/POST/PUT/DELETE /exercices` |
| Justificatifs | `GET/POST/PUT/DELETE /justificatifs` |
| Travaux | `GET/POST/PUT/DELETE /travaux` |
| Mobilier | `GET/POST/PUT/DELETE /mobilier` |
| Emprunts | `GET/POST/PUT/DELETE /emprunts` |
| Synthèse | `GET /home/synthese` |

Chaque domaine expose un endpoint `/referentials` retournant les valeurs d'enum avec libellés.

---

## Tests

```bash
# Backend (JUnit 5 + Mockito)
cd Nemia-Core
./mvnw clean test

# Frontend (Vitest)
cd Nemia-View
npm test
```

---

## Build production

```bash
# Backend
cd Nemia-Core
./mvnw clean package

# Frontend
cd Nemia-View
npm run build
# Sortie : dist/
```

---

## Configuration

### Backend (`Nemia-Core/src/main/resources/application.properties`)

```properties
spring.datasource.url=jdbc:sqlite:nemia.db
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
```

### Frontend (`Nemia-View/src/app/app.config.ts`)

```typescript
// URL de l'API
http://localhost:8080/api
```

### CORS

Le backend autorise les requêtes depuis `http://localhost:4200` pour les méthodes GET, POST, PUT, DELETE, OPTIONS.

---

## Roadmap post-MVP

- [ ] Migration PostgreSQL (débloque upload de fichiers et clés étrangères strictes)
- [ ] Upload de justificatifs (photos, PDFs)
- [ ] Objet Immobilisation
- [ ] Dossier déclaratif et export PDF
- [ ] Refonte UX du formulaire Flux (après retours utilisateurs)

---

## Documentation

La documentation conceptuelle est dans [`conception_docs/`](conception_docs/) :

- `fonctionnel/` — Spécifications métier et exigences fonctionnelles
- `Back-end/` — Architecture et conventions d'implémentation
- `front-end/` — Patterns UI/UX et guide des composants
