package com.nemia.core.flux.service;

import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.Occurrence;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class FluxValidationService {

    private static final Set<FluxCategory> CATEGORIES_RECETTE = Set.of(
            FluxCategory.LOYER
    );

    private static final Set<FluxCategory> CATEGORIES_DEPENSE = Set.of(
            FluxCategory.CHARGES_COPRO,
            FluxCategory.ELECTRICITE,
            FluxCategory.EAU,
            FluxCategory.INTERNET,
            FluxCategory.ASSURANCE,
            FluxCategory.INTERETS_EMPRUNT,
            FluxCategory.TRAVAUX,
            FluxCategory.MOBILIER,
            FluxCategory.FRAIS_BANCAIRES,
            FluxCategory.TAXE_FONCIERE,
            FluxCategory.HONORAIRES,
            FluxCategory.CONSOMMABLES
    );

    private static final Set<FluxCategory> CATEGORIES_COURANTES = Set.of(
            FluxCategory.CHARGES_COPRO,
            FluxCategory.ELECTRICITE,
            FluxCategory.EAU,
            FluxCategory.INTERNET,
            FluxCategory.ASSURANCE,
            FluxCategory.FRAIS_BANCAIRES,
            FluxCategory.TAXE_FONCIERE,
            FluxCategory.HONORAIRES,
            FluxCategory.CONSOMMABLES
    );

    private static final Set<FluxCategory> CATEGORIES_RECURRENTES = Set.of(
            FluxCategory.CHARGES_COPRO,
            FluxCategory.ELECTRICITE,
            FluxCategory.EAU,
            FluxCategory.INTERNET,
            FluxCategory.ASSURANCE,
            FluxCategory.INTERETS_EMPRUNT,
            FluxCategory.TAXE_FONCIERE
    );

    /**
     * Lève une exception si une règle bloquante est violée.
     * Retourne la liste des warnings non bloquants.
     */
    public List<String> validate(FluxType type, FluxCategory categorie,
                              QualificationPressentie qualification,
                              StatutJustificatif statutJustificatif,
                              Occurrence occurrence,
                              StatutTraitement statutTraitement) {

    validateBlocking(type, categorie);
    return collectWarnings(type, categorie, qualification, statutJustificatif, occurrence, statutTraitement);
}

    private void validateBlocking(FluxType type, FluxCategory categorie) {
        if (type == null || categorie == null) return;

        if (type == FluxType.RECETTE && CATEGORIES_DEPENSE.contains(categorie)) {
            throw new IllegalArgumentException(
                    "Incohérence bloquante : une RECETTE ne peut pas avoir la catégorie " + categorie.getLabel()
            );
        }

        if (type == FluxType.DEPENSE && CATEGORIES_RECETTE.contains(categorie)) {
            throw new IllegalArgumentException(
                    "Incohérence bloquante : une DEPENSE ne peut pas avoir la catégorie " + categorie.getLabel()
            );
        }

        if (type == FluxType.MOUVEMENT_FINANCIER && CATEGORIES_COURANTES.contains(categorie)) {
    throw new IllegalArgumentException(
            "Incohérence bloquante : un MOUVEMENT_FINANCIER ne peut pas avoir une catégorie de charge courante : " + categorie.getLabel()
    );
}

if (type == FluxType.MOUVEMENT_FINANCIER && CATEGORIES_RECETTE.contains(categorie)) {
    throw new IllegalArgumentException(
            "Incohérence bloquante : un MOUVEMENT_FINANCIER ne peut pas avoir une catégorie de recette : " + categorie.getLabel()
    );
}
    }

   private List<String> collectWarnings(FluxType type, FluxCategory categorie,
                                      QualificationPressentie qualification,
                                      StatutJustificatif statutJustificatif,
                                      Occurrence occurrence,
                                      StatutTraitement statutTraitement) {
    List<String> warnings = new ArrayList<>();

    if (qualification == QualificationPressentie.IMMOBILISATION
            && categorie != null
            && CATEGORIES_COURANTES.contains(categorie)) {
        warnings.add("Qualification IMMOBILISATION suspecte pour une catégorie de charge courante : " + categorie.getLabel());
    }

    if (qualification == QualificationPressentie.CHARGE_COURANTE
            && categorie != null
            && (categorie == FluxCategory.TRAVAUX || categorie == FluxCategory.MOBILIER)) {
        warnings.add("Qualification CHARGE_COURANTE suspecte pour la catégorie " + categorie.getLabel());
    }

    if (statutJustificatif == StatutJustificatif.NON_REQUIS
            && type == FluxType.DEPENSE
            && categorie != null
            && CATEGORIES_COURANTES.contains(categorie)) {
        warnings.add("Justificatif NON_REQUIS inhabituel pour une dépense d'exploitation courante : " + categorie.getLabel());
    }

    if (occurrence == Occurrence.PONCTUEL
            && categorie != null
            && CATEGORIES_RECURRENTES.contains(categorie)) {
        warnings.add("Occurrence PONCTUEL suspecte pour une catégorie structurellement récurrente : " + categorie.getLabel());
    }

    if (qualification == QualificationPressentie.A_ARBITRER) {
        warnings.add("Qualification A_ARBITRER : ce flux nécessite un arbitrage comptable/fiscal.");
    }

    if (statutTraitement == StatutTraitement.A_REVOIR) {
        warnings.add("Statut A_REVOIR : ce flux doit être relu avant validation.");
    }

    if (qualification == QualificationPressentie.A_ARBITRER) {
    warnings.add("Qualification A_ARBITRER : ce flux nécessite un arbitrage comptable/fiscal.");
}

if (statutTraitement == StatutTraitement.A_REVOIR) {
    warnings.add("Statut A_REVOIR : ce flux doit être relu avant validation.");
}
if (type == FluxType.REGULARISATION && categorie != null) {
    warnings.add("Type REGULARISATION : vérifier la cohérence de la catégorie " + categorie.getLabel() + " avant validation.");
}

if (type == FluxType.MOUVEMENT_FINANCIER
        && (qualification == QualificationPressentie.CHARGE_COURANTE
            || qualification == QualificationPressentie.IMMOBILISATION)) {
    warnings.add("Qualification " + qualification.getLabel() + " suspecte pour un MOUVEMENT_FINANCIER.");
}
    return warnings;
}
}
