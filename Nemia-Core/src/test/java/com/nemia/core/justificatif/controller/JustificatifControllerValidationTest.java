package com.nemia.core.justificatif.controller;

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
class JustificatifControllerValidationTest {

  @Mock
  private com.nemia.core.justificatif.service.JustificatifService justificatifService;

  @InjectMocks
  private JustificatifController justificatifController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(justificatifController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldReturnBadRequestWhenTypePieceIsNull() throws Exception {
    String requestBody = """
      {
        "statutDocumentaire": "FOURNI"
      }
      """;

    mockMvc
      .perform(post("/api/justificatifs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.typePiece").exists());

    verifyNoInteractions(justificatifService);
  }

  @Test
  void shouldReturnBadRequestWhenStatutDocumentaireIsNull() throws Exception {
    String requestBody = """
      {
        "typePiece": "FACTURE"
      }
      """;

    mockMvc
      .perform(post("/api/justificatifs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.statutDocumentaire").exists());

    verifyNoInteractions(justificatifService);
  }

  @Test
  void shouldReturnBadRequestWhenReferencePieceIsTooLong() throws Exception {
    String referenceTropLongue = "A".repeat(101);

    String requestBody = """
      {
        "typePiece": "FACTURE",
        "statutDocumentaire": "FOURNI",
        "referencePiece": "%s"
      }
      """.formatted(referenceTropLongue);

    mockMvc
      .perform(post("/api/justificatifs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.referencePiece").exists());

    verifyNoInteractions(justificatifService);
  }

  @Test
  void shouldReturnBadRequestWhenEmetteurIsTooLong() throws Exception {
    String emetteurTropLong = "A".repeat(151);

    String requestBody = """
      {
        "typePiece": "FACTURE",
        "statutDocumentaire": "FOURNI",
        "emetteur": "%s"
      }
      """.formatted(emetteurTropLong);

    mockMvc
      .perform(post("/api/justificatifs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.emetteur").exists());

    verifyNoInteractions(justificatifService);
  }

  @Test
  void shouldReturnBadRequestWhenCommentaireIsTooLong() throws Exception {
    String commentaireTropLong = "A".repeat(501);

    String requestBody = """
      {
        "typePiece": "FACTURE",
        "statutDocumentaire": "FOURNI",
        "commentaire": "%s"
      }
      """.formatted(commentaireTropLong);

    mockMvc
      .perform(post("/api/justificatifs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.commentaire").exists());

    verifyNoInteractions(justificatifService);
  }
}
