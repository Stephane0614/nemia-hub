# BACK-END — Mémoire Opérationnelle
## nemia-hub — MVP v1.1 — Checkpoint 08/05/2026

---

## 1. Architecture back-end (vue d'ensemble)

- Application Spring Boot REST
- Architecture par domaine métier — chaque domaine est autonome
- Pas d'interface de service, pas de mapper dédié, pas d'abstraction prématurée
- Gestion d'erreurs centralisée via `GlobalExceptionHandler`
- Base PostgreSQL 16 via Docker Compose
- Clés étrangères strictes sur toutes les relations
- Authentification JWT (Spring Security, stateless)
- Migrations de schéma versionnées via Flyway
- Normalisation systématique des données : trim, vide → null
- Configuration externalisée via variables d'environnement

**Domaines implémentés :**
- `flux` — événement économique élémentaire
- `bien` — bien immobilier
- `exercice` — période comptable
- `justificatif` — pièce documentaire attachée à un flux
- `travaux` — opération de chantier / amélioration
- `mobilier` — bien meuble / équipement
- `emprunt` — dette structurée
- `home` — synthèse agrégée (lecture seule)
- `auth` — authentification JWT (login, filtre, sécurité)
- `common` — exceptions partagées

---

## 2. Stack technique & versions

| Élément | Valeur |
|---|---|
| Langage | Java 17 (Amazon Corretto) |
| Framework | Spring Boot REST (4.x) |
| Persistance | Spring Data JPA + Bean Validation |
| Base de données | PostgreSQL 16 via Docker Compose (`nemia_hub`) |
| Dialecte Hibernate | `PostgreSQLDialect` (intégré) |
| Migrations | Flyway (`spring-boot-starter-flyway`) |
| Sécurité | Spring Security + JWT (HMAC-SHA256, 24h) |
| Tests | JUnit 5 + Mockito — 179 tests au vert |
| Build | Maven (`./mvnw`) |
| CI | GitHub Actions |

**Configuration `application.properties` (production) :**
```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
jwt.secret=${JWT_SECRET_KEY}
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
```

**Profil dev :** `application-dev.properties` (non versionné) — valeurs en clair pour le travail local. Lancement via `--spring.profiles.active=dev`.

**Variables d'environnement requises :**
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `JWT_SECRET_KEY` (minimum 32 caractères)
- `NEMIA_ADMIN_PASSWORD`, `NEMIA_EMILIE_PASSWORD`

**Infrastructure locale :**
- PostgreSQL 16 dans un conteneur Docker isolé (docker-compose.yml)
- Aucune interaction avec les instances PostgreSQL locales existantes

---

## 3. Structure des dossiers & fichiers principaux

```
com.nemia.core/
  flux/
    controller/   FluxController, FluxReferentialController, HomeSyntheseController
    service/      FluxService, FluxValidationService, HomeSyntheseService
    repository/   FluxRepository
    model/        Flux, FluxType, FluxCategory, PaymentMode, Occurrence,
                  StatutJustificatif, QualificationPressentie, StatutTraitement
    dto/          CreateFluxRequest, UpdateFluxRequest, FluxResponse,
                  FluxPageResponse, FluxReferentialsResponse, HomeSyntheseResponse,
                  ExerciceSyntheseResponse

  bien/
    controller/   BienController, BienReferentialController
    service/      BienService
    repository/   BienRepository
    model/        Bien, StatutActiviteBien, TypeLocation, RegimeVise
    dto/          BienRequest, BienResponse, BienReferentialsResponse

  exercice/
    controller/   ExerciceController, ExerciceReferentialController
    service/      ExerciceService
    repository/   ExerciceRepository
    model/        Exercice, StatutExercice, NiveauCompletude
    dto/          ExerciceRequest, ExerciceResponse, ExerciceReferentialsResponse

  justificatif/
    controller/   JustificatifController, JustificatifReferentialController
    service/      JustificatifService
    repository/   JustificatifRepository
    model/        Justificatif, TypePiece, StatutDocumentaire
    dto/          JustificatifRequest, JustificatifResponse,
                  JustificatifReferentialsResponse

  travaux/
    controller/   TravauxController, TravauxReferentialController
    service/      TravauxService
    repository/   TravauxRepository
    model/        Travaux, FinalitePressentie, StatutTravaux
    dto/          TravauxRequest, TravauxResponse, TravauxReferentialsResponse

  mobilier/
    controller/   MobilierController, MobilierReferentialController
    service/      MobilierService
    repository/   MobilierRepository
    model/        Mobilier, CategorieMobilier, EtatUsage, StatutMobilier
    dto/          MobilierRequest, MobilierResponse, MobilierReferentialsResponse

  emprunt/
    controller/   EmpruntController, EmpruntReferentialController
    service/      EmpruntService
    repository/   EmpruntRepository
    model/        Emprunt, StatutEmprunt
    dto/          EmpruntRequest, EmpruntResponse, EmpruntReferentialsResponse

  auth/
    controller/   AuthController (POST /api/auth/login)
    service/      AuthService
    security/     SecurityConfig, JwtUtil, JwtAuthenticationFilter
    model/        NemiaUser
    repository/   NemiaUserRepository
    dto/          LoginRequest, LoginResponse
    DataInitializer (création comptes au démarrage)

  common/
    exception/    GlobalExceptionHandler, ApiErrorResponse,
                  FluxNotFoundException, BienNotFoundException,
                  BienAlreadyExistsException, ExerciceNotFoundException,
                  ExerciceAlreadyExistsException, JustificatifNotFoundException,
                  TravauxNotFoundException, MobilierNotFoundException,
                  EmpruntNotFoundException
```

