package com.nemia.core.exercice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nemia.core.common.exception.ExerciceAlreadyExistsException;
import com.nemia.core.common.exception.ExerciceNotFoundException;
import com.nemia.core.exercice.dto.ExerciceResponse;
import com.nemia.core.exercice.model.NiveauCompletude;
import com.nemia.core.exercice.model.StatutExercice;
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
class ExerciceControllerTest {

  @Mock
  private com.nemia.core.exercice.service.ExerciceService exerciceService;

  @InjectMocks
  private ExerciceController exerciceController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(exerciceController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldCreateExercice() throws Exception {
    ExerciceResponse response = buildExerciceResponse(
      1L,
      "2026",
      LocalDate.of(2026, 1, 1),
      LocalDate.of(2026, 12, 31),
      StatutExercice.OUVERT,
      NiveauCompletude.FAIBLE
    );

    when(exerciceService.create(any())).thenReturn(response);

    String requestBody = """
      {
        "libelleExercice": "2026",
        "dateDebut": "2026-01-01",
        "dateFin": "2026-12-31",
        "statutExercice": "OUVERT",
        "niveauCompletude": "FAIBLE",
        "commentaire": "Exercice en cours"
      }
      """;

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.libelleExercice").value("2026"))
      .andExpect(jsonPath("$.statutExercice").value("OUVERT"));

    verify(exerciceService).create(any());
  }

  @Test
  void shouldCreateExerciceWithOptionalFieldsAbsent() throws Exception {
    ExerciceResponse response = buildExerciceResponse(
      2L,
      "2025",
      LocalDate.of(2025, 1, 1),
      LocalDate.of(2025, 12, 31),
      StatutExercice.CLOTURE,
      null
    );

    when(exerciceService.create(any())).thenReturn(response);

    String requestBody = """
      {
        "libelleExercice": "2025",
        "dateDebut": "2025-01-01",
        "dateFin": "2025-12-31",
        "statutExercice": "CLOTURE"
      }
      """;

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.libelleExercice").value("2025"));

    verify(exerciceService).create(any());
  }

  @Test
  void shouldReturn409WhenCreatingDuplicateExercice() throws Exception {
    when(exerciceService.create(any())).thenThrow(new ExerciceAlreadyExistsException("2026"));

    String requestBody = """
      {
        "libelleExercice": "2026",
        "dateDebut": "2026-01-01",
        "dateFin": "2026-12-31",
        "statutExercice": "OUVERT"
      }
      """;

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.message").value("Un exercice existe déjà avec ce libellé : 2026"));

    verify(exerciceService).create(any());
  }

  @Test
  void shouldReturn400WhenDateFinBeforeDateDebut() throws Exception {
    when(exerciceService.create(any())).thenThrow(
      new IllegalArgumentException("La date de fin doit être strictement postérieure à la date de début.")
    );

    String requestBody = """
      {
        "libelleExercice": "2026",
        "dateDebut": "2026-12-31",
        "dateFin": "2026-01-01",
        "statutExercice": "OUVERT"
      }
      """;

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("La date de fin doit être strictement postérieure à la date de début."));

    verify(exerciceService).create(any());
  }

