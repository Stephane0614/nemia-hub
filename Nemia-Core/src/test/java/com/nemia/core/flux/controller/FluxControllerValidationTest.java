package com.nemia.core.flux.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nemia.core.flux.service.FluxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FluxController.class)
class FluxControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FluxService fluxService;

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
      .perform(
        post("/api/flux").contentType(MediaType.APPLICATION_JSON).content(invalidRequestBody)
      )
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Validation failed"))
      .andExpect(jsonPath("$.path").value("/api/flux"))
      .andExpect(jsonPath("$.validationErrors.libelle").exists())
      .andExpect(jsonPath("$.validationErrors.montant").exists());

    verifyNoInteractions(fluxService);
  }
}
