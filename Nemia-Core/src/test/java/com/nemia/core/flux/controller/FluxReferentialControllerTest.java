package com.nemia.core.flux.controller;

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
class FluxReferentialControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(new FluxReferentialController()).build();
  }

  @Test
  void shouldReturnFluxReferentials() throws Exception {
    mockMvc
      .perform(get("/api/flux/referentials"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.types").isArray())
      .andExpect(jsonPath("$.categories").isArray())
      .andExpect(jsonPath("$.paymentModes").isArray())
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
      .andExpect(jsonPath("$.occurrences[?(@.code=='RECURRENT')]").exists())
      .andExpect(jsonPath("$.occurrences[?(@.code=='PONCTUEL')]").exists())
      .andExpect(jsonPath("$.statutJustificatifs").isArray())
      .andExpect(jsonPath("$.statutJustificatifs[?(@.code=='FOURNI')]").exists())
      .andExpect(jsonPath("$.statutJustificatifs[?(@.code=='INCOMPLET')]").exists());
  }

  @Test
  void shouldReturnQualificationPressentiesAndStatutTraitementsReferentials() throws Exception {
    mockMvc
      .perform(get("/api/flux/referentials"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.qualificationPressenties").isArray())
      .andExpect(jsonPath("$.qualificationPressenties[?(@.code=='CHARGE_COURANTE')]").exists())
      .andExpect(jsonPath("$.qualificationPressenties[?(@.code=='A_ARBITRER')]").exists())
      .andExpect(jsonPath("$.statutTraitements").isArray())
      .andExpect(jsonPath("$.statutTraitements[?(@.code=='A_REVOIR')]").exists())
      .andExpect(jsonPath("$.statutTraitements[?(@.code=='VALIDE')]").exists());
  }

  // -------------------------------------------------------------------------
  // GET /api/flux/referentials/categories — mission #41
  // -------------------------------------------------------------------------

  @Test
  void shouldReturnOnlyRecetteCategoriesForTypeRecette() throws Exception {
    mockMvc
      .perform(get("/api/flux/referentials/categories").param("typeFlux", "RECETTE"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.code=='LOYER')]").exists())
      .andExpect(jsonPath("$[?(@.code=='CHARGES_REFACTUREES')]").exists())
      .andExpect(jsonPath("$[?(@.code=='ELECTRICITE')]").doesNotExist())
      .andExpect(jsonPath("$[?(@.code=='EMPRUNT_CAPITAL')]").doesNotExist());
  }

  @Test
  void shouldReturnOnlyDepenseCategoriesForTypeDepense() throws Exception {
    mockMvc
      .perform(get("/api/flux/referentials/categories").param("typeFlux", "DEPENSE"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.code=='ELECTRICITE')]").exists())
      .andExpect(jsonPath("$[?(@.code=='TRAVAUX')]").exists())
      .andExpect(jsonPath("$[?(@.code=='LOYER')]").doesNotExist())
      .andExpect(jsonPath("$[?(@.code=='APPORT')]").doesNotExist());
  }

  @Test
  void shouldReturnOnlyMouvementFinancierCategoriesForTypeMouvementFinancier() throws Exception {
    mockMvc
      .perform(get("/api/flux/referentials/categories").param("typeFlux", "MOUVEMENT_FINANCIER"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.code=='EMPRUNT_CAPITAL')]").exists())
      .andExpect(jsonPath("$[?(@.code=='APPORT')]").exists())
      .andExpect(jsonPath("$[?(@.code=='ELECTRICITE')]").doesNotExist());
  }

  @Test
  void shouldReturnAllCategoriesWhenTypeFluxMissing() throws Exception {
    mockMvc
      .perform(get("/api/flux/referentials/categories"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.code=='LOYER')]").exists())
      .andExpect(jsonPath("$[?(@.code=='ELECTRICITE')]").exists())
      .andExpect(jsonPath("$[?(@.code=='EMPRUNT_CAPITAL')]").exists())
      .andExpect(jsonPath("$[?(@.code=='REGULARISATION')]").exists());
  }

  @Test
  void shouldReturnAllCategoriesWhenTypeFluxInvalid() throws Exception {
    mockMvc
      .perform(get("/api/flux/referentials/categories").param("typeFlux", "PAS_UN_TYPE"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[?(@.code=='LOYER')]").exists())
      .andExpect(jsonPath("$[?(@.code=='ELECTRICITE')]").exists());
  }
}
