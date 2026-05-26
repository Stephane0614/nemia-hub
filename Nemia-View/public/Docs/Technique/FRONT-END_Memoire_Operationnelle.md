# FRONT-END — Mémoire Opérationnelle
## nemia-hub — MVP v1.1 — Checkpoint 08/05/2026

---

## 1. Architecture front-end (vue d'ensemble)

- Application Angular standalone, pas de modules NgModule
- Architecture par **feature** — chaque domaine métier est un dossier autonome
- Pas de state management global — état local par composant
- Communication backend via services HTTP dédiés par feature
- Angular Material comme bibliothèque UI unique
- Locale `fr-FR` configurée globalement
- Authentification JWT : interceptor HTTP + guard de routes
- Page de login dédiée, toolbar conditionnelle

---

## 2. Stack technique & versions

| Élément | Valeur |
|---|---|
| Framework | Angular 21.2.0 standalone |
| Langage | TypeScript |
| Style | SCSS par composant |
| UI | Angular Material |
| Formulaires | Reactive Forms uniquement |
| HTTP | HttpClient + AuthInterceptor |
| State management | Aucun |
| Node.js | 22.19.0 (identique sur toutes les machines) |
| npm | 10.9.3 |
| Backend URL de base (dev) | `/api` via proxy Angular CLI |
| Backend URL de base (prod) | `/api` via Nginx reverse proxy |

**Configuration locale globale dans `app.config.ts` :**
```typescript
providers: [
  provideRouter(routes),
  provideHttpClient(withInterceptors([authInterceptor])),
  provideNativeDateAdapter(),
  { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' },
  { provide: LOCALE_ID, useValue: 'fr-FR' },
]
```

**Proxy dev local :** `proxy.conf.json` redirige `/api` vers `localhost:8080` via Angular CLI (configuration dans `angular.json` → proxyConfig).

---

## 3. Structure des dossiers & composants principaux

```
src/app/
  app.config.ts
  app.routes.ts
  app.ts / app.html / app.scss

  core/
    api/
      api.config.ts                  — API_BASE_URL = '/api'
    auth/
      auth.service.ts                — login(), logout(), getToken(), getUsername(), isAuthenticated()
      auth.interceptor.ts            — injecte Bearer token, redirect 401
      auth.guard.ts                  — protège les routes, redirect /login

  features/
    auth/
      login/
        login.ts / login.html / login.scss

    home/
      pages/home/
        home.ts / home.html / home.scss
        home-synthese-api.ts
        models/home-synthese.ts

    flux/
      flux.routes.ts
      models/
        flux-request.ts / flux-response.ts / flux-page-response.ts
        flux-type.ts / flux-category.ts
        flux-enums.ts                — Occurrence, StatutJustificatif, QualificationPressentie, StatutTraitement
        flux-referentials-response.ts
        flux-filters.ts              — FluxFilters interface (9 filtres)
        payment-mode.ts / referential-item.ts / api-error-response.ts
      pages/
        flux-list/flux-list.ts / .html / .scss
        flux-form/flux-form.ts / .html / .scss
      services/flux-api.ts

    bien/
      bien.routes.ts
      models/
        bien-request.ts / bien-response.ts / bien-referentials-response.ts
        statut-activite-bien.ts / type-location.ts / regime-vise.ts
      pages/
        bien-list/ / bien-form/ / bien-detail/
      services/bien-api.ts

    exercice/
      exercice.routes.ts
      models/
        exercice-request.ts / exercice-response.ts / exercice-referentials-response.ts
        exercice-synthese-response.ts
        statut-exercice.ts / niveau-completude.ts
      pages/
        exercice-list/ / exercice-form/ / exercice-synthese/
      services/exercice-api.ts

    justificatif/
      models/
        justificatif-request.ts / justificatif-response.ts
        justificatif-referentials-response.ts
        type-piece.ts / statut-documentaire.ts
      pages/
        justificatif-form/           — utilisable en page et en dialog modale
      services/justificatif-api.ts

    travaux/
      travaux.routes.ts
      models/
        travaux-request.ts / travaux-response.ts / travaux-referentials-response.ts
        finalite-pressentie.ts / statut-travaux.ts
      pages/
        travaux-list/ / travaux-form/    — form utilisable en page et dialog
      services/travaux-api.ts

    mobilier/
      mobilier.routes.ts
      models/
        mobilier-request.ts / mobilier-response.ts / mobilier-referentials-response.ts
        categorie-mobilier.ts / etat-usage.ts / statut-mobilier.ts
      pages/
        mobilier-list/ / mobilier-form/   — form utilisable en page et dialog
      services/mobilier-api.ts

    emprunt/
      emprunt.routes.ts
      models/
        emprunt-request.ts / emprunt-response.ts / emprunt-referentials-response.ts
        statut-emprunt.ts
      pages/
        emprunt-list/ / emprunt-form/    — form utilisable en page et dialog
      services/emprunt-api.ts

  shared/ui/
    confirm-dialog/confirm-dialog.ts
```

