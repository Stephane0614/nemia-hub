package com.nemia.core.flux.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.Occurrence;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;
import java.util.List;
import org.junit.jupiter.api.Test;

class FluxValidationServiceTest {

  private final FluxValidationService service = new FluxValidationService();

  // -------------------------------------------------------------------------
  // Règles bloquantes — validateBlocking
  // -------------------------------------------------------------------------

  @Test
  void shouldThrowWhenRecetteHasDepenseCategory() {
    assertThatThrownBy(() -> service.validateBlocking(FluxType.RECETTE, FluxCategory.ELECTRICITE))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("recette");
  }

  @Test
  void shouldNotThrowWhenRecetteHasRecetteCategory() {
    // LOYER est une catégorie valide pour RECETTE
    service.validateBlocking(FluxType.RECETTE, FluxCategory.LOYER);
  }

  @Test
  void shouldThrowWhenDepenseHasRecetteCategory() {
    assertThatThrownBy(() -> service.validateBlocking(FluxType.DEPENSE, FluxCategory.LOYER))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("dépense");
  }

  @Test
  void shouldNotThrowWhenDepenseHasChargeCategory() {
    // ELECTRICITE est une catégorie valide pour DEPENSE
    service.validateBlocking(FluxType.DEPENSE, FluxCategory.ELECTRICITE);
  }

  @Test
  void shouldThrowWhenMouvementFinancierHasChargeCategory() {
    assertThatThrownBy(() -> service.validateBlocking(FluxType.MOUVEMENT_FINANCIER, FluxCategory.ELECTRICITE))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("mouvement financier");
  }

  @Test
  void shouldThrowWhenMouvementFinancierHasRecetteCategory() {
    assertThatThrownBy(() -> service.validateBlocking(FluxType.MOUVEMENT_FINANCIER, FluxCategory.LOYER))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("mouvement financier");
  }

  @Test
  void shouldNotThrowWhenMouvementFinancierHasMouvementCategory() {
    // APPORT est une catégorie valide pour MOUVEMENT_FINANCIER
    service.validateBlocking(FluxType.MOUVEMENT_FINANCIER, FluxCategory.APPORT);
  }

  // -------------------------------------------------------------------------
  // Warnings — computeWarnings
  // -------------------------------------------------------------------------

  @Test
  void shouldWarnWhenImmobilisationWithChargeCouranteManifeste() {
    List<String> warnings = service.computeWarnings(
      FluxType.DEPENSE,
      FluxCategory.ELECTRICITE,
      QualificationPressentie.IMMOBILISATION,
      null,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("IMMOBILISATION"));
  }

  @Test
  void shouldWarnWhenChargeCouranteWithTravaux() {
    List<String> warnings = service.computeWarnings(
      FluxType.DEPENSE,
      FluxCategory.TRAVAUX,
      QualificationPressentie.CHARGE_COURANTE,
      null,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("CHARGE_COURANTE"));
  }

  @Test
  void shouldWarnWhenNonRequisOnDepenseExploitation() {
    List<String> warnings = service.computeWarnings(
      FluxType.DEPENSE,
      FluxCategory.ELECTRICITE,
      null,
      StatutJustificatif.NON_REQUIS,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("NON_REQUIS"));
  }

  @Test
  void shouldWarnWhenPonctuelOnRecurrentCategory() {
    List<String> warnings = service.computeWarnings(FluxType.DEPENSE, FluxCategory.ELECTRICITE, null, null, Occurrence.PONCTUEL, null);
    assertThat(warnings).anyMatch(w -> w.contains("PONCTUEL"));
  }

  @Test
  void shouldWarnWhenAArbitrer() {
    List<String> warnings = service.computeWarnings(
      FluxType.DEPENSE,
      FluxCategory.TRAVAUX,
      QualificationPressentie.A_ARBITRER,
      null,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("arbitrer"));
  }

  @Test
  void shouldWarnWhenARevoir() {
    List<String> warnings = service.computeWarnings(
      FluxType.DEPENSE,
      FluxCategory.ELECTRICITE,
      null,
      null,
      null,
      StatutTraitement.A_REVOIR
    );
    assertThat(warnings).anyMatch(w -> w.contains("REVOIR"));
  }

  @Test
  void shouldWarnWhenRegularisationTypeFlux() {
    List<String> warnings = service.computeWarnings(FluxType.REGULARISATION, FluxCategory.AUTRE, null, null, null, null);
    assertThat(warnings).anyMatch(w -> w.contains("égularisation"));
  }