**Fichiers de migration Flyway :**
- `V0__initial_schema.sql` — création des 7 tables de base
- `V1__add_foreign_keys.sql` — FK strictes sur toutes les relations
- `V2__create_user_table.sql` — table nemia_user

**Fichier partagé entre domaines :**
- `ReferentialItemResponse` — dans `flux/dto/referential/`, réutilisé par tous les domaines

**CORS :** géré dans `SecurityConfig` (WebConfig supprimé) — autorise `http://localhost:4200` et `https://nemiahub.eu`

---

## 4. Modèles de données & schéma BDD

### Entité FLUX (table `flux`)
| Champ | Type Java | Nullable | Notes |
|---|---|---|---|
| id | Long | Non | Généré |
| date | LocalDate | Non | Type natif PostgreSQL |
| dateValeur | LocalDate | Oui | Type natif PostgreSQL |
| type | FluxType | Non | Enum STRING |
| libelle | String | Non | Max 255 |
| montant | BigDecimal | Non | precision=12, scale=2 |
| categorie | FluxCategory | Non | Enum STRING |
| modePaiement | PaymentMode | Non | Enum STRING |
| bienId | Long | Oui | FK stricte → bien.id |
| exerciceId | Long | Oui | FK stricte → exercice.id |
| justificatifId | Long | Oui | FK stricte → justificatif.id |
| travauxId | Long | Oui | FK stricte → travaux.id |
| mobilierId | Long | Oui | FK stricte → mobilier.id |
| empruntId | Long | Oui | FK stricte → emprunt.id |
| occurrence | Occurrence | Oui | Enum STRING |
| statutJustificatif | StatutJustificatif | Oui | Enum STRING |
| qualificationPressentie | QualificationPressentie | Oui | Enum STRING |
| statutTraitement | StatutTraitement | Oui | Enum STRING |
| commentaire | String | Oui | Max 1000 |
| createdAt | LocalDateTime | Non | Auto @PrePersist |
| updatedAt | LocalDateTime | Non | Auto @PreUpdate |

### Entité BIEN (table `bien`)
| Champ | Type Java | Nullable | Notes |
|---|---|---|---|
| id | Long | Non | Généré |
| nomUsuel | String | Non | Max 120 |
| adresseSimplifiee | String | Non | Max 200 |
| statutActivite | StatutActiviteBien | Non | Enum STRING |
| typeLocation | TypeLocation | Oui | Enum STRING |
| dateMiseEnLocation | LocalDate | Oui | Type natif PostgreSQL |
| regimeVise | RegimeVise | Oui | Enum STRING |
| commentaire | String | Oui | Max 500 |
| createdAt | LocalDateTime | Non | Auto |
| updatedAt | LocalDateTime | Non | Auto |

**Contrainte unicité :** `@UniqueConstraint(columnNames = {"nom_usuel", "adresse_simplifiee"})`

