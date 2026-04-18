package com.nemia.core.bien.controller;

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
class BienControllerValidationTest {

  @Mock
  private com.nemia.core.bien.service.BienService bienService;

  @InjectMocks
  private BienController bienController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders
      .standaloneSetup(bienController)
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldReturnBadRequestWhenNomUsuelIsBlank() throws Exception {
    String requestBody = """
      {
        "nomUsuel": "",
        "adresseSimplifiee": "12 rue des Capucins, 33000 Bordeaux",
        "statutActivite": "ACTIF"
      }
      """;

    mockMvc.perform(post("/api/biens").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.nomUsuel").exists());

    verifyNoInteractions(bienService);
  }

  @Test
  void shouldReturnBadRequestWhenAdresseIsBlank() throws Exception {
    String requestBody = """
      {
        "nomUsuel": "Studio Bordeaux",
        "adresseSimplifiee": "",
        "statutActivite": "ACTIF"
      }
      """;

    mockMvc.perform(post("/api/biens").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.adresseSimplifiee").exists());

    verifyNoInteractions(bienService);
  }

  @Test
  void shouldReturnBadRequestWhenStatutActiviteIsNull() throws Exception {
    String requestBody = """
      {
        "nomUsuel": "Studio Bordeaux",
        "adresseSimplifiee": "12 rue des Capucins, 33000 Bordeaux"
      }
      """;

    mockMvc.perform(post("/api/biens").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.statutActivite").exists());

    verifyNoInteractions(bienService);
  }

  @Test
  void shouldReturnBadRequestWhenNomUsuelIsTooLong() throws Exception {
    String nomTropLong = "A".repeat(121);

    String requestBody = """
      {
        "nomUsuel": "%s",
        "adresseSimplifiee": "12 rue des Capucins, 33000 Bordeaux",
        "statutActivite": "ACTIF"
      }
      """.formatted(nomTropLong);

    mockMvc.perform(post("/api/biens").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.nomUsuel").exists());

    verifyNoInteractions(bienService);
  }

  @Test
  void shouldReturnBadRequestWhenCommentaireIsTooLong() throws Exception {
    String commentaireTropLong = "A".repeat(501);

    String requestBody = """
      {
        "nomUsuel": "Studio Bordeaux",
        "adresseSimplifiee": "12 rue des Capucins, 33000 Bordeaux",
        "statutActivite": "ACTIF",
        "commentaire": "%s"
      }
      """.formatted(commentaireTropLong);

    mockMvc.perform(post("/api/biens").contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.validationErrors.commentaire").exists());

    verifyNoInteractions(bienService);
  }
}