package com.nemia.core.emprunt.controller;

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

import com.nemia.core.common.exception.EmpruntNotFoundException;
import com.nemia.core.emprunt.dto.EmpruntResponse;
import com.nemia.core.emprunt.model.StatutEmprunt;
import com.nemia.core.emprunt.service.EmpruntService;
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
class EmpruntControllerTest {

  @Mock
  private EmpruntService empruntService;

  @InjectMocks
  private EmpruntController empruntController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(empruntController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  // ── Helpers ──

  private EmpruntResponse buildResponse(Long id) {
    EmpruntResponse r = new EmpruntResponse();
    r.setId(id);
    r.setReferencePret("PRET-001");
    r.setBienId(1L);
    r.setOrganismePreteur("Banque Nationale");
    r.setMensualiteTotale(new BigDecimal("850.00"));
    r.setDatePremiereEcheance(LocalDate.of(2026, 1, 1));
    r.setDateDerniereEcheance(LocalDate.of(2046, 1, 1));
    r.setStatutEmprunt(StatutEmprunt.EN_COURS);
    r.setCommentaire("Prêt immobilier principal");
    r.setCreatedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
    r.setUpdatedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
    return r;
  }

  private String requestBodyComplet() {
    return """
    {
      "referencePret": "PRET-001",
      "bienId": 1,
      "organismePreteur": "Banque Nationale",
      "mensualiteTotale": 850.00,
      "datePremiereEcheance": "2026-01-01",
      "dateDerniereEcheance": "2046-01-01",
      "statutEmprunt": "EN_COURS",
      "commentaire": "Prêt immobilier principal"
    }
    """;
  }

  private String requestBodyMinimal() {
    return """
    {
      "referencePret": "PRET-002",
      "bienId": 1,
      "statutEmprunt": "EN_COURS"
    }
    """;
  }

  // ── CRUD ──

  @Test
  void shouldCreateEmpruntComplet() throws Exception {
    when(empruntService.create(any())).thenReturn(buildResponse(1L));

    mockMvc
      .perform(post("/api/emprunts").contentType(MediaType.APPLICATION_JSON).content(requestBodyComplet()))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.referencePret").value("PRET-001"))
      .andExpect(jsonPath("$.bienId").value(1))
      .andExpect(jsonPath("$.organismePreteur").value("Banque Nationale"))
      .andExpect(jsonPath("$.mensualiteTotale").value(850.00))
      .andExpect(jsonPath("$.statutEmprunt").value("EN_COURS"));

    verify(empruntService).create(any());
  }

  @Test
  void shouldCreateEmpruntSansChampsOptionnels() throws Exception {
    EmpruntResponse r = buildResponse(2L);
    r.setOrganismePreteur(null);
    r.setMensualiteTotale(null);
    r.setDatePremiereEcheance(null);
    r.setDateDerniereEcheance(null);
    r.setCommentaire(null);
    when(empruntService.create(any())).thenReturn(r);

    mockMvc
      .perform(post("/api/emprunts").contentType(MediaType.APPLICATION_JSON).content(requestBodyMinimal()))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(2))
      .andExpect(jsonPath("$.referencePret").value("PRET-001"));

    verify(empruntService).create(any());
  }

  @Test
  void shouldReturnAllEmprunts() throws Exception {
    when(empruntService.findAll(null)).thenReturn(List.of(buildResponse(1L), buildResponse(2L)));

    mockMvc
      .perform(get("/api/emprunts"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2));

    verify(empruntService).findAll(null);
  }

  @Test
  void shouldReturnEmpruntsFilteredByBienId() throws Exception {
    when(empruntService.findAll(1L)).thenReturn(List.of(buildResponse(1L)));

    mockMvc
      .perform(get("/api/emprunts").param("bienId", "1"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1));

    verify(empruntService).findAll(1L);
  }

