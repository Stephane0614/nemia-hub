package com.nemia.core.flux.service;

import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Centralise le mapping typeFlux → catégories autorisées (règles 1 à 4 de la
 * matrice de compatibilité). Utilisé pour le préfiltre exposé par
 * GET /api/flux/referentials/categories.
 *
 * Volontairement indépendant des règles bloquantes de FluxValidationService :
 * ce mapping sert de préfiltre UI, pas de validation serveur. Les deux ne
 * sont pas garantis strictement identiques (ex. EMPRUNT_INTERETS/ASSURANCE
 * restent autorisés par validateBlocking pour DEPENSE mais n'apparaissent
 * pas ici), pour ne pas modifier le comportement des endpoints POST/PUT
 * existants.
 */
public final class FluxCompatibilityRules {

  private static final Map<FluxType, Set<FluxCategory>> CATEGORIES_AUTORISEES_PAR_TYPE = new EnumMap<>(FluxType.class);

  static {
    CATEGORIES_AUTORISEES_PAR_TYPE.put(
      FluxType.RECETTE,
      EnumSet.of(
        FluxCategory.LOYER,
        FluxCategory.CHARGES_REFACTUREES,
        FluxCategory.INDEMNITE_RECUE,
        FluxCategory.AUTRE_RECETTE_EXPLOITATION,
        FluxCategory.AUTRE
      )
    );

    CATEGORIES_AUTORISEES_PAR_TYPE.put(
      FluxType.DEPENSE,
      EnumSet.of(
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
        FluxCategory.AUTRE_CHARGE_EXPLOITATION,
        FluxCategory.TRAVAUX,
        FluxCategory.REPARATION_IMPORTANTE,
        FluxCategory.AMELIORATION,
        FluxCategory.MOBILIER,
        FluxCategory.ELECTROMENAGER,
        FluxCategory.EQUIPEMENT,
        FluxCategory.DECORATION,
        FluxCategory.FRAIS_FINANCEMENT,
        FluxCategory.AUTRE
      )
    );

    CATEGORIES_AUTORISEES_PAR_TYPE.put(
      FluxType.MOUVEMENT_FINANCIER,
      EnumSet.of(FluxCategory.EMPRUNT_CAPITAL, FluxCategory.APPORT, FluxCategory.RETRAIT, FluxCategory.VIREMENT_INTERNE, FluxCategory.AUTRE)
    );

    CATEGORIES_AUTORISEES_PAR_TYPE.put(FluxType.REGULARISATION, EnumSet.of(FluxCategory.REGULARISATION, FluxCategory.AUTRE));
  }

  private FluxCompatibilityRules() {}

  /**
   * Catégories autorisées pour un typeFlux donné. Si le type est null (ou
   * inconnu du mapping), retourne toutes les catégories — comportement
   * dégradé volontaire, pas d'erreur.
   */
  public static Set<FluxCategory> getCategoriesAutorisees(FluxType type) {
    if (type == null) {
      return EnumSet.allOf(FluxCategory.class);
    }
    return CATEGORIES_AUTORISEES_PAR_TYPE.getOrDefault(type, EnumSet.allOf(FluxCategory.class));
  }
}
