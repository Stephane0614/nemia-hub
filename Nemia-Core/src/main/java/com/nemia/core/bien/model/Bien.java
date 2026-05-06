package com.nemia.core.bien.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bien", uniqueConstraints = { @UniqueConstraint(columnNames = { "nom_usuel", "adresse_simplifiee" }) })
public class Bien {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String nomUsuel;

  @Column(nullable = false, length = 200)
  private String adresseSimplifiee;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatutActiviteBien statutActivite;

  @Enumerated(EnumType.STRING)
  private TypeLocation typeLocation;

  private LocalDate dateMiseEnLocation;

  @Enumerated(EnumType.STRING)
  private RegimeVise regimeVise;

  @Column(length = 500)
  private String commentaire;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public Bien() {}

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

  public Long getId() {
    return id;
  }

  public String getNomUsuel() {
    return nomUsuel;
  }

  public void setNomUsuel(String nomUsuel) {
    this.nomUsuel = nomUsuel;
  }

  public String getAdresseSimplifiee() {
    return adresseSimplifiee;
  }

  public void setAdresseSimplifiee(String adresseSimplifiee) {
    this.adresseSimplifiee = adresseSimplifiee;
  }

  public StatutActiviteBien getStatutActivite() {
    return statutActivite;
  }

  public void setStatutActivite(StatutActiviteBien statutActivite) {
    this.statutActivite = statutActivite;
  }

  public TypeLocation getTypeLocation() {
    return typeLocation;
  }

  public void setTypeLocation(TypeLocation typeLocation) {
    this.typeLocation = typeLocation;
  }

  public LocalDate getDateMiseEnLocation() {
    return dateMiseEnLocation;
  }

  public void setDateMiseEnLocation(LocalDate dateMiseEnLocation) {
    this.dateMiseEnLocation = dateMiseEnLocation;
  }

  public RegimeVise getRegimeVise() {
    return regimeVise;
  }

  public void setRegimeVise(RegimeVise regimeVise) {
    this.regimeVise = regimeVise;
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
