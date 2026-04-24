package com.nemia.core.flux.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nemia.core.flux.dto.HomeSyntheseResponse;
import com.nemia.core.flux.service.HomeSyntheseService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class HomeSyntheseControllerTest {

  @Mock
  private HomeSyntheseService homeSyntheseService;

  @InjectMocks
  private HomeSyntheseController homeSyntheseController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(homeSyntheseController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  private HomeSyntheseResponse buildEmptyResponse(String moisCode, Long exerciceId, Long bienId) {
    return new HomeSyntheseResponse(
      new HomeSyntheseResponse.Periode("Avril 2026", "2026-04-01", "2026-04-30", moisCode, exerciceId, bienId),
      new HomeSyntheseResponse.Metriques(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0),
      new HomeSyntheseResponse.Alertes(0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0),
      List.of(),
      new HomeSyntheseResponse.RecurrenceDepenses(BigDecimal.ZERO, BigDecimal.ZERO),
      List.of()
    );
  }

  @Test
  void getSynthese_sansParametre_retourne200() throws Exception {
    when(homeSyntheseService.getSynthese(any(), any(), any())).thenReturn(buildEmptyResponse("2026-04", null, null));

    mockMvc
      .perform(get("/api/home/synthese"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.periode").exists())
      .andExpect(jsonPath("$.metriques").exists())
      .andExpect(jsonPath("$.alertes").exists())
      .andExpect(jsonPath("$.repartitionDepenses").exists())
      .andExpect(jsonPath("$.recurrenceDepenses").exists())
      .andExpect(jsonPath("$.dernieresOperations").exists());
  }

  @Test
  void getSynthese_avecMoisValide_retourne200AvecBonnePeriode() throws Exception {
    when(homeSyntheseService.getSynthese(any(), any(), any())).thenReturn(buildEmptyResponse("2026-04", null, null));

    mockMvc
      .perform(get("/api/home/synthese").param("mois", "2026-04"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.periode.mois").value("2026-04"))
      .andExpect(jsonPath("$.periode.dateDebut").value("2026-04-01"))
      .andExpect(jsonPath("$.periode.dateFin").value("2026-04-30"));
  }

  @Test
  void getSynthese_avecMoisMalFormate_retourne400() throws Exception {
    when(homeSyntheseService.getSynthese(any(), any(), any())).thenThrow(new IllegalArgumentException("Format de mois invalide"));

    mockMvc.perform(get("/api/home/synthese").param("mois", "mauvais-format")).andExpect(status().isBadRequest());
  }

  @Test
  void getSynthese_retourneMetriquesAZero_siBaseVide() throws Exception {
    when(homeSyntheseService.getSynthese(any(), any(), any())).thenReturn(buildEmptyResponse("2026-04", null, null));

    mockMvc
      .perform(get("/api/home/synthese").param("mois", "2026-04"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.metriques.totalRecettes").value(0))
      .andExpect(jsonPath("$.metriques.totalDepenses").value(0))
      .andExpect(jsonPath("$.metriques.solde").value(0))
      .andExpect(jsonPath("$.metriques.nombreOperations").value(0));
  }

  @Test
  void getSynthese_retourneAlertesAZero_siBaseVide() throws Exception {
    when(homeSyntheseService.getSynthese(any(), any(), any())).thenReturn(buildEmptyResponse("2026-04", null, null));

    mockMvc
      .perform(get("/api/home/synthese").param("mois", "2026-04"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.alertes.fluxSansJustificatif").value(0))
      .andExpect(jsonPath("$.alertes.fluxAArbitrer").value(0))
      .andExpect(jsonPath("$.alertes.fluxARevoir").value(0));
  }

  @Test
  void getSynthese_retourneListesVides_siBaseVide() throws Exception {
    when(homeSyntheseService.getSynthese(any(), any(), any())).thenReturn(buildEmptyResponse("2026-04", null, null));

    mockMvc
      .perform(get("/api/home/synthese").param("mois", "2026-04"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.repartitionDepenses").isArray())
      .andExpect(jsonPath("$.repartitionDepenses").isEmpty())
      .andExpect(jsonPath("$.dernieresOperations").isArray())
      .andExpect(jsonPath("$.dernieresOperations").isEmpty());
  }

  @Test
  void getSynthese_avecBienId_retourne200AvecBienIdDansReponse() throws Exception {
    when(homeSyntheseService.getSynthese(any(), eq(1L), any())).thenReturn(buildEmptyResponse("2026-04", null, 1L));

    mockMvc
      .perform(get("/api/home/synthese").param("bienId", "1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.periode.bienId").value(1));
  }

  @Test
  void getSynthese_avecBienIdInconnu_retourne200AvecZeros() throws Exception {
    when(homeSyntheseService.getSynthese(any(), eq(999L), any())).thenReturn(buildEmptyResponse("2026-04", null, 999L));

    mockMvc
      .perform(get("/api/home/synthese").param("bienId", "999"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.metriques.totalRecettes").value(0))
      .andExpect(jsonPath("$.metriques.totalDepenses").value(0));
  }

  @Test
  void getSynthese_avecExerciceId_retourne200AvecExerciceIdDansReponse() throws Exception {
    HomeSyntheseResponse response = new HomeSyntheseResponse(
      new HomeSyntheseResponse.Periode("2026", "2026-01-01", "2026-12-31", null, 1L, null),
      new HomeSyntheseResponse.Metriques(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0),
      new HomeSyntheseResponse.Alertes(0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0),
      List.of(),
      new HomeSyntheseResponse.RecurrenceDepenses(BigDecimal.ZERO, BigDecimal.ZERO),
      List.of()
    );

    when(homeSyntheseService.getSynthese(any(), any(), eq(1L))).thenReturn(response);

    mockMvc
      .perform(get("/api/home/synthese").param("exerciceId", "1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.periode.exerciceId").value(1))
      .andExpect(jsonPath("$.periode.mois").doesNotExist())
      .andExpect(jsonPath("$.periode.label").value("2026"))
      .andExpect(jsonPath("$.periode.dateDebut").value("2026-01-01"))
      .andExpect(jsonPath("$.periode.dateFin").value("2026-12-31"));
  }

  @Test
  void getSynthese_avecExerciceIdInconnu_retourne200AvecFallbackMois() throws Exception {
    when(homeSyntheseService.getSynthese(any(), any(), eq(999L))).thenReturn(buildEmptyResponse("2026-04", null, null));

    mockMvc
      .perform(get("/api/home/synthese").param("exerciceId", "999"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.periode.mois").value("2026-04"));
  }

  @Test
  void getSynthese_avecBienIdEtExerciceId_retourne200AvecLesDeux() throws Exception {
    HomeSyntheseResponse response = new HomeSyntheseResponse(
      new HomeSyntheseResponse.Periode("2026", "2026-01-01", "2026-12-31", null, 1L, 1L),
      new HomeSyntheseResponse.Metriques(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0),
      new HomeSyntheseResponse.Alertes(0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0),
      List.of(),
      new HomeSyntheseResponse.RecurrenceDepenses(BigDecimal.ZERO, BigDecimal.ZERO),
      List.of()
    );

    when(homeSyntheseService.getSynthese(any(), eq(1L), eq(1L))).thenReturn(response);

    mockMvc
      .perform(get("/api/home/synthese").param("bienId", "1").param("exerciceId", "1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.periode.bienId").value(1))
      .andExpect(jsonPath("$.periode.exerciceId").value(1));
  }

  @Test
  void getSynthese_avecExerciceIdValideEtMois_exerciceIdPrioritaire() throws Exception {
    HomeSyntheseResponse response = new HomeSyntheseResponse(
      new HomeSyntheseResponse.Periode("2026", "2026-01-01", "2026-12-31", null, 1L, null),
      new HomeSyntheseResponse.Metriques(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0),
      new HomeSyntheseResponse.Alertes(0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0),
      List.of(),
      new HomeSyntheseResponse.RecurrenceDepenses(BigDecimal.ZERO, BigDecimal.ZERO),
      List.of()
    );

    when(homeSyntheseService.getSynthese(any(), any(), eq(1L))).thenReturn(response);

    mockMvc
      .perform(get("/api/home/synthese").param("mois", "2026-04").param("exerciceId", "1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.periode.exerciceId").value(1))
      .andExpect(jsonPath("$.periode.label").value("2026"));
  }
}
