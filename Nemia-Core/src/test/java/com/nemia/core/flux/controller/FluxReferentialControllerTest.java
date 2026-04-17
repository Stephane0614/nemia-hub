package com.nemia.core.flux.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FluxReferentialController.class)
class FluxReferentialControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnFluxReferentials() throws Exception {
    mockMvc
      .perform(get("/api/flux/referentials"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.types").isArray())
      .andExpect(jsonPath("$.categories").isArray())
      .andExpect(jsonPath("$.paymentModes").isArray())
      .andExpect(jsonPath("$.types[0].code").exists())
      .andExpect(jsonPath("$.types[0].label").exists())
      .andExpect(jsonPath("$.categories[0].code").exists())
      .andExpect(jsonPath("$.paymentModes[0].code").exists())
      .andExpect(jsonPath("$.types[?(@.code=='RECETTE')]").exists())
      .andExpect(jsonPath("$.types[?(@.code=='DEPENSE')]").exists())
      .andExpect(jsonPath("$.paymentModes[?(@.code=='VIREMENT')]").exists());
  }

  @Test
void shouldReturnOccurrencesAndStatutJustificatifsReferentials() throws Exception {
  mockMvc
    .perform(get("/api/flux/referentials"))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.occurrences").isArray())
    .andExpect(jsonPath("$.occurrences[0].code").exists())
    .andExpect(jsonPath("$.occurrences[0].label").exists())
    .andExpect(jsonPath("$.occurrences[?(@.code=='RECURRENT')]").exists())
    .andExpect(jsonPath("$.occurrences[?(@.code=='PONCTUEL')]").exists())
    .andExpect(jsonPath("$.occurrences[?(@.code=='INDETERMINE')]").exists())
    .andExpect(jsonPath("$.statutJustificatifs").isArray())
    .andExpect(jsonPath("$.statutJustificatifs[0].code").exists())
    .andExpect(jsonPath("$.statutJustificatifs[0].label").exists())
    .andExpect(jsonPath("$.statutJustificatifs[?(@.code=='FOURNI')]").exists())
    .andExpect(jsonPath("$.statutJustificatifs[?(@.code=='INCOMPLET')]").exists())
    .andExpect(jsonPath("$.statutJustificatifs[?(@.code=='NON_REQUIS')]").exists())
    .andExpect(jsonPath("$.statutJustificatifs[?(@.code=='A_VERIFIER')]").exists());
}

@Test
void shouldReturnQualificationPressentiesAndStatutTraitementsReferentials() throws Exception {
  mockMvc
    .perform(get("/api/flux/referentials"))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.qualificationPressenties").isArray())
    .andExpect(jsonPath("$.qualificationPressenties[0].code").exists())
    .andExpect(jsonPath("$.qualificationPressenties[0].label").exists())
    .andExpect(jsonPath("$.qualificationPressenties[?(@.code=='CHARGE_COURANTE')]").exists())
    .andExpect(jsonPath("$.qualificationPressenties[?(@.code=='IMMOBILISATION')]").exists())
    .andExpect(jsonPath("$.qualificationPressenties[?(@.code=='A_ARBITRER')]").exists())
    .andExpect(jsonPath("$.statutTraitements").isArray())
    .andExpect(jsonPath("$.statutTraitements[0].code").exists())
    .andExpect(jsonPath("$.statutTraitements[0].label").exists())
    .andExpect(jsonPath("$.statutTraitements[?(@.code=='A_REVOIR')]").exists())
    .andExpect(jsonPath("$.statutTraitements[?(@.code=='A_REVOIR')]").exists())
    .andExpect(jsonPath("$.statutTraitements[?(@.code=='VALIDE')]").exists());
}
}