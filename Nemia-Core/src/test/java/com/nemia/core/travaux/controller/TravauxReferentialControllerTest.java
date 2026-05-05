package com.nemia.core.travaux.controller;

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
class TravauxReferentialControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(new TravauxReferentialController())
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldReturnTravauxReferentials() throws Exception {
    mockMvc
      .perform(get("/api/travaux/referentials"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.finalitesPressenties").isArray())
      .andExpect(jsonPath("$.finalitesPressenties[?(@.code=='ENTRETIEN_COURANT')]").exists())
      .andExpect(jsonPath("$.finalitesPressenties[?(@.code=='REPARATION')]").exists())
      .andExpect(jsonPath("$.finalitesPressenties[?(@.code=='AMELIORATION')]").exists())
      .andExpect(jsonPath("$.finalitesPressenties[?(@.code=='CREATION')]").exists())
      .andExpect(jsonPath("$.finalitesPressenties[?(@.code=='REMISE_EN_ETAT')]").exists())
      .andExpect(jsonPath("$.finalitesPressenties[?(@.code=='A_ARBITRER')]").exists())
      .andExpect(jsonPath("$.statutsTravaux").isArray())
      .andExpect(jsonPath("$.statutsTravaux[?(@.code=='BRUT')]").exists())
      .andExpect(jsonPath("$.statutsTravaux[?(@.code=='QUALIFIE')]").exists())
      .andExpect(jsonPath("$.statutsTravaux[?(@.code=='A_REVOIR')]").exists())
      .andExpect(jsonPath("$.statutsTravaux[?(@.code=='VALIDE')]").exists());
  }
}
