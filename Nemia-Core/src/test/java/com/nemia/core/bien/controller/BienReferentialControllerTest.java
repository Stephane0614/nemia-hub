package com.nemia.core.bien.controller;

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
class BienReferentialControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(new BienReferentialController())
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldReturnBienReferentials() throws Exception {
    mockMvc
      .perform(get("/api/biens/referentials"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.statutActivites").isArray())
      .andExpect(jsonPath("$.statutActivites[0].code").exists())
      .andExpect(jsonPath("$.statutActivites[0].label").exists())
      .andExpect(jsonPath("$.statutActivites[?(@.code=='EN_PREPARATION')]").exists())
      .andExpect(jsonPath("$.statutActivites[?(@.code=='ACTIF')]").exists())
      .andExpect(jsonPath("$.statutActivites[?(@.code=='SUSPENDU')]").exists())
      .andExpect(jsonPath("$.statutActivites[?(@.code=='CLOTURE')]").exists())
      .andExpect(jsonPath("$.typeLocations").isArray())
      .andExpect(jsonPath("$.typeLocations[?(@.code=='LMNP_LONGUE_DUREE')]").exists())
      .andExpect(jsonPath("$.typeLocations[?(@.code=='LMNP_COURTE_DUREE')]").exists())
      .andExpect(jsonPath("$.typeLocations[?(@.code=='MIXTE')]").exists())
      .andExpect(jsonPath("$.typeLocations[?(@.code=='AUTRE')]").exists())
      .andExpect(jsonPath("$.regimeVises").isArray())
      .andExpect(jsonPath("$.regimeVises[?(@.code=='MICRO_BIC')]").exists())
      .andExpect(jsonPath("$.regimeVises[?(@.code=='REEL')]").exists())
      .andExpect(jsonPath("$.regimeVises[?(@.code=='A_DEFINIR')]").exists());
  }
}
