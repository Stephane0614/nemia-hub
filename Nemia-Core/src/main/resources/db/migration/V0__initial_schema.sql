CREATE TABLE bien (
    id                    BIGSERIAL    PRIMARY KEY,
    nom_usuel             VARCHAR(120) NOT NULL,
    adresse_simplifiee    VARCHAR(200) NOT NULL,
    statut_activite       VARCHAR(50)  NOT NULL,
    type_location         VARCHAR(50),
    date_mise_en_location DATE,
    regime_vise           VARCHAR(50),
    commentaire           VARCHAR(500),
    created_at            TIMESTAMP    NOT NULL,
    updated_at            TIMESTAMP    NOT NULL,
    CONSTRAINT uq_bien_nom_adresse UNIQUE (nom_usuel, adresse_simplifiee)
);

CREATE TABLE exercice (
    id                BIGSERIAL   PRIMARY KEY,
    libelle_exercice  VARCHAR(50) NOT NULL,
    date_debut        DATE        NOT NULL,
    date_fin          DATE        NOT NULL,
    statut_exercice   VARCHAR(50) NOT NULL,
    niveau_completude VARCHAR(50),
    commentaire       VARCHAR(500),
    created_at        TIMESTAMP   NOT NULL,
    updated_at        TIMESTAMP   NOT NULL,
    CONSTRAINT uq_exercice_libelle UNIQUE (libelle_exercice)
);

CREATE TABLE justificatif (
    id                  BIGSERIAL    PRIMARY KEY,
    type_piece          VARCHAR(50)  NOT NULL,
    statut_documentaire VARCHAR(50)  NOT NULL,
    date_piece          DATE,
    reference_piece     VARCHAR(100),
    emetteur            VARCHAR(150),
    commentaire         VARCHAR(500),
    fichier_associe     VARCHAR(255),
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL
);

CREATE TABLE emprunt (
    id                     BIGSERIAL     PRIMARY KEY,
    reference_pret         VARCHAR(100)  NOT NULL,
    bien_id                BIGINT        NOT NULL,
    organisme_preteur      VARCHAR(150),
    mensualite_totale      DECIMAL(12,2),
    date_premiere_echeance DATE,
    date_derniere_echeance DATE,
    statut_emprunt         VARCHAR(50)   NOT NULL,
    commentaire            VARCHAR(500),
    created_at             TIMESTAMP     NOT NULL,
    updated_at             TIMESTAMP     NOT NULL
);

CREATE TABLE travaux (
    id                       BIGSERIAL     PRIMARY KEY,
    libelle_travaux          VARCHAR(200)  NOT NULL,
    bien_id                  BIGINT,
    date_debut               DATE,
    date_fin                 DATE,
    montant_total            DECIMAL(12,2) NOT NULL,
    finalite_pressentie      VARCHAR(50)   NOT NULL,
    qualification_pressentie VARCHAR(50)   NOT NULL,
    statut_travaux           VARCHAR(50),
    commentaire              VARCHAR(500),
    created_at               TIMESTAMP     NOT NULL,
    updated_at               TIMESTAMP     NOT NULL
);

CREATE TABLE mobilier (
    id                       BIGSERIAL     PRIMARY KEY,
    designation              VARCHAR(200)  NOT NULL,
    bien_id                  BIGINT        NOT NULL,
    date_acquisition         DATE,
    montant                  DECIMAL(12,2) NOT NULL,
    quantite                 INTEGER,
    categorie_mobilier       VARCHAR(50)   NOT NULL,
    etat_usage               VARCHAR(50),
    qualification_pressentie VARCHAR(50)   NOT NULL,
    statut_mobilier          VARCHAR(50)   NOT NULL,
    justificatif_id          BIGINT,
    commentaire              VARCHAR(500),
    created_at               TIMESTAMP     NOT NULL,
    updated_at               TIMESTAMP     NOT NULL
);

CREATE TABLE flux (
    id                       BIGSERIAL     PRIMARY KEY,
    date                     DATE          NOT NULL,
    type                     VARCHAR(50)   NOT NULL,
    libelle                  VARCHAR(255)  NOT NULL,
    montant                  DECIMAL(12,2) NOT NULL,
    categorie                VARCHAR(50)   NOT NULL,
    mode_paiement            VARCHAR(50)   NOT NULL,
    commentaire              VARCHAR(1000),
    created_at               TIMESTAMP     NOT NULL,
    updated_at               TIMESTAMP     NOT NULL,
    bien_id                  BIGINT,
    exercice_id              BIGINT,
    date_valeur              DATE,
    occurrence               VARCHAR(50),
    statut_justificatif      VARCHAR(50),
    qualification_pressentie VARCHAR(50),
    statut_traitement        VARCHAR(50),
    justificatif_id          BIGINT,
    travaux_id               BIGINT,
    mobilier_id              BIGINT,
    emprunt_id               BIGINT
);
