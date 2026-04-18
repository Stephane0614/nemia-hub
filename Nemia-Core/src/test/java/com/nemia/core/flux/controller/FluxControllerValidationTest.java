package com.nemia.core.flux.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nemia.core.flux.service.FluxService;
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
class FluxControllerValidationTest {

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

  @Test
  void shouldReturnBadRequestWhenCreateRequestIsInvalid() throws Exception {
    String invalidRequestBody = """
      {
        "date": "2026-04-15",
        "type": "DEPENSE",
        "libelle": "Abonnement internet résidence meublée avec un libellé volontairement beaucoup trop long pour dépasser clairement la limite maximale attendue",
        "montant": -29.99,
        "categorie": "INTERNET",
        "modePaiement": "PRELEVEMENT",
        "commentaire": "test validation"
      }
      """;

    mockMvc
      .perform(post("/api/flux").contentType(MediaType.APPLICATION_JSON).content(invalidRequestBody))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }
}
