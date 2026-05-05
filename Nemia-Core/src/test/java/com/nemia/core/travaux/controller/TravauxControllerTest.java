package com.nemia.core.travaux.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nemia.core.common.exception.TravauxNotFoundException;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.travaux.dto.TravauxResponse;
import com.nemia.core.travaux.model.FinalitePressentie;
import com.nemia.core.travaux.model.StatutTravaux;
import com.nemia.core.travaux.service.TravauxService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TravauxControllerTest {

  @Mock
  private TravauxService travauxService;

  @InjectMocks
  private TravauxController travauxController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(travauxController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  // ── Helpers ──

  private TravauxResponse buildResponse(Long id) {
    TravauxResponse r = new TravauxResponse();
    r.setId(id);
    r.setLibelleTravaux("Rénovation salle de bain");
    r.setBienId(1L);
    r.setDateDebut(LocalDate.of(2026, 3, 1));
    r.setDateFin(LocalDate.of(2026, 3, 31));
    r.setMontantTotal(new BigDecimal("4500.00"));
    r.setFinalitePressentie(FinalitePressentie.AMELIORATION);
    r.setQualificationPressentie(QualificationPressentie.IMMOBILISATION);
    r.setStatutTravaux(StatutTravaux.BRUT);
    r.setCommentaire("Remplacement baignoire");
    r.setCreatedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
    r.setUpdatedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
    return r;
  }

  private String requestBodyComplet() {
    return """
    {
      "libelleTravaux": "Rénovation salle de bain",
      "bienId": 1,
      "dateDebut": "2026-03-01",
      "dateFin": "2026-03-31",
      "montantTotal": 4500.00,
      "finalitePressentie": "AMELIORATION",
      "qualificationPressentie": "IMMOBILISATION",
      "statutTravaux": "BRUT",
      "commentaire": "Remplacement baignoire"
    }
    """;
  }

  private String requestBodyMinimal() {
    return """
    {
      "libelleTravaux": "Peinture",
      "bienId": 1,
      "montantTotal": 800.00,
      "finalitePressentie": "ENTRETIEN_COURANT",
      "qualificationPressentie": "CHARGE_COURANTE"
    }
    """;
  }

  // ── CRUD ──

  @Test
  void shouldCreateTravauxComplet() throws Exception {
    when(travauxService.create(any())).thenReturn(buildResponse(1L));

    mockMvc
      .perform(post("/api/travaux").contentType(MediaType.APPLICATION_JSON).content(requestBodyComplet()))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.libelleTravaux").value("Rénovation salle de bain"))
      .andExpect(jsonPath("$.bienId").value(1))
      .andExpect(jsonPath("$.montantTotal").value(4500.00))
      .andExpect(jsonPath("$.finalitePressentie").value("AMELIORATION"))
      .andExpect(jsonPath("$.qualificationPressentie").value("IMMOBILISATION"))
      .andExpect(jsonPath("$.statutTravaux").value("BRUT"));

    verify(travauxService).create(any());
  }

  @Test
  void shouldCreateTravauxSansChampsOptionnels() throws Exception {
    TravauxResponse r = buildResponse(2L);
    r.setDateDebut(null);
    r.setDateFin(null);
    r.setCommentaire(null);
    r.setStatutTravaux(null);
    when(travauxService.create(any())).thenReturn(r);

    mockMvc
      .perform(post("/api/travaux").contentType(MediaType.APPLICATION_JSON).content(requestBodyMinimal()))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(2))
      .andExpect(jsonPath("$.libelleTravaux").value("Rénovation salle de bain"));

    verify(travauxService).create(any());
  }

  @Test
  void shouldReturnAllTravaux() throws Exception {
    when(travauxService.findAll(null)).thenReturn(List.of(buildResponse(1L), buildResponse(2L)));

    mockMvc
      .perform(get("/api/travaux"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2));

    verify(travauxService).findAll(null);
  }

  @Test
  void shouldReturnTravauxFilteredByBienId() throws Exception {
    when(travauxService.findAll(1L)).thenReturn(List.of(buildResponse(1L)));

    mockMvc
      .perform(get("/api/travaux").param("bienId", "1"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1));

    verify(travauxService).findAll(1L);
  }

  @Test
  void shouldReturnEmptyListWhenNoBienId() throws Exception {
    when(travauxService.findAll(99L)).thenReturn(List.of());

    mockMvc
      .perform(get("/api/travaux").param("bienId", "99"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void shouldReturnTravauxById() throws Exception {
    when(travauxService.findById(1L)).thenReturn(buildResponse(1L));

    mockMvc
      .perform(get("/api/travaux/{id}", 1L))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.libelleTravaux").value("Rénovation salle de bain"));

    verify(travauxService).findById(1L);
  }

  @Test
  void shouldReturn404WhenTravauxNotFound() throws Exception {
    when(travauxService.findById(99L)).thenThrow(new TravauxNotFoundException(99L));

    mockMvc
      .perform(get("/api/travaux/{id}", 99L))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("Travaux introuvable : 99"));

    verify(travauxService).findById(99L);
  }

  @Test
  void shouldUpdateTravaux() throws Exception {
    TravauxResponse updated = buildResponse(1L);
    updated.setLibelleTravaux("Rénovation cuisine");
    updated.setMontantTotal(new BigDecimal("6000.00"));
    when(travauxService.update(eq(1L), any())).thenReturn(updated);

    String body = """
      {
        "libelleTravaux": "Rénovation cuisine",
        "bienId": 1,
        "montantTotal": 6000.00,
        "finalitePressentie": "AMELIORATION",
        "qualificationPressentie": "IMMOBILISATION"
      }
      """;

    mockMvc
      .perform(put("/api/travaux/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.libelleTravaux").value("Rénovation cuisine"))
      .andExpect(jsonPath("$.montantTotal").value(6000.00));

    verify(travauxService).update(eq(1L), any());
  }

  @Test
  void shouldReturn404WhenUpdatingNonExistentTravaux() throws Exception {
    when(travauxService.update(eq(99L), any())).thenThrow(new TravauxNotFoundException(99L));

    mockMvc
      .perform(put("/api/travaux/{id}", 99L).contentType(MediaType.APPLICATION_JSON).content(requestBodyComplet()))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldDeleteTravaux() throws Exception {
    doNothing().when(travauxService).delete(1L);

    mockMvc.perform(delete("/api/travaux/{id}", 1L)).andDo(print()).andExpect(status().isNoContent());

    verify(travauxService).delete(1L);
  }

  @Test
  void shouldReturn404WhenDeletingNonExistentTravaux() throws Exception {
    doThrow(new TravauxNotFoundException(99L)).when(travauxService).delete(99L);

    mockMvc.perform(delete("/api/travaux/{id}", 99L)).andDo(print()).andExpect(status().isNotFound());
  }

  // ── Validations ──

  @Test
  void shouldReturn400WhenLibelleIsBlank() throws Exception {
    String body = """
      {
        "libelleTravaux": "",
        "bienId": 1,
        "montantTotal": 1000.00,
        "finalitePressentie": "ENTRETIEN_COURANT",
        "qualificationPressentie": "CHARGE_COURANTE"
      }
      """;

    mockMvc
      .perform(post("/api/travaux").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.libelleTravaux").exists());

    verifyNoInteractions(travauxService);
  }

  @Test
  void shouldReturn400WhenBienIdIsNull() throws Exception {
    String body = """
      {
        "libelleTravaux": "Travaux test",
        "montantTotal": 1000.00,
        "finalitePressentie": "ENTRETIEN_COURANT",
        "qualificationPressentie": "CHARGE_COURANTE"
      }
      """;

    mockMvc
      .perform(post("/api/travaux").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.bienId").exists());

    verifyNoInteractions(travauxService);
  }

  @Test
  void shouldReturn400WhenMontantIsNegative() throws Exception {
    String body = """
      {
        "libelleTravaux": "Travaux test",
        "bienId": 1,
        "montantTotal": -500.00,
        "finalitePressentie": "ENTRETIEN_COURANT",
        "qualificationPressentie": "CHARGE_COURANTE"
      }
      """;

    mockMvc
      .perform(post("/api/travaux").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.montantTotal").exists());

    verifyNoInteractions(travauxService);
  }

  @Test
  void shouldReturn400WhenFinaliteIsNull() throws Exception {
    String body = """
      {
        "libelleTravaux": "Travaux test",
        "bienId": 1,
        "montantTotal": 1000.00,
        "qualificationPressentie": "CHARGE_COURANTE"
      }
      """;

    mockMvc
      .perform(post("/api/travaux").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.finalitePressentie").exists());

    verifyNoInteractions(travauxService);
  }

  @Test
  void shouldReturn400WhenQualificationIsNull() throws Exception {
    String body = """
      {
        "libelleTravaux": "Travaux test",
        "bienId": 1,
        "montantTotal": 1000.00,
        "finalitePressentie": "ENTRETIEN_COURANT"
      }
      """;

    mockMvc
      .perform(post("/api/travaux").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.qualificationPressentie").exists());

    verifyNoInteractions(travauxService);
  }

  @Test
  void shouldReturn400WhenDateFinBeforeDateDebut() throws Exception {
    when(travauxService.create(any())).thenThrow(
      new IllegalArgumentException("La date de fin des travaux ne peut pas être antérieure à la date de début.")
    );

    String body = """
      {
        "libelleTravaux": "Travaux test",
        "bienId": 1,
        "dateDebut": "2026-03-31",
        "dateFin": "2026-03-01",
        "montantTotal": 1000.00,
        "finalitePressentie": "ENTRETIEN_COURANT",
        "qualificationPressentie": "CHARGE_COURANTE"
      }
      """;

    mockMvc
      .perform(post("/api/travaux").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("La date de fin des travaux ne peut pas être antérieure à la date de début."));
  }
}
