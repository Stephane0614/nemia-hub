package com.nemia.core.flux.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nemia.core.flux.dto.FluxPageResponse;
import com.nemia.core.flux.dto.FluxResponse;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.Occurrence;
import com.nemia.core.flux.model.PaymentMode;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;
import com.nemia.core.flux.service.FluxService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class FluxFilterControllerTest {

  @Mock
  private FluxService fluxService;

  @InjectMocks
  private FluxController fluxController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(fluxController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  // ── Helpers ──

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

  private FluxPageResponse pageOf(FluxResponse... responses) {
    List<FluxResponse> contenu = List.of(responses);
    return new FluxPageResponse(contenu, 0, 20, contenu.size(), 1);
  }

  private FluxPageResponse emptyPage() {
    return new FluxPageResponse(List.of(), 0, 20, 0L, 0);
  }

  // ── Filtres existants ──

  @Test
  void findAll_sansFiltre_retourneTousLesFlux() throws Exception {
    when(fluxService.findAllWithFilters(null, null, null, null, null, null, null, null, null, 0, 20)).thenReturn(
      pageOf(buildFluxResponse(1L), buildFluxResponse(2L))
    );

    mockMvc
      .perform(get("/api/flux"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu").isArray())
      .andExpect(jsonPath("$.contenu.length()").value(2))
      .andExpect(jsonPath("$.totalElements").value(2));

    verify(fluxService).findAllWithFilters(null, null, null, null, null, null, null, null, null, 0, 20);
  }

  @Test
  void findAll_avecBienId_retourneFluxFiltres() throws Exception {
    when(
      fluxService.findAllWithFilters(eq(1L), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(20))
    ).thenReturn(pageOf(buildFluxResponse(1L)));

    mockMvc.perform(get("/api/flux").param("bienId", "1")).andExpect(status().isOk()).andExpect(jsonPath("$.contenu.length()").value(1));

    verify(fluxService).findAllWithFilters(
      eq(1L),
      isNull(),
      isNull(),
      isNull(),
      isNull(),
      isNull(),
      isNull(),
      isNull(),
      isNull(),
      eq(0),
      eq(20)
    );
  }

  @Test
  void findAll_avecBienId_sansResultat_retournePageVide() throws Exception {
    when(
      fluxService.findAllWithFilters(eq(99L), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(20))
    ).thenReturn(emptyPage());

    mockMvc
      .perform(get("/api/flux").param("bienId", "99"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu").isArray())
      .andExpect(jsonPath("$.contenu.length()").value(0))
      .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void findAll_avecQualificationPressentie_retourneFluxFiltres() throws Exception {
    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq("A_ARBITRER"),
        isNull(),
        isNull(),
        eq(0),
        eq(20)
      )
    ).thenReturn(pageOf(buildFluxResponse(1L)));

    mockMvc
      .perform(get("/api/flux").param("qualificationPressentie", "A_ARBITRER"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu.length()").value(1));
  }

  @Test
  void findAll_avecStatutTraitement_retourneFluxFiltres() throws Exception {
    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq("A_REVOIR"),
        isNull(),
        eq(0),
        eq(20)
      )
    ).thenReturn(pageOf(buildFluxResponse(1L)));

    mockMvc
      .perform(get("/api/flux").param("statutTraitement", "A_REVOIR"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu.length()").value(1));
  }

  @Test
  void findAll_avecStatutJustificatifMultiple_retourneFluxFiltres() throws Exception {
    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq("A_FOURNIR,INCOMPLET"),
        eq(0),
        eq(20)
      )
    ).thenReturn(pageOf(buildFluxResponse(1L)));

    mockMvc
      .perform(get("/api/flux").param("statutJustificatif", "A_FOURNIR,INCOMPLET"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu.length()").value(1));
  }

  @Test
  void findAll_avecExerciceId_retourneFluxFiltres() throws Exception {
    when(
      fluxService.findAllWithFilters(isNull(), eq(2L), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(20))
    ).thenReturn(pageOf(buildFluxResponse(1L)));

    mockMvc
      .perform(get("/api/flux").param("exerciceId", "2"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu.length()").value(1));

    verify(fluxService).findAllWithFilters(
      isNull(),
      eq(2L),
      isNull(),
      isNull(),
      isNull(),
      isNull(),
      isNull(),
      isNull(),
      isNull(),
      eq(0),
      eq(20)
    );
  }

  @Test
  void findAll_avecExerciceId_sansResultat_retournePageVide() throws Exception {
    when(
      fluxService.findAllWithFilters(isNull(), eq(99L), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(20))
    ).thenReturn(emptyPage());

    mockMvc.perform(get("/api/flux").param("exerciceId", "99")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void findAll_avecTypeFlux_retourneFluxFiltres() throws Exception {
    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        eq("RECETTE"),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq(0),
        eq(20)
      )
    ).thenReturn(pageOf(buildFluxResponse(1L)));

    mockMvc
      .perform(get("/api/flux").param("typeFlux", "RECETTE"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu.length()").value(1));
  }

  @Test
  void findAll_avecTypeFlux_sansResultat_retournePageVide() throws Exception {
    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        eq("MOUVEMENT_FINANCIER"),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq(0),
        eq(20)
      )
    ).thenReturn(emptyPage());

    mockMvc
      .perform(get("/api/flux").param("typeFlux", "MOUVEMENT_FINANCIER"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void findAll_avecCategorie_retourneFluxFiltres() throws Exception {
    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        isNull(),
        eq("LOYER"),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq(0),
        eq(20)
      )
    ).thenReturn(pageOf(buildFluxResponse(1L)));

    mockMvc
      .perform(get("/api/flux").param("categorie", "LOYER"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu.length()").value(1));
  }

  @Test
  void findAll_avecCategorie_sansResultat_retournePageVide() throws Exception {
    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        isNull(),
        eq("TRAVAUX"),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq(0),
        eq(20)
      )
    ).thenReturn(emptyPage());

    mockMvc
      .perform(get("/api/flux").param("categorie", "TRAVAUX"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void findAll_avecPeriode_retourneFluxFiltres() throws Exception {
    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq("2026-04-01"),
        eq("2026-04-30"),
        isNull(),
        isNull(),
        isNull(),
        eq(0),
        eq(20)
      )
    ).thenReturn(pageOf(buildFluxResponse(1L)));

    mockMvc
      .perform(get("/api/flux").param("dateDebut", "2026-04-01").param("dateFin", "2026-04-30"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu.length()").value(1));
  }

  @Test
  void findAll_avecPeriode_sansResultat_retournePageVide() throws Exception {
    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq("2020-01-01"),
        eq("2020-01-31"),
        isNull(),
        isNull(),
        isNull(),
        eq(0),
        eq(20)
      )
    ).thenReturn(emptyPage());

    mockMvc
      .perform(get("/api/flux").param("dateDebut", "2020-01-01").param("dateFin", "2020-01-31"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void findAll_premierePage_retourneMetadonneesPagination() throws Exception {
    List<FluxResponse> contenu = List.of(buildFluxResponse(1L), buildFluxResponse(2L));
    FluxPageResponse page = new FluxPageResponse(contenu, 0, 2, 5L, 3);

    when(
      fluxService.findAllWithFilters(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(2))
    ).thenReturn(page);

    mockMvc
      .perform(get("/api/flux").param("page", "0").param("size", "2"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu.length()").value(2))
      .andExpect(jsonPath("$.page").value(0))
      .andExpect(jsonPath("$.taille").value(2))
      .andExpect(jsonPath("$.totalElements").value(5))
      .andExpect(jsonPath("$.totalPages").value(3));
  }

  @Test
  void findAll_pagesuivante_retourneMetadonneesPagination() throws Exception {
    List<FluxResponse> contenu = List.of(buildFluxResponse(3L), buildFluxResponse(4L));
    FluxPageResponse page = new FluxPageResponse(contenu, 1, 2, 5L, 3);

    when(
      fluxService.findAllWithFilters(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(1), eq(2))
    ).thenReturn(page);

    mockMvc
      .perform(get("/api/flux").param("page", "1").param("size", "2"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.page").value(1))
      .andExpect(jsonPath("$.contenu.length()").value(2));
  }

  @Test
  void findAll_taillePersonnalisee_retourneMetadonneesPagination() throws Exception {
    List<FluxResponse> contenu = List.of(
      buildFluxResponse(1L),
      buildFluxResponse(2L),
      buildFluxResponse(3L),
      buildFluxResponse(4L),
      buildFluxResponse(5L)
    );
    FluxPageResponse page = new FluxPageResponse(contenu, 0, 50, 5L, 1);

    when(
      fluxService.findAllWithFilters(
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq(0),
        eq(50)
      )
    ).thenReturn(page);

    mockMvc
      .perform(get("/api/flux").param("size", "50"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.taille").value(50))
      .andExpect(jsonPath("$.totalPages").value(1));
  }

  @Test
  void findAll_avecFiltresCombinesEtPagination_retourneResultatFiltrePagine() throws Exception {
    FluxPageResponse page = new FluxPageResponse(List.of(buildFluxResponse(1L)), 0, 5, 1L, 1);

    when(
      fluxService.findAllWithFilters(
        eq(1L),
        isNull(),
        eq("DEPENSE"),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        isNull(),
        eq("A_FOURNIR,INCOMPLET"),
        eq(0),
        eq(5)
      )
    ).thenReturn(page);

    mockMvc
      .perform(
        get("/api/flux")
          .param("bienId", "1")
          .param("typeFlux", "DEPENSE")
          .param("statutJustificatif", "A_FOURNIR,INCOMPLET")
          .param("page", "0")
          .param("size", "5")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.contenu.length()").value(1))
      .andExpect(jsonPath("$.totalElements").value(1));
  }
}