  @Test
  void shouldReturnAllExercices() throws Exception {
    ExerciceResponse first = buildExerciceResponse(
      1L,
      "2026",
      LocalDate.of(2026, 1, 1),
      LocalDate.of(2026, 12, 31),
      StatutExercice.OUVERT,
      NiveauCompletude.FAIBLE
    );
    ExerciceResponse second = buildExerciceResponse(
      2L,
      "2025",
      LocalDate.of(2025, 1, 1),
      LocalDate.of(2025, 12, 31),
      StatutExercice.CLOTURE,
      NiveauCompletude.COMPLET
    );

    when(exerciceService.findAll()).thenReturn(List.of(first, second));

    mockMvc
      .perform(get("/api/exercices"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].libelleExercice").value("2026"))
      .andExpect(jsonPath("$[1].libelleExercice").value("2025"));

    verify(exerciceService).findAll();
  }

  @Test
  void shouldReturnExerciceById() throws Exception {
    Long id = 1L;
    ExerciceResponse response = buildExerciceResponse(
      id,
      "2026",
      LocalDate.of(2026, 1, 1),
      LocalDate.of(2026, 12, 31),
      StatutExercice.OUVERT,
      NiveauCompletude.FAIBLE
    );

    when(exerciceService.findById(id)).thenReturn(response);

    mockMvc
      .perform(get("/api/exercices/{id}", id))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.libelleExercice").value("2026"));

    verify(exerciceService).findById(id);
  }

  @Test
  void shouldReturn404WhenExerciceNotFound() throws Exception {
    Long id = 99L;
    when(exerciceService.findById(id)).thenThrow(new ExerciceNotFoundException(id));

    mockMvc
      .perform(get("/api/exercices/{id}", id))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("Exercice introuvable : 99"));

    verify(exerciceService).findById(id);
  }

  @Test
  void shouldUpdateExercice() throws Exception {
    Long id = 1L;
    ExerciceResponse response = buildExerciceResponse(
      id,
      "2026 MAJ",
      LocalDate.of(2026, 1, 1),
      LocalDate.of(2026, 12, 31),
      StatutExercice.EN_PREPARATION_DE_CLOTURE,
      NiveauCompletude.AVANCE
    );

    when(exerciceService.update(eq(id), any())).thenReturn(response);

    String requestBody = """
      {
        "libelleExercice": "2026 MAJ",
        "dateDebut": "2026-01-01",
        "dateFin": "2026-12-31",
        "statutExercice": "EN_PREPARATION_DE_CLOTURE",
        "niveauCompletude": "AVANCE"
      }
      """;

    mockMvc
      .perform(put("/api/exercices/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.libelleExercice").value("2026 MAJ"))
      .andExpect(jsonPath("$.statutExercice").value("EN_PREPARATION_DE_CLOTURE"));

    verify(exerciceService).update(eq(id), any());
  }

  @Test
  void shouldReturn404WhenUpdatingNonExistentExercice() throws Exception {
    Long id = 99L;
    when(exerciceService.update(eq(id), any())).thenThrow(new ExerciceNotFoundException(id));

    String requestBody = """
      {
        "libelleExercice": "2026",
        "dateDebut": "2026-01-01",
        "dateFin": "2026-12-31",
        "statutExercice": "OUVERT"
      }
      """;

    mockMvc
      .perform(put("/api/exercices/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isNotFound());

    verify(exerciceService).update(eq(id), any());
  }

  @Test
  void shouldDeleteExercice() throws Exception {
    Long id = 1L;
    doNothing().when(exerciceService).delete(id);

    mockMvc.perform(delete("/api/exercices/{id}", id)).andDo(print()).andExpect(status().isNoContent());

    verify(exerciceService).delete(id);
  }

  @Test
  void shouldReturn404WhenDeletingNonExistentExercice() throws Exception {
    Long id = 99L;
    doThrow(new ExerciceNotFoundException(id)).when(exerciceService).delete(id);

    mockMvc.perform(delete("/api/exercices/{id}", id)).andDo(print()).andExpect(status().isNotFound());

    verify(exerciceService).delete(id);
  }

  private ExerciceResponse buildExerciceResponse(
    Long id,
    String libelle,
    LocalDate dateDebut,
    LocalDate dateFin,
    StatutExercice statut,
    NiveauCompletude niveau
  ) {
    ExerciceResponse response = new ExerciceResponse();
    response.setId(id);
    response.setLibelleExercice(libelle);
    response.setDateDebut(dateDebut);
    response.setDateFin(dateFin);
    response.setStatutExercice(statut);
    response.setNiveauCompletude(niveau);
    response.setCommentaire(null);
    response.setCreatedAt(LocalDateTime.of(2026, 4, 18, 10, 0));
    response.setUpdatedAt(LocalDateTime.of(2026, 4, 18, 10, 0));
    return response;
  }
}
