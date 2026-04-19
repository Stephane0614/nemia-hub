package com.nemia.core.justificatif.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class JustificatifReferentialControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders
      .standaloneSetup(new JustificatifReferentialController())
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldReturnJustificatifReferentials() throws Exception {
    mockMvc
      .perform(get("/api/justificatifs/referentials"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.typePieces").isArray())
      .andExpect(jsonPath("$.typePieces[0].code").exists())
      .andExpect(jsonPath("$.typePieces[0].label").exists())
      .andExpect(jsonPath("$.typePieces[?(@.code=='FACTURE')]").exists())
      .andExpect(jsonPath("$.typePieces[?(@.code=='TICKET')]").exists())
      .andExpect(jsonPath("$.typePieces[?(@.code=='RELEVE')]").exists())
      .andExpect(jsonPath("$.typePieces[?(@.code=='ECHEANCIER')]").exists())
      .andExpect(jsonPath("$.typePieces[?(@.code=='ACTE')]").exists())
      .andExpect(jsonPath("$.typePieces[?(@.code=='DEVIS')]").exists())
      .andExpect(jsonPath("$.typePieces[?(@.code=='AUTRE')]").exists())
      .andExpect(jsonPath("$.statutDocumentaires").isArray())
      .andExpect(jsonPath("$.statutDocumentaires[0].code").exists())
      .andExpect(jsonPath("$.statutDocumentaires[0].label").exists())
      .andExpect(jsonPath("$.statutDocumentaires[?(@.code=='A_FOURNIR')]").exists())
      .andExpect(jsonPath("$.statutDocumentaires[?(@.code=='FOURNI')]").exists())
      .andExpect(jsonPath("$.statutDocumentaires[?(@.code=='INCOMPLET')]").exists())
      .andExpect(jsonPath("$.statutDocumentaires[?(@.code=='A_VERIFIER')]").exists())
      .andExpect(jsonPath("$.statutDocumentaires[?(@.code=='REJETE')]").exists());
  }
}