**Token JWT :** stocké en localStorage (clés `nemia_token`, `nemia_username`).

---

## 4. Pages & routing implémentés

| Page | Route | Protégée | Statut |
|---|---|---|---|
| Login | `/login` | Non | ✅ |
| Home cockpit | `/` | Oui | ✅ |
| FluxList (paginée, 8 filtres UI) | `/flux` | Oui | ✅ |
| FluxForm création | `/flux/nouveau` | Oui | ✅ |
| FluxForm édition | `/flux/:id/modifier` | Oui | ✅ |
| BienList | `/biens` | Oui | ✅ |
| BienForm création | `/biens/nouveau` | Oui | ✅ |
| BienDetail | `/biens/:id` | Oui | ✅ |
| BienForm édition | `/biens/:id/modifier` | Oui | ✅ |
| ExerciceList | `/exercices` | Oui | ✅ |
| ExerciceForm création | `/exercices/nouveau` | Oui | ✅ |
| ExerciceForm édition | `/exercices/:id/modifier` | Oui | ✅ |
| ExerciceSynthese | `/exercice` | Oui | ✅ |
| JustificatifForm création | `/justificatifs/nouveau` | Oui | ✅ |
| JustificatifForm édition | `/justificatifs/:id/modifier` | Oui | ✅ |
| TravauxList | `/travaux` | Oui | ✅ |
| TravauxForm création | `/travaux/nouveau` | Oui | ✅ |
| TravauxForm édition | `/travaux/:id/modifier` | Oui | ✅ |
| MobilierList | `/mobilier` | Oui | ✅ |
| MobilierForm création | `/mobilier/nouveau` | Oui | ✅ |
| MobilierForm édition | `/mobilier/:id/modifier` | Oui | ✅ |
| EmpruntList | `/emprunts` | Oui | ✅ |
| EmpruntForm création | `/emprunts/nouveau` | Oui | ✅ |
| EmpruntForm édition | `/emprunts/:id/modifier` | Oui | ✅ |

**Guard :** `authGuard` via `canActivate` sur toutes les routes sauf `/login`.

**Shell applicatif :** toolbar conditionnelle — masquée sur `/login`, visible partout ailleurs avec username affiché et bouton déconnexion.

**Navigation principale (toolbar) :** Home, Opérations, Biens, Exercices, Synthèse, Travaux, Mobilier, Emprunts

---

## 5. Composants clés & leur rôle

### Home (`home.ts`)
- Cockpit patrimonial — appelle `GET /api/home/synthese`
- Select Bien pour filtrer la synthèse par bien
- Navigation entre mois via chevrons
- Alertes cliquables → FluxList filtrée avec query params
- bienId actif propagé dans les navigations

### FluxList (`flux-list.ts`)
- Liste paginée avec MatPaginator (20/10/50)
- 8 filtres UI : bien, exercice, type flux, catégorie, statut justificatif, qualification, statut traitement, période
- Bandeau "Filtré par" + bouton effacer
- Badges colorés par 6 familles de catégorie
- Query params cohérents avec filtres UI
- Changement de filtre remet pagination à page 0

### FluxForm (`flux-form.ts`)
- Création + édition dans le même composant
- Selects dynamiques : bienId, exerciceId, justificatifId, travauxId, mobilierId, empruntId
- Boutons dialog modale pour créer justificatif/travaux/mobilier/emprunt sans quitter le formulaire
- Passe en mode édition après premier create réussi avec warnings (fix doublon)
- Zone upload réservée "disponible prochainement"
- Affichage warnings backend non bloquants

### BienDetail (`bien-detail.ts`)
- Synthèse financière branchée sur Home synthèse API
- Alertes naviguent vers `/flux?bienId=X&...`

### ExerciceSynthese (`exercice-synthese.ts`)
- 4 blocs : identité, financier, complétude, qualification
- Chargement auto exercice en cours
- Select navigation entre exercices
- Lignes qualification à zéro masquées