### Entité EXERCICE (table `exercice`)
| Champ | Type Java | Nullable | Notes |
|---|---|---|---|
| id | Long | Non | Généré |
| libelleExercice | String | Non | Max 50, unique |
| dateDebut | LocalDate | Non | Type natif PostgreSQL |
| dateFin | LocalDate | Non | Type natif PostgreSQL |
| statutExercice | StatutExercice | Non | Enum STRING |
| niveauCompletude | NiveauCompletude | Oui | Enum STRING |
| commentaire | String | Oui | Max 500 |
| createdAt | LocalDateTime | Non | Auto |
| updatedAt | LocalDateTime | Non | Auto |

### Entité JUSTIFICATIF (table `justificatif`)
| Champ | Type Java | Nullable | Notes |
|---|---|---|---|
| id | Long | Non | Généré |
| typePiece | TypePiece | Non | Enum STRING |
| statutDocumentaire | StatutDocumentaire | Non | Enum STRING |
| datePiece | LocalDate | Oui | Type natif PostgreSQL |
| referencePiece | String | Oui | Max 100 |
| emetteur | String | Oui | Max 150 |
| commentaire | String | Oui | Max 500 |
| fichierAssocie | String | Oui | Réservé upload futur |
| createdAt | LocalDateTime | Non | Auto |
| updatedAt | LocalDateTime | Non | Auto |

### Entité TRAVAUX (table `travaux`)
| Champ | Type Java | Nullable | Notes |
|---|---|---|---|
| id | Long | Non | Généré |
| libelleTravaux | String | Non | Obligatoire |
| bienId | Long | Non | FK stricte → bien.id |
| dateDebut | LocalDate | Oui | Type natif PostgreSQL |
| dateFin | LocalDate | Oui | Type natif PostgreSQL |
| montantTotal | BigDecimal | Non | Positif |
| finalitePressentie | FinalitePressentie | Non | Enum STRING |
| qualificationPressentie | QualificationPressentie | Non | Enum STRING |
| statutTravaux | StatutTravaux | Non | Enum STRING |
| commentaire | String | Oui | |
| createdAt | LocalDateTime | Non | Auto |
| updatedAt | LocalDateTime | Non | Auto |

### Entité MOBILIER (table `mobilier`)
| Champ | Type Java | Nullable | Notes |
|---|---|---|---|
| id | Long | Non | Généré |
| designation | String | Non | Obligatoire |
| bienId | Long | Non | FK stricte → bien.id |
| dateAcquisition | LocalDate | Oui | Type natif PostgreSQL |
| montant | BigDecimal | Non | Positif |
| quantite | Integer | Oui | Défaut 1, positif |
| categorieMobilier | CategorieMobilier | Non | Enum STRING |
| etatUsage | EtatUsage | Oui | Enum STRING |
| qualificationPressentie | QualificationPressentie | Non | Enum STRING |
| statutMobilier | StatutMobilier | Non | Enum STRING |
| justificatifId | Long | Oui | Validé si renseigné |
| commentaire | String | Oui | |
| createdAt | LocalDateTime | Non | Auto |
| updatedAt | LocalDateTime | Non | Auto |

### Entité EMPRUNT (table `emprunt`)
| Champ | Type Java | Nullable | Notes |
|---|---|---|---|
| id | Long | Non | Généré |
| referencePret | String | Non | Obligatoire |
| bienId | Long | Non | FK stricte → bien.id |
| organismePreteur | String | Oui | |
| mensualiteTotale | BigDecimal | Oui | Positif si renseigné |
| datePremiereEcheance | LocalDate | Oui | Type natif PostgreSQL |
| dateDerniereEcheance | LocalDate | Oui | Type natif PostgreSQL |
| statutEmprunt | StatutEmprunt | Non | Enum STRING |
| commentaire | String | Oui | |
| createdAt | LocalDateTime | Non | Auto |
| updatedAt | LocalDateTime | Non | Auto |

### Entité NEMIA_USER (table `nemia_user`)
| Champ | Type Java | Nullable | Notes |
|---|---|---|---|
| id | Long | Non | Généré |
| username | String | Non | Unique |
| passwordHash | String | Non | BCrypt |
| createdAt | LocalDateTime | Non | Auto |

---

## 5. Endpoints / API implémentés

### AUTHENTIFICATION
| Route | Description | Statut |
|---|---|---|
| `POST /api/auth/login` | Login — retourne JWT | ✅ |

> Toutes les routes `/api/**` sont protégées sauf `/api/auth/login`.
> Header requis : `Authorization: Bearer <token>`

