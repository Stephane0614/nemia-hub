package com.nemia.core.travaux.model;

import com.nemia.core.flux.model.QualificationPressentie;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "travaux")
public class Travaux {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "libelle_travaux", nullable = false, length = 200)
  private String libelleTravaux;

  @Column(name = "bien_id")
  private Long bienId;

  @Column(name = "date_debut")
  private LocalDate dateDebut;

  @Column(name = "date_fin")
  private LocalDate dateFin;

  @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal montantTotal;

  @Enumerated(EnumType.STRING)
  @Column(name = "finalite_pressentie", nullable = false)
  private FinalitePressentie finalitePressentie;

  @Enumerated(EnumType.STRING)
  @Column(name = "qualification_pressentie", nullable = false)
  private QualificationPressentie qualificationPressentie;

  @Enumerated(EnumType.STRING)
  @Column(name = "statut_travaux")
  private StatutTravaux statutTravaux;

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

  public String getLibelleTravaux() {
    return libelleTravaux;
  }

  public void setLibelleTravaux(String libelleTravaux) {
    this.libelleTravaux = libelleTravaux;
  }

  public Long getBienId() {
    return bienId;
  }

  public void setBienId(Long bienId) {
    this.bienId = bienId;
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

  public BigDecimal getMontantTotal() {
    return montantTotal;
  }

  public void setMontantTotal(BigDecimal montantTotal) {
    this.montantTotal = montantTotal;
  }

  public FinalitePressentie getFinalitePressentie() {
    return finalitePressentie;
  }

  public void setFinalitePressentie(FinalitePressentie finalitePressentie) {
    this.finalitePressentie = finalitePressentie;
  }

  public QualificationPressentie getQualificationPressentie() {
    return qualificationPressentie;
  }

  public void setQualificationPressentie(QualificationPressentie qualificationPressentie) {
    this.qualificationPressentie = qualificationPressentie;
  }

  public StatutTravaux getStatutTravaux() {
    return statutTravaux;
  }

  public void setStatutTravaux(StatutTravaux statutTravaux) {
    this.statutTravaux = statutTravaux;
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
