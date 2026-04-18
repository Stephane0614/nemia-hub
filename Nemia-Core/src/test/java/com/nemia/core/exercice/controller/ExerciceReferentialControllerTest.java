package com.nemia.core.exercice.controller;

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
class ExerciceReferentialControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders
      .standaloneSetup(new ExerciceReferentialController())
      .setControllerAdvice(new com.nemia.core.common.exception.GlobalExceptionHandler())
      .build();
  }

  @Test
  void shouldReturnExerciceReferentials() throws Exception {
    mockMvc
      .perform(get("/api/exercices/referentials"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.statutExercices").isArray())
      .andExpect(jsonPath("$.statutExercices[0].code").exists())
      .andExpect(jsonPath("$.statutExercices[0].label").exists())
      .andExpect(jsonPath("$.statutExercices[?(@.code=='OUVERT')]").exists())
      .andExpect(jsonPath("$.statutExercices[?(@.code=='EN_PREPARATION_DE_CLOTURE')]").exists())
      .andExpect(jsonPath("$.statutExercices[?(@.code=='CLOTURE')]").exists())
      .andExpect(jsonPath("$.niveauxCompletude").isArray())
      .andExpect(jsonPath("$.niveauxCompletude[?(@.code=='FAIBLE')]").exists())
      .andExpect(jsonPath("$.niveauxCompletude[?(@.code=='MOYEN')]").exists())
      .andExpect(jsonPath("$.niveauxCompletude[?(@.code=='AVANCE')]").exists())
      .andExpect(jsonPath("$.niveauxCompletude[?(@.code=='COMPLET')]").exists());
  }
}