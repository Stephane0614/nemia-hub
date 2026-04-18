package com.nemia.core.exercice.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class ExerciceControllerValidationTest {

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
  void shouldReturnBadRequestWhenLibelleIsBlank() throws Exception {
    String requestBody = """
      {
        "libelleExercice": "",
        "dateDebut": "2026-01-01",
        "dateFin": "2026-12-31",
        "statutExercice": "OUVERT"
      }
      """;

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.libelleExercice").exists());

    verifyNoInteractions(exerciceService);
  }

  @Test
  void shouldReturnBadRequestWhenDateDebutIsNull() throws Exception {
    String requestBody = """
      {
        "libelleExercice": "2026",
        "dateFin": "2026-12-31",
        "statutExercice": "OUVERT"
      }
      """;

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.dateDebut").exists());

    verifyNoInteractions(exerciceService);
  }

  @Test
  void shouldReturnBadRequestWhenDateFinIsNull() throws Exception {
    String requestBody = """
      {
        "libelleExercice": "2026",
        "dateDebut": "2026-01-01",
        "statutExercice": "OUVERT"
      }
      """;

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.dateFin").exists());

    verifyNoInteractions(exerciceService);
  }

  @Test
  void shouldReturnBadRequestWhenStatutExerciceIsNull() throws Exception {
    String requestBody = """
      {
        "libelleExercice": "2026",
        "dateDebut": "2026-01-01",
        "dateFin": "2026-12-31"
      }
      """;

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.statutExercice").exists());

    verifyNoInteractions(exerciceService);
  }

  @Test
  void shouldReturnBadRequestWhenLibelleIsTooLong() throws Exception {
    String libelleTropLong = "A".repeat(51);

    String requestBody = """
      {
        "libelleExercice": "%s",
        "dateDebut": "2026-01-01",
        "dateFin": "2026-12-31",
        "statutExercice": "OUVERT"
      }
      """.formatted(libelleTropLong);

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.libelleExercice").exists());

    verifyNoInteractions(exerciceService);
  }

  @Test
  void shouldReturnBadRequestWhenCommentaireIsTooLong() throws Exception {
    String commentaireTropLong = "A".repeat(501);

    String requestBody = """
      {
        "libelleExercice": "2026",
        "dateDebut": "2026-01-01",
        "dateFin": "2026-12-31",
        "statutExercice": "OUVERT",
        "commentaire": "%s"
      }
      """.formatted(commentaireTropLong);

    mockMvc
      .perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.commentaire").exists());

    verifyNoInteractions(exerciceService);
  }
}