### FLUX
| Route | Description | Statut |
|---|---|---|
| `GET /api/flux` | Liste paginée, 9 filtres + page/size | ✅ |
| `GET /api/flux/{id}` | Flux par id | ✅ |
| `POST /api/flux` | Création | ✅ |
| `PUT /api/flux/{id}` | Modification | ✅ |
| `DELETE /api/flux/{id}` | Suppression | ✅ |
| `GET /api/flux/referentials` | Enums Flux | ✅ |

### BIEN
| Route | Description | Statut |
|---|---|---|
| `GET /api/biens` | Liste biens | ✅ |
| `GET /api/biens/{id}` | Bien par id | ✅ |
| `POST /api/biens` | Création | ✅ |
| `PUT /api/biens/{id}` | Modification | ✅ |
| `DELETE /api/biens/{id}` | Suppression | ✅ |
| `GET /api/biens/referentials` | Enums Bien | ✅ |

### EXERCICE
| Route | Description | Statut |
|---|---|---|
| `GET /api/exercices` | Liste exercices | ✅ |
| `GET /api/exercices/{id}` | Exercice par id | ✅ |
| `GET /api/exercices/en-cours` | Exercice en cours avec fallback | ✅ |
| `GET /api/exercices/{id}/synthese` | Synthèse 4 blocs | ✅ |
| `POST /api/exercices` | Création | ✅ |
| `PUT /api/exercices/{id}` | Modification | ✅ |
| `DELETE /api/exercices/{id}` | Suppression | ✅ |
| `GET /api/exercices/referentials` | Enums Exercice | ✅ |

### JUSTIFICATIF
| Route | Description | Statut |
|---|---|---|
| `GET /api/justificatifs` | Liste justificatifs | ✅ |
| `GET /api/justificatifs/{id}` | Justificatif par id | ✅ |
| `POST /api/justificatifs` | Création | ✅ |
| `PUT /api/justificatifs/{id}` | Modification | ✅ |
| `DELETE /api/justificatifs/{id}` | Suppression | ✅ |
| `GET /api/justificatifs/referentials` | Enums Justificatif | ✅ |

### TRAVAUX
| Route | Description | Statut |
|---|---|---|
| `GET /api/travaux` | Liste — filtre optionnel bienId | ✅ |
| `GET /api/travaux/{id}` | Travaux par id | ✅ |
| `POST /api/travaux` | Création | ✅ |
| `PUT /api/travaux/{id}` | Modification | ✅ |
| `DELETE /api/travaux/{id}` | Suppression | ✅ |
| `GET /api/travaux/referentials` | Enums Travaux | ✅ |

### MOBILIER
| Route | Description | Statut |
|---|---|---|
| `GET /api/mobilier` | Liste — filtre optionnel bienId | ✅ |
| `GET /api/mobilier/{id}` | Mobilier par id | ✅ |
| `POST /api/mobilier` | Création | ✅ |
| `PUT /api/mobilier/{id}` | Modification | ✅ |
| `DELETE /api/mobilier/{id}` | Suppression | ✅ |
| `GET /api/mobilier/referentials` | Enums Mobilier | ✅ |

### EMPRUNT
| Route | Description | Statut |
|---|---|---|
| `GET /api/emprunts` | Liste — filtre optionnel bienId | ✅ |
| `GET /api/emprunts/{id}` | Emprunt par id | ✅ |
| `POST /api/emprunts` | Création | ✅ |
| `PUT /api/emprunts/{id}` | Modification | ✅ |
| `DELETE /api/emprunts/{id}` | Suppression | ✅ |
| `GET /api/emprunts/referentials` | Enums Emprunt | ✅ |

### HOME SYNTHÈSE
| Route | Description | Statut |
|---|---|---|
| `GET /api/home/synthese` | Synthèse mois en cours | ✅ |
| `GET /api/home/synthese?mois=yyyy-MM` | Synthèse par mois | ✅ |
| `GET /api/home/synthese?bienId=1` | Filtre par bien | ✅ |
| `GET /api/home/synthese?exerciceId=1` | Filtre par exercice | ✅ |

---

## 6. Logique métier & règles importantes

