package com.nemia.core.bien.dto;

import com.nemia.core.bien.model.RegimeVise;
import com.nemia.core.bien.model.StatutActiviteBien;
import com.nemia.core.bien.model.TypeLocation;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BienResponse {

  private Long id;
  private String nomUsuel;
  private String adresseSimplifiee;
  private StatutActiviteBien statutActivite;
  private TypeLocation typeLocation;
  private LocalDate dateMiseEnLocation;
  private RegimeVise regimeVise;
  private String commentaire;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public BienResponse() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
