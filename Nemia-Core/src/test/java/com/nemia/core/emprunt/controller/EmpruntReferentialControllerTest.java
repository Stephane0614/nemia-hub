package com.nemia.core.emprunt.controller;

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
class EmpruntReferentialControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(new EmpruntReferentialController())
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldReturnEmpruntReferentials() throws Exception {
    mockMvc
      .perform(get("/api/emprunts/referentials"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.statutsEmprunt").isArray())
      .andExpect(jsonPath("$.statutsEmprunt[?(@.code=='EN_COURS')]").exists())
      .andExpect(jsonPath("$.statutsEmprunt[?(@.code=='TERMINE')]").exists())
      .andExpect(jsonPath("$.statutsEmprunt[?(@.code=='SUSPENDU')]").exists())
      .andExpect(jsonPath("$.statutsEmprunt[?(@.code=='A_VERIFIER')]").exists());
  }
}