### Formulaires objets riches (Travaux, Mobilier, Emprunt)
- Tous utilisables en page et en dialog modale via injection optionnelle MatDialogRef/MAT_DIALOG_DATA
- Pattern identique : à la fermeture dialog, rafraîchir le select parent et présélectionner l'élément créé

---

## 6. Gestion d'état

Pas de state management global. État local uniquement :
- `ChangeDetectorRef.detectChanges()` après chaque appel async
- Navigation filtrée via query params Angular

---

## 7. Appels API & intégration back-end

### FluxApi
```typescript
getAll(filters?: FluxFilters, page?: number, size?: number): Observable<FluxPageResponse>
getById(id: number): Observable<FluxResponse>
create(payload: FluxRequest): Observable<FluxResponse>
update(id: number, payload: FluxRequest): Observable<FluxResponse>
delete(id: number): Observable<void>
getReferentials(): Observable<FluxReferentialsResponse>
```

**FluxFilters (9 filtres) :**
```typescript
export interface FluxFilters {
  bienId?: number;
  exerciceId?: number;
  typeFlux?: string;
  categorie?: string;
  statutJustificatif?: string[];
  qualificationPressentie?: string;
  statutTraitement?: string;
  dateDebut?: string;
  dateFin?: string;
}
```

**FluxPageResponse :**
```typescript
export interface FluxPageResponse {
  contenu: FluxResponse[];
  page: number;
  taille: number;
  totalElements: number;
  totalPages: number;
}
```

### ExerciceApi (étendu)
```typescript
getSynthese(id: number): Observable<ExerciceSyntheseResponse>
getEnCours(): Observable<ExerciceResponse>
```

### Services par feature
Chaque feature a son propre service : BienApi, ExerciceApi, JustificatifApi, TravauxApi, MobilierApi, EmpruntApi, HomeSyntheseApi.

---

## 8. Conventions & patterns adoptés

### Locale française — obligatoire dans tout nouveau composant
```typescript
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
constructor() { registerLocaleData(localeFr); }
```

### Pipe date — toujours avec format explicite
```html
{{ flux.date | date:'dd/MM/yyyy' }}
```

### Dialog modale — pattern injection optionnelle
```typescript
// Permet au composant d'être utilisé en page et en dialog sans duplication
constructor(
  @Optional() private dialogRef: MatDialogRef<MonComponent>,
  @Optional() @Inject(MAT_DIALOG_DATA) public data: any
) {}
```

### Badges colorés — 6 familles FluxCategory
- Recettes (vert), Charges courantes (bleu), Travaux/immobilisations (orange)
- Financement (violet), Mouvements financiers (gris), Divers (gris clair)

### Filtres appearance
- `appearance="fill"` pour les champs filtres FluxList

### Qualifications pressenties
- Définies en dur dans les composants Travaux/Mobilier — pas de dépendance référentiel flux

---

## 9. Points bloquants & dette technique

| Point | Type | Statut |
|---|---|---|
| Refonte UX FluxForm | Dette assumée | Après retours utilisateur |
| Upload photo Bien | Débloqué par PostgreSQL | À implémenter |
| Upload fichiers justificatifs | Débloqué par PostgreSQL | À implémenter |
| returnTo encore en place JustificatifForm | Code mort | À nettoyer |
| Filtre exerciceId Home | Décision produit | Page dédiée future |
| Pagination Travaux/Mobilier/Emprunt | À faire | Si besoin après retours |
| FinalitePressentie en dur dans composants | Pattern assumé | À surveiller |

**Dettes résolues en v1.1 :**
- ~~Cohérence référentielle FK~~ → FK strictes côté backend
- ~~Pas d'interceptor HTTP global~~ → AuthInterceptor en place

---

## 10. Prochaines tâches front-end

### Après retours d'utilisation
| Priorité | Chantier |
|---|---|
| 1 | Refonte UX FluxForm — simplifier selon retours |
| 2 | Upload fichiers justificatifs (UI) |
| 3 | Upload photo Bien (UI) |
| 4 | Export PDF synthèse exercice |
| 5 | Page archive exercices clos |
| 6 | Indicateur "prêt à clôturer" |
| 7 | Nettoyage code mort returnTo |

### Long terme
- Objet Immobilisation
- Dossier déclaratif / préparation fiscale
- Multi-biens avancé

---

*Checkpoint mis à jour le 08/05/2026 — MVP v1.1 déployé sur https://nemiahub.eu*
