package com.nemia.core.flux.controller;

import com.nemia.core.flux.service.FluxService;
import com.nemia.core.flux.dto.FluxResponse;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.PaymentMode;
import com.nemia.core.flux.model.Occurrence;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutTraitement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FluxFilterControllerTest {

    @Mock
    private FluxService fluxService;

    @InjectMocks
    private FluxController fluxController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(fluxController)
                .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
                .build();
    }

    private FluxResponse buildFluxResponse(Long id) {
        FluxResponse response = new FluxResponse();
        response.setId(id);
        response.setDate(LocalDate.of(2026, 4, 15));
        response.setType(FluxType.DEPENSE);
        response.setLibelle("Test flux " + id);
        response.setMontant(new BigDecimal("100.00"));
        response.setCategorie(FluxCategory.ASSURANCE);
        response.setModePaiement(PaymentMode.VIREMENT);
        response.setOccurrence(Occurrence.RECURRENT);
        response.setStatutJustificatif(StatutJustificatif.A_FOURNIR);
        response.setQualificationPressentie(QualificationPressentie.CHARGE_COURANTE);
        response.setStatutTraitement(StatutTraitement.BRUT);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        response.setWarnings(List.of());
        return response;
    }

    @Test
    void findAll_sansFiltre_retourneTousLesFlux() throws Exception {
        when(fluxService.findAllWithFilters(null, null, null, null))
                .thenReturn(List.of(buildFluxResponse(1L), buildFluxResponse(2L)));

        mockMvc.perform(get("/api/flux"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(fluxService).findAllWithFilters(null, null, null, null);
    }

    @Test
    void findAll_avecBienId_retourneFluxFiltres() throws Exception {
        when(fluxService.findAllWithFilters(eq(1L), isNull(), isNull(), isNull()))
                .thenReturn(List.of(buildFluxResponse(1L)));

        mockMvc.perform(get("/api/flux").param("bienId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(fluxService).findAllWithFilters(eq(1L), isNull(), isNull(), isNull());
    }

    @Test
    void findAll_avecQualificationPressentie_retourneFluxFiltres() throws Exception {
        when(fluxService.findAllWithFilters(isNull(), eq("A_ARBITRER"), isNull(), isNull()))
                .thenReturn(List.of(buildFluxResponse(1L)));

        mockMvc.perform(get("/api/flux").param("qualificationPressentie", "A_ARBITRER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(fluxService).findAllWithFilters(isNull(), eq("A_ARBITRER"), isNull(), isNull());
    }

    @Test
    void findAll_avecStatutTraitement_retourneFluxFiltres() throws Exception {
        when(fluxService.findAllWithFilters(isNull(), isNull(), eq("A_REVOIR"), isNull()))
                .thenReturn(List.of(buildFluxResponse(1L)));

        mockMvc.perform(get("/api/flux").param("statutTraitement", "A_REVOIR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(fluxService).findAllWithFilters(isNull(), isNull(), eq("A_REVOIR"), isNull());
    }

    @Test
    void findAll_avecStatutJustificatifMultiple_retourneFluxFiltres() throws Exception {
        when(fluxService.findAllWithFilters(isNull(), isNull(), isNull(), eq("A_FOURNIR,INCOMPLET")))
                .thenReturn(List.of(buildFluxResponse(1L)));

        mockMvc.perform(get("/api/flux").param("statutJustificatif", "A_FOURNIR,INCOMPLET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(fluxService).findAllWithFilters(isNull(), isNull(), isNull(), eq("A_FOURNIR,INCOMPLET"));
    }

    @Test
    void findAll_avecFiltresCombines_retourneFluxFiltres() throws Exception {
        when(fluxService.findAllWithFilters(eq(1L), isNull(), isNull(), eq("A_FOURNIR,INCOMPLET")))
                .thenReturn(List.of(buildFluxResponse(1L)));

        mockMvc.perform(get("/api/flux")
                        .param("bienId", "1")
                        .param("statutJustificatif", "A_FOURNIR,INCOMPLET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(fluxService).findAllWithFilters(eq(1L), isNull(), isNull(), eq("A_FOURNIR,INCOMPLET"));
    }

    @Test
    void findAll_avecFiltreVide_retourneTousLesFlux() throws Exception {
        when(fluxService.findAllWithFilters(null, null, null, null))
                .thenReturn(List.of(buildFluxResponse(1L), buildFluxResponse(2L)));

        mockMvc.perform(get("/api/flux"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}