  @Test
  void shouldWarnWhenMouvementFinancierWithChargeCouranteQualification() {
    List<String> warnings = service.computeWarnings(
      FluxType.MOUVEMENT_FINANCIER,
      FluxCategory.AUTRE,
      QualificationPressentie.CHARGE_COURANTE,
      null,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("mouvement financier"));
  }

  @Test
  void shouldReturnNoWarningsForValidFlux() {
    List<String> warnings = service.computeWarnings(
      FluxType.DEPENSE,
      FluxCategory.ELECTRICITE,
      QualificationPressentie.CHARGE_COURANTE,
      StatutJustificatif.FOURNI,
      Occurrence.RECURRENT,
      StatutTraitement.BRUT
    );
    assertThat(warnings).isEmpty();
  }

  // -------------------------------------------------------------------------
  // Warnings — W9 à W14 (mission #41)
  // -------------------------------------------------------------------------

  @Test
  void shouldWarnW9WhenChargeCouranteWithRecetteCategory() {
    List<String> warnings = service.computeWarnings(
      FluxType.RECETTE,
      FluxCategory.LOYER,
      QualificationPressentie.CHARGE_COURANTE,
      null,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("recette") && w.contains("charge courante"));
  }

  @Test
  void shouldWarnW10WhenChargeCouranteWithMouvementFinancierCategoryOnOtherType() {
    // REGULARISATION n'est pas bloqué par validateBlocking pour ces catégories
    List<String> warnings = service.computeWarnings(
      FluxType.REGULARISATION,
      FluxCategory.APPORT,
      QualificationPressentie.CHARGE_COURANTE,
      null,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("mouvement financier") && w.contains("charge courante"));
  }

  @Test
  void shouldNotDuplicateW10WhenTypeIsMouvementFinancier() {
    // Déjà couvert par W8 pour ce type — W10 ne doit pas se déclencher en plus
    List<String> warnings = service.computeWarnings(
      FluxType.MOUVEMENT_FINANCIER,
      FluxCategory.APPORT,
      QualificationPressentie.CHARGE_COURANTE,
      null,
      null,
      null
    );
    assertThat(warnings).hasSize(1);
    assertThat(warnings).anyMatch(w -> w.contains("Qualification inhabituelle pour un mouvement financier"));
  }

  @Test
  void shouldWarnW11WhenImmobilisationWithRecetteCategory() {
    List<String> warnings = service.computeWarnings(
      FluxType.RECETTE,
      FluxCategory.LOYER,
      QualificationPressentie.IMMOBILISATION,
      null,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("recette") && w.contains("immobilisation"));
  }

  @Test
  void shouldNotDuplicateW1AndW12ForImmobilisationOnChargeCouranteManifeste() {
    // W1 couvre déjà ce cas — W12 volontairement non implémenté pour éviter le doublon
    List<String> warnings = service.computeWarnings(
      FluxType.DEPENSE,
      FluxCategory.ELECTRICITE,
      QualificationPressentie.IMMOBILISATION,
      null,
      null,
      null
    );
    assertThat(warnings).hasSize(1);
  }

  @Test
  void shouldWarnW13WhenHorsResultatWithExploitationCategory() {
    List<String> warnings = service.computeWarnings(
      FluxType.DEPENSE,
      FluxCategory.ELECTRICITE,
      QualificationPressentie.HORS_RESULTAT,
      null,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("impact sur le résultat"));
  }

  @Test
  void shouldWarnW14WhenNonApplicableWithDepenseCategory() {
    List<String> warnings = service.computeWarnings(
      FluxType.DEPENSE,
      FluxCategory.ELECTRICITE,
      QualificationPressentie.NON_APPLICABLE,
      null,
      null,
      null
    );
    assertThat(warnings).anyMatch(w -> w.contains("qualification comptable pressentie"));
  }

  @Test
  void shouldNotWarnW14ForRecetteCategory() {
    List<String> warnings = service.computeWarnings(
      FluxType.RECETTE,
      FluxCategory.LOYER,
      QualificationPressentie.NON_APPLICABLE,
      null,
      null,
      null
    );
    assertThat(warnings).noneMatch(w -> w.contains("qualification comptable pressentie"));
  }

  @Test
  void shouldNotWarnW14ForMouvementCategory() {
    List<String> warnings = service.computeWarnings(
      FluxType.MOUVEMENT_FINANCIER,
      FluxCategory.APPORT,
      QualificationPressentie.NON_APPLICABLE,
      null,
      null,
      null
    );
    assertThat(warnings).noneMatch(w -> w.contains("qualification comptable pressentie"));
  }
}