### Validation Flux — règles bloquantes (HTTP 400)
```
RECETTE + catégorie de dépense/mouvement → interdit
DEPENSE + catégorie de recette/mouvement → interdit
MOUVEMENT_FINANCIER + catégorie de charge/recette → interdit
justificatifId renseigné + justificatif inexistant → interdit
travauxId renseigné + travaux inexistant → interdit
mobilierId renseigné + mobilier inexistant → interdit
empruntId renseigné + emprunt inexistant → interdit
```

### Validation Flux — warnings non bloquants
```
W1 — IMMOBILISATION + catégorie charge courante manifeste
W2 — CHARGE_COURANTE + catégorie travaux/mobilier
W3 — NON_REQUIS + dépense exploitation courante
W4 — PONCTUEL + catégorie structurellement récurrente
W5 — qualificationPressentie = A_ARBITRER
W6 — statutTraitement = A_REVOIR
W7 — REGULARISATION (typeFlux ou categorie)
W8 — MOUVEMENT_FINANCIER + CHARGE_COURANTE ou IMMOBILISATION
```

### Validation Bien — unicité
```
nomUsuel + adresseSimplifiee → unique
Doublon → HTTP 409
```

### Validation Exercice
```
dateFin <= dateDebut → HTTP 400
libelleExercice doublon → HTTP 409
Warnings : CLOTURE + !COMPLET, EN_PREPARATION_DE_CLOTURE + FAIBLE
```

### Validation Travaux
```
dateFin < dateDebut si les deux renseignées → HTTP 400
```

### Validation Emprunt
```
dateDerniereEcheance < datePremiereEcheance si les deux → HTTP 400
```

### Home synthèse — logique de période
```
exerciceId fourni + trouvé → période exercice, mois ignoré
exerciceId introuvable → fallback mois en cours
mois fourni seul → comportement standard
aucun paramètre → mois en cours
```

### Exercice en cours — logique de fallback
```
Exercice OUVERT couvrant la date du jour → retourné
Sinon dernier exercice OUVERT → retourné
Sinon → HTTP 404
```

---

## 7. Conventions & patterns adoptés

### Pattern test
```java
@ExtendWith(MockitoExtension.class)
class MonControllerTest {
  @Mock private MonService monService;
  @InjectMocks private MonController monController;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders
      .standaloneSetup(monController)
      .setControllerAdvice(new GlobalExceptionHandler())
      .build();
  }
}
```
**Jamais de `@WebMvcTest`.**

### Pattern référentiel
```java
@GetMapping("/api/domaine/referentials")
public DomaineReferentialsResponse getReferentials() {
  List<ReferentialItemResponse> valeurs = Arrays.stream(MonEnum.values())
    .map(v -> new ReferentialItemResponse(v.name(), v.getLabel()))
    .toList();
  return new DomaineReferentialsResponse(valeurs);
}
```

### Normalisation texte
```java
private String normalizeOptionalText(String value) {
  if (value == null) return null;
  String trimmed = value.trim();
  return trimmed.isEmpty() ? null : trimmed;
}
```

### Gestion dates PostgreSQL
```java
// PostgreSQL gère nativement LocalDate et LocalDateTime
// Plus besoin de converter — annotation simple
private LocalDate maDate;
```
> `LocalDateStringConverter` supprimé lors de la migration v1.1

### Anti-doublon repository
```java
boolean existsByChampA(String champA);
boolean existsByChampAAndIdNot(String champA, Long id);
```

---

## 8. Enums complets par domaine

**FluxType :** `RECETTE`, `DEPENSE`, `MOUVEMENT_FINANCIER`, `REGULARISATION`

**FluxCategory (33 valeurs) :** `LOYER`, `CHARGES_REFACTUREES`, `INDEMNITE_RECUE`, `AUTRE_RECETTE_EXPLOITATION`, `ELECTRICITE`, `EAU`, `INTERNET`, `ASSURANCE`, `TAXE`, `COPROPRIETE`, `FRAIS_BANCAIRES`, `HONORAIRES`, `ENTRETIEN_COURANT`, `CONSOMMABLES`, `MENAGE`, `FOURNITURES`, `AUTRE_CHARGE_EXPLOITATION`, `TRAVAUX`, `REPARATION_IMPORTANTE`, `AMELIORATION`, `MOBILIER`, `ELECTROMENAGER`, `EQUIPEMENT`, `DECORATION`, `FRAIS_FINANCEMENT`, `EMPRUNT_INTERETS`, `EMPRUNT_ASSURANCE`, `EMPRUNT_CAPITAL`, `APPORT`, `RETRAIT`, `VIREMENT_INTERNE`, `REGULARISATION`, `AUTRE`

