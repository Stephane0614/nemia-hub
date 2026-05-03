package com.nemia.core.mobilier.model;

import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.persistence.LocalDateStringConverter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mobilier")
public class Mobilier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "designation", nullable = false, length = 200)
    private String designation;

    @Column(name = "bien_id", nullable = false)
    private Long bienId;

    @Convert(converter = LocalDateStringConverter.class)
    @Column(name = "date_acquisition")
    private LocalDate dateAcquisition;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "quantite")
    private Integer quantite;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie_mobilier", nullable = false)
    private CategorieMobilier categorieMobilier;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_usage")
    private EtatUsage etatUsage;

    @Enumerated(EnumType.STRING)
    @Column(name = "qualification_pressentie", nullable = false)
    private QualificationPressentie qualificationPressentie;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_mobilier", nullable = false)
    private StatutMobilier statutMobilier;

    @Column(name = "justificatif_id")
    private Long justificatifId;

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

    public Long getId() { return id; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public Long getBienId() { return bienId; }
    public void setBienId(Long bienId) { this.bienId = bienId; }
    public LocalDate getDateAcquisition() { return dateAcquisition; }
    public void setDateAcquisition(LocalDate dateAcquisition) { this.dateAcquisition = dateAcquisition; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    public CategorieMobilier getCategorieMobilier() { return categorieMobilier; }
    public void setCategorieMobilier(CategorieMobilier categorieMobilier) { this.categorieMobilier = categorieMobilier; }
    public EtatUsage getEtatUsage() { return etatUsage; }
    public void setEtatUsage(EtatUsage etatUsage) { this.etatUsage = etatUsage; }
    public QualificationPressentie getQualificationPressentie() { return qualificationPressentie; }
    public void setQualificationPressentie(QualificationPressentie qualificationPressentie) { this.qualificationPressentie = qualificationPressentie; }
    public StatutMobilier getStatutMobilier() { return statutMobilier; }
    public void setStatutMobilier(StatutMobilier statutMobilier) { this.statutMobilier = statutMobilier; }
    public Long getJustificatifId() { return justificatifId; }
    public void setJustificatifId(Long justificatifId) { this.justificatifId = justificatifId; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}