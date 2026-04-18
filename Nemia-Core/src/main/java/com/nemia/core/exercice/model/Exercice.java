package com.nemia.core.exercice.model;

import com.nemia.core.flux.persistence.LocalDateStringConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
  name = "exercice",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"libelle_exercice"})
  }
)
public class Exercice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "libelle_exercice", nullable = false, length = 50)
  private String libelleExercice;

  @Convert(converter = LocalDateStringConverter.class)
  @Column(nullable = false)
  private LocalDate dateDebut;

  @Convert(converter = LocalDateStringConverter.class)
  @Column(nullable = false)
  private LocalDate dateFin;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatutExercice statutExercice;

  @Enumerated(EnumType.STRING)
  private NiveauCompletude niveauCompletude;

  @Column(length = 500)
  private String commentaire;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public Exercice() {}

  @PrePersist
  public void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public Long getId() { return id; }

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

  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
}