**PaymentMode :** `VIREMENT`, `CARTE`, `PRELEVEMENT`, `CHEQUE`, `ESPECES`, `AUTRE`

**Occurrence :** `RECURRENT`, `PONCTUEL`, `INDETERMINE`

**StatutJustificatif :** `NON_REQUIS`, `A_FOURNIR`, `FOURNI`, `INCOMPLET`, `A_VERIFIER`, `REJETE`

**QualificationPressentie :** `CHARGE_COURANTE`, `IMMOBILISATION`, `MIXTE_OU_A_VENTILER`, `HORS_RESULTAT`, `A_ARBITRER`, `NON_APPLICABLE`

**StatutTraitement :** `BRUT`, `QUALIFIE`, `A_REVOIR`, `VALIDE`

**StatutActiviteBien :** `EN_PREPARATION`, `ACTIF`, `SUSPENDU`, `CLOTURE`

**TypeLocation :** `LMNP_LONGUE_DUREE`, `LMNP_COURTE_DUREE`, `MIXTE`, `AUTRE`

**RegimeVise :** `MICRO_BIC`, `REEL`, `A_DEFINIR`

**StatutExercice :** `OUVERT`, `EN_PREPARATION_DE_CLOTURE`, `CLOTURE`

**NiveauCompletude :** `FAIBLE`, `MOYEN`, `AVANCE`, `COMPLET`

**TypePiece :** `FACTURE`, `TICKET`, `RELEVE`, `ECHEANCIER`, `ACTE`, `DEVIS`, `AUTRE`

**StatutDocumentaire :** `A_FOURNIR`, `FOURNI`, `INCOMPLET`, `A_VERIFIER`, `REJETE`

**FinalitePressentie :** `ENTRETIEN_COURANT`, `REPARATION`, `AMELIORATION`, `CREATION`, `REMISE_EN_ETAT`, `A_ARBITRER`

**StatutTravaux :** `BRUT`, `QUALIFIE`, `A_REVOIR`, `VALIDE`

**CategorieMobilier :** `LITERIE`, `ELECTROMENAGER`, `MEUBLE`, `EQUIPEMENT`, `DECORATION`, `AUTRE`

**EtatUsage :** `NEUF`, `OCCASION`, `REMPLACEMENT`, `INDETERMINE`

**StatutMobilier :** `BRUT`, `QUALIFIE`, `A_REVOIR`, `VALIDE`

**StatutEmprunt :** `EN_COURS`, `TERMINE`, `SUSPENDU`, `A_VERIFIER`

---

## 9. Points bloquants & dette technique

| Point | Type | Statut |
|---|---|---|
| Upload fichier justificatif | Dette fonctionnelle | Débloqué par PostgreSQL — à implémenter |
| Upload photo Bien | Dette fonctionnelle | Débloqué par PostgreSQL — à implémenter |
| `niveauConfiance` dans Flux | Reporté | Futur |
| Pagination Travaux/Mobilier/Emprunt | À faire | Futur si besoin |
| Tests unitaires violations FK | Dette technique | Non écrits lors mission #32 |
| Erreur 500 "Frais de notaire refinancement" | À investiguer | Payload suspect |
| OWASP Dependency-Check pipeline | Abandonné | Trop lent — Trivy à envisager |
| Ubuntu 25.04 non-LTS sur VPS | Infra | Upgrade vers 26.04 LTS à prévoir |

**Dettes résolues en v1.1 :**
- ~~Cohérence référentielle FK~~ → FK strictes en place (#32)
- ~~Clés étrangères strictes~~ → Flyway V1 (#32)
- ~~Secrets en dur dans le code~~ → Variables d'environnement (#35)

---

## 10. Prochaines tâches back-end

### Après retours d'utilisation
| Priorité | Chantier |
|---|---|
| 1 | Upload fichiers justificatifs |
| 2 | Upload photo Bien |
| 3 | Refonte UX FluxForm (backend si impact) |
| 4 | Objet Immobilisation |
| 5 | Indicateur "prêt à clôturer" exercice |
| 6 | Export PDF synthèse exercice |
| 7 | niveauConfiance dans Flux |

---

*Checkpoint mis à jour le 08/05/2026 — MVP v1.1 déployé sur https://nemiahub.eu*