  @Test
  void shouldReturnEmptyListWhenNoBienId() throws Exception {
    when(empruntService.findAll(99L)).thenReturn(List.of());

    mockMvc
      .perform(get("/api/emprunts").param("bienId", "99"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void shouldReturnEmpruntById() throws Exception {
    when(empruntService.findById(1L)).thenReturn(buildResponse(1L));

    mockMvc
      .perform(get("/api/emprunts/{id}", 1L))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.referencePret").value("PRET-001"));

    verify(empruntService).findById(1L);
  }

  @Test
  void shouldReturn404WhenEmpruntNotFound() throws Exception {
    when(empruntService.findById(99L)).thenThrow(new EmpruntNotFoundException(99L));

    mockMvc
      .perform(get("/api/emprunts/{id}", 99L))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("Emprunt introuvable : 99"));

    verify(empruntService).findById(99L);
  }

  @Test
  void shouldUpdateEmprunt() throws Exception {
    EmpruntResponse updated = buildResponse(1L);
    updated.setReferencePret("PRET-001-MAJ");
    updated.setMensualiteTotale(new BigDecimal("900.00"));
    when(empruntService.update(eq(1L), any())).thenReturn(updated);

    String body = """
      {
        "referencePret": "PRET-001-MAJ",
        "bienId": 1,
        "mensualiteTotale": 900.00,
        "statutEmprunt": "EN_COURS"
      }
      """;

    mockMvc
      .perform(put("/api/emprunts/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.referencePret").value("PRET-001-MAJ"))
      .andExpect(jsonPath("$.mensualiteTotale").value(900.00));

    verify(empruntService).update(eq(1L), any());
  }

  @Test
  void shouldReturn404WhenUpdatingNonExistentEmprunt() throws Exception {
    when(empruntService.update(eq(99L), any())).thenThrow(new EmpruntNotFoundException(99L));

    mockMvc
      .perform(put("/api/emprunts/{id}", 99L).contentType(MediaType.APPLICATION_JSON).content(requestBodyComplet()))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldDeleteEmprunt() throws Exception {
    doNothing().when(empruntService).delete(1L);

    mockMvc.perform(delete("/api/emprunts/{id}", 1L)).andDo(print()).andExpect(status().isNoContent());

    verify(empruntService).delete(1L);
  }

  @Test
  void shouldReturn404WhenDeletingNonExistentEmprunt() throws Exception {
    doThrow(new EmpruntNotFoundException(99L)).when(empruntService).delete(99L);

    mockMvc.perform(delete("/api/emprunts/{id}", 99L)).andDo(print()).andExpect(status().isNotFound());
  }

  // ── Validations ──

  @Test
  void shouldReturn400WhenReferencePretIsBlank() throws Exception {
    String body = """
      {
        "referencePret": "",
        "bienId": 1,
        "statutEmprunt": "EN_COURS"
      }
      """;

    mockMvc
      .perform(post("/api/emprunts").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.referencePret").exists());

    verifyNoInteractions(empruntService);
  }

  @Test
  void shouldReturn400WhenBienIdIsNull() throws Exception {
    String body = """
      {
        "referencePret": "PRET-001",
        "statutEmprunt": "EN_COURS"
      }
      """;

    mockMvc
      .perform(post("/api/emprunts").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.bienId").exists());

    verifyNoInteractions(empruntService);
  }

  @Test
  void shouldReturn400WhenStatutEmpruntIsNull() throws Exception {
    String body = """
      {
        "referencePret": "PRET-001",
        "bienId": 1
      }
      """;

    mockMvc
      .perform(post("/api/emprunts").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.statutEmprunt").exists());

    verifyNoInteractions(empruntService);
  }

  @Test
  void shouldReturn400WhenMensualiteIsNegative() throws Exception {
    String body = """
      {
        "referencePret": "PRET-001",
        "bienId": 1,
        "mensualiteTotale": -500.00,
        "statutEmprunt": "EN_COURS"
      }
      """;

    mockMvc
      .perform(post("/api/emprunts").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.mensualiteTotale").exists());

    verifyNoInteractions(empruntService);
  }

  @Test
  void shouldReturn400WhenDateDerniereEcheanceBeforeDatePremiereEcheance() throws Exception {
    when(empruntService.create(any())).thenThrow(
      new IllegalArgumentException("La date de dernière échéance ne peut pas être antérieure à la date de première échéance.")
    );

    String body = """
      {
        "referencePret": "PRET-001",
        "bienId": 1,
        "datePremiereEcheance": "2046-01-01",
        "dateDerniereEcheance": "2026-01-01",
        "statutEmprunt": "EN_COURS"
      }
      """;

    mockMvc
      .perform(post("/api/emprunts").contentType(MediaType.APPLICATION_JSON).content(body))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(
        jsonPath("$.message").value("La date de dernière échéance ne peut pas être antérieure à la date de première échéance.")
      );
  }
}
