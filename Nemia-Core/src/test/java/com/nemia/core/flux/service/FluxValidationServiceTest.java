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

  // --- RÈGLES BLOQUANTES ---

  @Test
  void shouldThrowWhenRecetteHasDepenseCategory() {
    assertThatThrownBy(() ->
      service.validate(FluxType.RECETTE, FluxCategory.ELECTRICITE, null, null, null, null)
    ).isInstanceOf(IllegalArgumentException.class)
     .hasMessageContaining("RECETTE");
  }

  @Test
  void shouldThrowWhenDepenseHasRecetteCategory() {
    assertThatThrownBy(() ->
      service.validate(FluxType.DEPENSE, FluxCategory.LOYER, null, null, null, null)
    ).isInstanceOf(IllegalArgumentException.class)
     .hasMessageContaining("DEPENSE");
  }

  @Test
  void shouldThrowWhenMouvementFinancierHasChargeCategory() {
    assertThatThrownBy(() ->
      service.validate(FluxType.MOUVEMENT_FINANCIER, FluxCategory.ELECTRICITE, null, null, null, null)
    ).isInstanceOf(IllegalArgumentException.class)
     .hasMessageContaining("MOUVEMENT_FINANCIER");
  }

  @Test
  void shouldThrowWhenMouvementFinancierHasRecetteCategory() {
    assertThatThrownBy(() ->
      service.validate(FluxType.MOUVEMENT_FINANCIER, FluxCategory.LOYER, null, null, null, null)
    ).isInstanceOf(IllegalArgumentException.class)
     .hasMessageContaining("MOUVEMENT_FINANCIER");
  }

  // --- WARNINGS ---

  @Test
  void shouldWarnWhenImmobilisationWithChargeCategory() {
    List<String> warnings = service.validate(
      FluxType.DEPENSE, FluxCategory.ELECTRICITE,
      QualificationPressentie.IMMOBILISATION, null, null, null
    );
    assertThat(warnings).anyMatch(w -> w.contains("IMMOBILISATION"));
  }

  @Test
  void shouldWarnWhenChargeCouranteWithTravaux() {
    List<String> warnings = service.validate(
      FluxType.DEPENSE, FluxCategory.TRAVAUX,
      QualificationPressentie.CHARGE_COURANTE, null, null, null
    );
    assertThat(warnings).anyMatch(w -> w.contains("CHARGE_COURANTE"));
  }

  @Test
  void shouldWarnWhenNonRequisOnDepenseExploitation() {
    List<String> warnings = service.validate(
      FluxType.DEPENSE, FluxCategory.ELECTRICITE,
      null, StatutJustificatif.NON_REQUIS, null, null
    );
    assertThat(warnings).anyMatch(w -> w.contains("NON_REQUIS"));
  }

  @Test
  void shouldWarnWhenPonctuelOnRecurrentCategory() {
    List<String> warnings = service.validate(
      FluxType.DEPENSE, FluxCategory.ELECTRICITE,
      null, null, Occurrence.PONCTUEL, null
    );
    assertThat(warnings).anyMatch(w -> w.contains("PONCTUEL"));
  }

  @Test
  void shouldWarnWhenAArbitrer() {
    List<String> warnings = service.validate(
      FluxType.DEPENSE, FluxCategory.TRAVAUX,
      QualificationPressentie.A_ARBITRER, null, null, null
    );
    assertThat(warnings).anyMatch(w -> w.contains("A_ARBITRER"));
  }

  @Test
  void shouldWarnWhenARevoir() {
    List<String> warnings = service.validate(
      FluxType.DEPENSE, FluxCategory.ELECTRICITE,
      null, null, null, StatutTraitement.A_REVOIR
    );
    assertThat(warnings).anyMatch(w -> w.contains("A_REVOIR"));
  }

  @Test
  void shouldWarnWhenRegularisationWithAnyCategory() {
    List<String> warnings = service.validate(
      FluxType.REGULARISATION, FluxCategory.ELECTRICITE,
      null, null, null, null
    );
    assertThat(warnings).anyMatch(w -> w.contains("REGULARISATION"));
  }

  @Test
  void shouldWarnWhenMouvementFinancierWithChargeCouranteQualification() {
    List<String> warnings = service.validate(
      FluxType.MOUVEMENT_FINANCIER, FluxCategory.AUTRE,
      QualificationPressentie.CHARGE_COURANTE, null, null, null
    );
    assertThat(warnings).anyMatch(w -> w.contains("MOUVEMENT_FINANCIER"));
  }

  @Test
  void shouldReturnNoWarningsForValidFlux() {
    List<String> warnings = service.validate(
      FluxType.DEPENSE, FluxCategory.ELECTRICITE,
      QualificationPressentie.CHARGE_COURANTE,
      StatutJustificatif.FOURNI,
      Occurrence.RECURRENT,
      StatutTraitement.BRUT
    );
    assertThat(warnings).isEmpty();
  }
}