package com.nemia.core.mobilier.controller;

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
class MobilierReferentialControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(new MobilierReferentialController())
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldReturnMobilierReferentials() throws Exception {
    mockMvc.perform(get("/api/mobilier/referentials"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.categoriesMobilier").isArray())
      .andExpect(jsonPath("$.categoriesMobilier[?(@.code=='LITERIE')]").exists())
      .andExpect(jsonPath("$.categoriesMobilier[?(@.code=='ELECTROMENAGER')]").exists())
      .andExpect(jsonPath("$.categoriesMobilier[?(@.code=='MEUBLE')]").exists())
      .andExpect(jsonPath("$.categoriesMobilier[?(@.code=='EQUIPEMENT')]").exists())
      .andExpect(jsonPath("$.categoriesMobilier[?(@.code=='DECORATION')]").exists())
      .andExpect(jsonPath("$.categoriesMobilier[?(@.code=='AUTRE')]").exists())
      .andExpect(jsonPath("$.etatsUsage").isArray())
      .andExpect(jsonPath("$.etatsUsage[?(@.code=='NEUF')]").exists())
      .andExpect(jsonPath("$.etatsUsage[?(@.code=='OCCASION')]").exists())
      .andExpect(jsonPath("$.etatsUsage[?(@.code=='REMPLACEMENT')]").exists())
      .andExpect(jsonPath("$.etatsUsage[?(@.code=='INDETERMINE')]").exists())
      .andExpect(jsonPath("$.statutsMobilier").isArray())
      .andExpect(jsonPath("$.statutsMobilier[?(@.code=='BRUT')]").exists())
      .andExpect(jsonPath("$.statutsMobilier[?(@.code=='QUALIFIE')]").exists())
      .andExpect(jsonPath("$.statutsMobilier[?(@.code=='A_REVOIR')]").exists())
      .andExpect(jsonPath("$.statutsMobilier[?(@.code=='VALIDE')]").exists());
  }
}