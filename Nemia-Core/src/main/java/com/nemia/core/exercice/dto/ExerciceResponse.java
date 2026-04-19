package com.nemia.core.exercice.dto;

import com.nemia.core.exercice.model.NiveauCompletude;
import com.nemia.core.exercice.model.StatutExercice;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExerciceResponse {

  private Long id;
  private String libelleExercice;
  private LocalDate dateDebut;
  private LocalDate dateFin;
  private StatutExercice statutExercice;
  private NiveauCompletude niveauCompletude;
  private String commentaire;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<String> warnings = new ArrayList<>();

  public ExerciceResponse() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLibelleExercice() {
    return libelleExercice;
  }

  public void setLibelleExercice(String libelleExercice) {
    this.libelleExercice = libelleExercice;
  }

  public LocalDate getDateDebut() {
    return dateDebut;
  }

  public void setDateDebut(LocalDate dateDebut) {
    this.dateDebut = dateDebut;
  }

  public LocalDate getDateFin() {
    return dateFin;
  }

  public void setDateFin(LocalDate dateFin) {
    this.dateFin = dateFin;
  }

  public StatutExercice getStatutExercice() {
    return statutExercice;
  }

  public void setStatutExercice(StatutExercice statutExercice) {
    this.statutExercice = statutExercice;
  }

  public NiveauCompletude getNiveauCompletude() {
    return niveauCompletude;
  }

  public void setNiveauCompletude(NiveauCompletude niveauCompletude) {
    this.niveauCompletude = niveauCompletude;
  }

  public String getCommentaire() {
    return commentaire;
  }

  public void setCommentaire(String commentaire) {
    this.commentaire = commentaire;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<String> getWarnings() {
    return warnings;
  }

  public void setWarnings(List<String> warnings) {
    this.warnings = warnings;
  }
}
