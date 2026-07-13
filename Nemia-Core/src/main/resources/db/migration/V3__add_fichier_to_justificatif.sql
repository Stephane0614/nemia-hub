ALTER TABLE justificatif
    ADD COLUMN fichier_nom     VARCHAR(255),
    ADD COLUMN fichier_chemin  VARCHAR(1000),
    ADD COLUMN fichier_type    VARCHAR(100),
    ADD COLUMN fichier_taille  BIGINT;