package com.nemia.core.exercice.dto;

import com.nemia.core.exercice.model.NiveauCompletude;
import com.nemia.core.exercice.model.StatutExercice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ExerciceRequest {

  @NotBlank
  @Size(max = 50)
  private String libelleExercice;

  @NotNull
  private LocalDate dateDebut;

  @NotNull
  private LocalDate dateFin;

  @NotNull
  private StatutExercice statutExercice;

  private NiveauCompletude niveauCompletude;

  @Size(max = 500)
  private String commentaire;

  public ExerciceRequest() {}

  public String getLibelleExercice() { return libelleExercice; }
  public void setLibelleExercice(String libelleExercice) { this.libelleExercice = libelleExercice; }

  public LocalDate getDateDebut() { return dateDebut; }
  public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

  public LocalDate getDateFin() { return dateFin; }
  public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

  public StatutExercice getStatutExercice() { return statutExercice; }
  public void setStatutExercice(StatutExercice statutExercice) { this.statutExercice = statutExercice; }

  public NiveauCompletude getNiveauCompletude() { return niveauCompletude; }
  public void setNiveauCompletude(NiveauCompletude niveauCompletude) { this.niveauCompletude = niveauCompletude; }

  public String getCommentaire() { return commentaire; }
  public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}