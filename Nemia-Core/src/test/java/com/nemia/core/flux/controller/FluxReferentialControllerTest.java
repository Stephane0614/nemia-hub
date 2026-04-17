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
}
