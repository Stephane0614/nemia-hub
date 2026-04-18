package com.nemia.core.exercice.controller;

import com.nemia.core.exercice.dto.ExerciceReferentialsResponse;
import com.nemia.core.exercice.model.NiveauCompletude;
import com.nemia.core.exercice.model.StatutExercice;
import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExerciceReferentialController {

  @GetMapping("/api/exercices/referentials")
  public ExerciceReferentialsResponse getExerciceReferentials() {

    List<ReferentialItemResponse> statutExercices = Arrays.stream(StatutExercice.values())
      .map(s -> new ReferentialItemResponse(s.name(), s.getLabel()))
      .toList();

    List<ReferentialItemResponse> niveauxCompletude = Arrays.stream(NiveauCompletude.values())
      .map(n -> new ReferentialItemResponse(n.name(), n.getLabel()))
      .toList();

    return new ExerciceReferentialsResponse(statutExercices, niveauxCompletude);
  }
}