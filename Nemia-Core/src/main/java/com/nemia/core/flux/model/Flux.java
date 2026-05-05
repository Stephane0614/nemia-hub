package com.nemia.core.flux.model;

import com.nemia.core.flux.persistence.LocalDateStringConverter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "flux")
public class Flux {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Convert(converter = LocalDateStringConverter.class)
  @Column(nullable = false)
  private LocalDate date;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FluxType type;

  @Column(nullable = false, length = 255)
  private String libelle;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal montant;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FluxCategory categorie;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentMode modePaiement;

  @Column(length = 1000)
  private String commentaire;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  private Long bienId;

  private Long exerciceId;

  @Convert(converter = LocalDateStringConverter.class)
  private LocalDate dateValeur;

  @Enumerated(EnumType.STRING)
  private Occurrence occurrence;

  @Enumerated(EnumType.STRING)
  private StatutJustificatif statutJustificatif;

  @Enumerated(EnumType.STRING)
  private QualificationPressentie qualificationPressentie;

  @Enumerated(EnumType.STRING)
  private StatutTraitement statutTraitement;

  private Long justificatifId;

  private Long travauxId;

  private Long mobilierId;

  private Long empruntId;

  public Flux() {}

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

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public FluxType getType() {
    return type;
  }

  public void setType(FluxType type) {
    this.type = type;
  }

  public String getLibelle() {
    return libelle;
  }

  public void setLibelle(String libelle) {
    this.libelle = libelle;
  }

  public BigDecimal getMontant() {
    return montant;
  }

  public void setMontant(BigDecimal montant) {
    this.montant = montant;
  }

  public FluxCategory getCategorie() {
    return categorie;
  }

  public void setCategorie(FluxCategory categorie) {
    this.categorie = categorie;
  }

  public PaymentMode getModePaiement() {
    return modePaiement;
  }

  public void setModePaiement(PaymentMode modePaiement) {
    this.modePaiement = modePaiement;
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

  public Long getBienId() {
    return bienId;
  }

  public void setBienId(Long bienId) {
    this.bienId = bienId;
  }

  public Long getExerciceId() {
    return exerciceId;
  }

  public void setExerciceId(Long exerciceId) {
    this.exerciceId = exerciceId;
  }

  public LocalDate getDateValeur() {
    return dateValeur;
  }

  public void setDateValeur(LocalDate dateValeur) {
    this.dateValeur = dateValeur;
  }

  public Occurrence getOccurrence() {
    return occurrence;
  }

  public void setOccurrence(Occurrence occurrence) {
    this.occurrence = occurrence;
  }

  public StatutJustificatif getStatutJustificatif() {
    return statutJustificatif;
  }

  public void setStatutJustificatif(StatutJustificatif statutJustificatif) {
    this.statutJustificatif = statutJustificatif;
  }

  public QualificationPressentie getQualificationPressentie() {
    return qualificationPressentie;
  }

  public void setQualificationPressentie(QualificationPressentie qualificationPressentie) {
    this.qualificationPressentie = qualificationPressentie;
  }

  public StatutTraitement getStatutTraitement() {
    return statutTraitement;
  }

  public void setStatutTraitement(StatutTraitement statutTraitement) {
    this.statutTraitement = statutTraitement;
  }

  public Long getJustificatifId() {
    return justificatifId;
  }

  public void setJustificatifId(Long justificatifId) {
    this.justificatifId = justificatifId;
  }

  public Long getTravauxId() {
    return travauxId;
  }

  public void setTravauxId(Long travauxId) {
    this.travauxId = travauxId;
  }

  public Long getMobilierId() {
    return mobilierId;
  }

  public void setMobilierId(Long mobilierId) {
    this.mobilierId = mobilierId;
  }

  public Long getEmpruntId() {
    return empruntId;
  }

  public void setEmpruntId(Long empruntId) {
    this.empruntId = empruntId;
  }
}
