package com.nemia.core.flux.controller;

import com.nemia.core.flux.dto.HomeSyntheseResponse;
import com.nemia.core.flux.service.HomeSyntheseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HomeSyntheseControllerTest {

    @Mock
    private HomeSyntheseService homeSyntheseService;

    @InjectMocks
    private HomeSyntheseController homeSyntheseController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(homeSyntheseController)
                .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
                .build();
    }

    private HomeSyntheseResponse buildEmptyResponse(String moisCode) {
        return new HomeSyntheseResponse(
                new HomeSyntheseResponse.Periode("Avril 2026", "2026-04-01", "2026-04-30", moisCode),
                new HomeSyntheseResponse.Metriques(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0),
                new HomeSyntheseResponse.Alertes(0, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0),
                List.of(),
                new HomeSyntheseResponse.RecurrenceDepenses(BigDecimal.ZERO, BigDecimal.ZERO),
                List.of()
        );
    }

    @Test
    void getSynthese_sansParametre_retourne200() throws Exception {
        when(homeSyntheseService.getSynthese(any(), any()))
                .thenReturn(buildEmptyResponse("2026-04"));

        mockMvc.perform(get("/api/home/synthese"))
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
        when(homeSyntheseService.getSynthese(any(), any()))
                .thenReturn(buildEmptyResponse("2026-04"));

        mockMvc.perform(get("/api/home/synthese").param("mois", "2026-04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periode.mois").value("2026-04"))
                .andExpect(jsonPath("$.periode.dateDebut").value("2026-04-01"))
                .andExpect(jsonPath("$.periode.dateFin").value("2026-04-30"));
    }

    @Test
    void getSynthese_avecMoisMalFormate_retourne400() throws Exception {
        when(homeSyntheseService.getSynthese(any(), any()))
                .thenThrow(new IllegalArgumentException("Format de mois invalide"));

        mockMvc.perform(get("/api/home/synthese").param("mois", "mauvais-format"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSynthese_retourneMetriquesAZero_siBaseVide() throws Exception {
        when(homeSyntheseService.getSynthese(any(), any()))
                .thenReturn(buildEmptyResponse("2026-04"));

        mockMvc.perform(get("/api/home/synthese").param("mois", "2026-04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metriques.totalRecettes").value(0))
                .andExpect(jsonPath("$.metriques.totalDepenses").value(0))
                .andExpect(jsonPath("$.metriques.solde").value(0))
                .andExpect(jsonPath("$.metriques.nombreOperations").value(0));
    }

    @Test
    void getSynthese_retourneAlertesAZero_siBaseVide() throws Exception {
        when(homeSyntheseService.getSynthese(any(), any()))
                .thenReturn(buildEmptyResponse("2026-04"));

        mockMvc.perform(get("/api/home/synthese").param("mois", "2026-04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertes.fluxSansJustificatif").value(0))
                .andExpect(jsonPath("$.alertes.fluxAArbitrer").value(0))
                .andExpect(jsonPath("$.alertes.fluxARevoir").value(0));
    }

    @Test
    void getSynthese_retourneListesVides_siBaseVide() throws Exception {
        when(homeSyntheseService.getSynthese(any(), any()))
                .thenReturn(buildEmptyResponse("2026-04"));

        mockMvc.perform(get("/api/home/synthese").param("mois", "2026-04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repartitionDepenses").isArray())
                .andExpect(jsonPath("$.repartitionDepenses").isEmpty())
                .andExpect(jsonPath("$.dernieresOperations").isArray())
                .andExpect(jsonPath("$.dernieresOperations").isEmpty());
    }
}