package com.nemia.core.exercice.dto;

import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import java.util.List;

public class ExerciceReferentialsResponse {

  private List<ReferentialItemResponse> statutExercices;
  private List<ReferentialItemResponse> niveauxCompletude;

  public ExerciceReferentialsResponse() {}

  public ExerciceReferentialsResponse(
    List<ReferentialItemResponse> statutExercices,
    List<ReferentialItemResponse> niveauxCompletude
  ) {
    this.statutExercices = statutExercices;
    this.niveauxCompletude = niveauxCompletude;
  }

  public List<ReferentialItemResponse> getStatutExercices() { return statutExercices; }
  public void setStatutExercices(List<ReferentialItemResponse> statutExercices) { this.statutExercices = statutExercices; }

  public List<ReferentialItemResponse> getNiveauxCompletude() { return niveauxCompletude; }
  public void setNiveauxCompletude(List<ReferentialItemResponse> niveauxCompletude) { this.niveauxCompletude = niveauxCompletude; }
}