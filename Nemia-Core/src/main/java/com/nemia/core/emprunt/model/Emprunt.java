package com.nemia.core.emprunt.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "emprunt")
public class Emprunt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "reference_pret", nullable = false, length = 100)
  private String referencePret;

  @Column(name = "bien_id", nullable = false)
  private Long bienId;

  @Column(name = "organisme_preteur", length = 150)
  private String organismePreteur;

  @Column(name = "mensualite_totale", precision = 12, scale = 2)
  private BigDecimal mensualiteTotale;

  @Column(name = "date_premiere_echeance")
  private LocalDate datePremiereEcheance;

  @Column(name = "date_derniere_echeance")
  private LocalDate dateDerniereEcheance;

  @Enumerated(EnumType.STRING)
  @Column(name = "statut_emprunt", nullable = false)
  private StatutEmprunt statutEmprunt;

  @Column(name = "commentaire", length = 500)
  private String commentaire;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  // Getters & Setters
  public Long getId() {
    return id;
  }

  public String getReferencePret() {
    return referencePret;
  }

  public void setReferencePret(String referencePret) {
    this.referencePret = referencePret;
  }

  public Long getBienId() {
    return bienId;
  }

  public void setBienId(Long bienId) {
    this.bienId = bienId;
  }

  public String getOrganismePreteur() {
    return organismePreteur;
  }

  public void setOrganismePreteur(String organismePreteur) {
    this.organismePreteur = organismePreteur;
  }

  public BigDecimal getMensualiteTotale() {
    return mensualiteTotale;
  }

  public void setMensualiteTotale(BigDecimal mensualiteTotale) {
    this.mensualiteTotale = mensualiteTotale;
  }

  public LocalDate getDatePremiereEcheance() {
    return datePremiereEcheance;
  }

  public void setDatePremiereEcheance(LocalDate datePremiereEcheance) {
    this.datePremiereEcheance = datePremiereEcheance;
  }

  public LocalDate getDateDerniereEcheance() {
    return dateDerniereEcheance;
  }

  public void setDateDerniereEcheance(LocalDate dateDerniereEcheance) {
    this.dateDerniereEcheance = dateDerniereEcheance;
  }

  public StatutEmprunt getStatutEmprunt() {
    return statutEmprunt;
  }

  public void setStatutEmprunt(StatutEmprunt statutEmprunt) {
    this.statutEmprunt = statutEmprunt;
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

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
