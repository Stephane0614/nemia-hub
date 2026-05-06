ALTER TABLE flux
    ADD CONSTRAINT fk_flux_bien
        FOREIGN KEY (bien_id) REFERENCES bien(id),
    ADD CONSTRAINT fk_flux_exercice
        FOREIGN KEY (exercice_id) REFERENCES exercice(id),
    ADD CONSTRAINT fk_flux_justificatif
        FOREIGN KEY (justificatif_id) REFERENCES justificatif(id),
    ADD CONSTRAINT fk_flux_travaux
        FOREIGN KEY (travaux_id) REFERENCES travaux(id),
    ADD CONSTRAINT fk_flux_mobilier
        FOREIGN KEY (mobilier_id) REFERENCES mobilier(id),
    ADD CONSTRAINT fk_flux_emprunt
        FOREIGN KEY (emprunt_id) REFERENCES emprunt(id);

ALTER TABLE travaux
    ADD CONSTRAINT fk_travaux_bien
        FOREIGN KEY (bien_id) REFERENCES bien(id);

ALTER TABLE mobilier
    ADD CONSTRAINT fk_mobilier_bien
        FOREIGN KEY (bien_id) REFERENCES bien(id);

ALTER TABLE emprunt
    ADD CONSTRAINT fk_emprunt_bien
        FOREIGN KEY (bien_id) REFERENCES bien(id);