package com.nemia.core.flux.service;

import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.Occurrence;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class FluxValidationService {

  // -------------------------------------------------------------------------
  // Groupes de catégories — pour les règles bloquantes et les warnings
  // -------------------------------------------------------------------------

  private static final Set<FluxCategory> CATEGORIES_RECETTE = EnumSet.of(
    FluxCategory.LOYER,
    FluxCategory.CHARGES_REFACTUREES,
    FluxCategory.INDEMNITE_RECUE,
    FluxCategory.AUTRE_RECETTE_EXPLOITATION
  );

  private static final Set<FluxCategory> CATEGORIES_CHARGE_COURANTE = EnumSet.of(
    FluxCategory.ELECTRICITE,
    FluxCategory.EAU,
    FluxCategory.INTERNET,
    FluxCategory.ASSURANCE,
    FluxCategory.TAXE,
    FluxCategory.COPROPRIETE,
    FluxCategory.FRAIS_BANCAIRES,
    FluxCategory.HONORAIRES,
    FluxCategory.ENTRETIEN_COURANT,
    FluxCategory.CONSOMMABLES,
    FluxCategory.MENAGE,
    FluxCategory.FOURNITURES,
    FluxCategory.AUTRE_CHARGE_EXPLOITATION
  );

  private static final Set<FluxCategory> CATEGORIES_TRAVAUX_MOBILIER = EnumSet.of(
    FluxCategory.TRAVAUX,
    FluxCategory.REPARATION_IMPORTANTE,
    FluxCategory.AMELIORATION,
    FluxCategory.MOBILIER,
    FluxCategory.ELECTROMENAGER,
    FluxCategory.EQUIPEMENT,
    FluxCategory.DECORATION
  );

  private static final Set<FluxCategory> CATEGORIES_FINANCEMENT = EnumSet.of(
    FluxCategory.FRAIS_FINANCEMENT,
    FluxCategory.EMPRUNT_INTERETS,
    FluxCategory.EMPRUNT_ASSURANCE,
    FluxCategory.EMPRUNT_CAPITAL
  );

  private static final Set<FluxCategory> CATEGORIES_MOUVEMENT = EnumSet.of(
    FluxCategory.APPORT,
    FluxCategory.RETRAIT,
    FluxCategory.VIREMENT_INTERNE
  );

  // Catégories interdites pour une RECETTE
  // = tout sauf les catégories de recette elles-mêmes
  private static final Set<FluxCategory> CATEGORIES_INTERDITES_RECETTE;

  static {
    CATEGORIES_INTERDITES_RECETTE = EnumSet.copyOf(CATEGORIES_CHARGE_COURANTE);
    CATEGORIES_INTERDITES_RECETTE.addAll(CATEGORIES_TRAVAUX_MOBILIER);
    CATEGORIES_INTERDITES_RECETTE.addAll(CATEGORIES_FINANCEMENT);
    CATEGORIES_INTERDITES_RECETTE.addAll(CATEGORIES_MOUVEMENT);
  }

  // Catégories interdites pour une DEPENSE
  // = recettes + mouvements financiers purs
  private static final Set<FluxCategory> CATEGORIES_INTERDITES_DEPENSE;

  static {
    CATEGORIES_INTERDITES_DEPENSE = EnumSet.copyOf(CATEGORIES_RECETTE);
    CATEGORIES_INTERDITES_DEPENSE.addAll(CATEGORIES_MOUVEMENT);
    CATEGORIES_INTERDITES_DEPENSE.add(FluxCategory.EMPRUNT_CAPITAL);
  }

  // Catégories interdites pour un MOUVEMENT_FINANCIER
  // = recettes + charges + travaux/mobilier + intérêts/assurance emprunt
  private static final Set<FluxCategory> CATEGORIES_INTERDITES_MOUVEMENT_FINANCIER;

  static {
    CATEGORIES_INTERDITES_MOUVEMENT_FINANCIER = EnumSet.copyOf(CATEGORIES_RECETTE);
    CATEGORIES_INTERDITES_MOUVEMENT_FINANCIER.addAll(CATEGORIES_CHARGE_COURANTE);
    CATEGORIES_INTERDITES_MOUVEMENT_FINANCIER.addAll(CATEGORIES_TRAVAUX_MOBILIER);
    CATEGORIES_INTERDITES_MOUVEMENT_FINANCIER.add(FluxCategory.EMPRUNT_INTERETS);
    CATEGORIES_INTERDITES_MOUVEMENT_FINANCIER.add(FluxCategory.EMPRUNT_ASSURANCE);
  }

  // Sous-groupes pour les warnings
  private static final Set<FluxCategory> CATEGORIES_CHARGE_COURANTE_MANIFESTE = EnumSet.of(
    FluxCategory.ELECTRICITE,
    FluxCategory.EAU,
    FluxCategory.INTERNET,
    FluxCategory.ASSURANCE,
    FluxCategory.TAXE,
    FluxCategory.FRAIS_BANCAIRES,
    FluxCategory.CONSOMMABLES,
    FluxCategory.MENAGE,
    FluxCategory.FOURNITURES
  );

  private static final Set<FluxCategory> CATEGORIES_STRUCTURELLEMENT_RECURRENTES = EnumSet.of(
    FluxCategory.ELECTRICITE,
    FluxCategory.EAU,
    FluxCategory.INTERNET,
    FluxCategory.ASSURANCE,
    FluxCategory.EMPRUNT_INTERETS,
    FluxCategory.EMPRUNT_ASSURANCE,
    FluxCategory.EMPRUNT_CAPITAL,
    FluxCategory.COPROPRIETE
  );

  // -------------------------------------------------------------------------
  // Validation bloquante
  // -------------------------------------------------------------------------

  public void validateBlocking(FluxType type, FluxCategory categorie) {
    if (type == FluxType.RECETTE && CATEGORIES_INTERDITES_RECETTE.contains(categorie)) {
      throw new IllegalArgumentException("Une recette ne peut pas être classée dans une catégorie de dépense ou de mouvement financier");
    }

    if (type == FluxType.DEPENSE && CATEGORIES_INTERDITES_DEPENSE.contains(categorie)) {
      throw new IllegalArgumentException("Une dépense ne peut pas être classée dans une catégorie de recette ou de mouvement financier");
    }

    if (type == FluxType.MOUVEMENT_FINANCIER && CATEGORIES_INTERDITES_MOUVEMENT_FINANCIER.contains(categorie)) {
      throw new IllegalArgumentException("Un mouvement financier ne peut pas être classé dans une catégorie de charge ou de recette");
    }
  }

  // -------------------------------------------------------------------------
  // Warnings non bloquants
  // -------------------------------------------------------------------------

  public List<String> computeWarnings(
    FluxType type,
    FluxCategory categorie,
    QualificationPressentie qualification,
    StatutJustificatif statutJustificatif,
    Occurrence occurrence,
    StatutTraitement statutTraitement
  ) {
    List<String> warnings = new ArrayList<>();

    // W1 — IMMOBILISATION + catégorie de charge courante manifeste
    if (qualification == QualificationPressentie.IMMOBILISATION && CATEGORIES_CHARGE_COURANTE_MANIFESTE.contains(categorie)) {
      warnings.add("Qualification IMMOBILISATION inhabituelle pour cette catégorie de charge courante");
    }

    // W2 — CHARGE_COURANTE + catégorie travaux ou mobilier
    if (qualification == QualificationPressentie.CHARGE_COURANTE && CATEGORIES_TRAVAUX_MOBILIER.contains(categorie)) {
      warnings.add("Catégorie travaux ou mobilier — vérifier si la qualification CHARGE_COURANTE est adaptée");
    }

    // W3 — NON_REQUIS + dépense d'exploitation courante
    if (statutJustificatif == StatutJustificatif.NON_REQUIS && type == FluxType.DEPENSE && CATEGORIES_CHARGE_COURANTE.contains(categorie)) {
      warnings.add("Statut NON_REQUIS inhabituel pour une dépense d'exploitation courante");
    }

    // W4 — PONCTUEL + catégorie structurellement récurrente
    if (occurrence == Occurrence.PONCTUEL && CATEGORIES_STRUCTURELLEMENT_RECURRENTES.contains(categorie)) {
      warnings.add("Occurrence PONCTUEL inhabituelle pour une catégorie structurellement récurrente");
    }

    // W5 — A_ARBITRER
    if (qualification == QualificationPressentie.A_ARBITRER) {
      warnings.add("Qualification à arbitrer — ce flux nécessite une relecture");
    }

    // W6 — A_REVOIR
    if (statutTraitement == StatutTraitement.A_REVOIR) {
      warnings.add("Flux marqué À REVOIR — relecture nécessaire");
    }

    // W7 — REGULARISATION (typeFlux ou categorie)
    if (type == FluxType.REGULARISATION || categorie == FluxCategory.REGULARISATION) {
      warnings.add("Régularisation détectée — vérifier le sens et le rattachement de ce flux");
    }

    // W8 — MOUVEMENT_FINANCIER + qualification CHARGE_COURANTE ou IMMOBILISATION
    if (
      type == FluxType.MOUVEMENT_FINANCIER &&
      (qualification == QualificationPressentie.CHARGE_COURANTE || qualification == QualificationPressentie.IMMOBILISATION)
    ) {
      warnings.add("Qualification inhabituelle pour un mouvement financier");
    }

    return warnings;
  }
}
