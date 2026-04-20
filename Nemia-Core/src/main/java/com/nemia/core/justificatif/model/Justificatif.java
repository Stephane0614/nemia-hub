package com.nemia.core.justificatif.model;

import com.nemia.core.flux.persistence.LocalDateStringConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "justificatif")
public class Justificatif {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TypePiece typePiece;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatutDocumentaire statutDocumentaire;

  @Convert(converter = LocalDateStringConverter.class)
  private LocalDate datePiece;

  @Column(length = 100)
  private String referencePiece;

  @Column(length = 150)
  private String emetteur;

  @Column(length = 500)
  private String commentaire;

  @Column
  private String fichierAssocie;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public Justificatif() {}

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

  public TypePiece getTypePiece() {
    return typePiece;
  }

  public void setTypePiece(TypePiece typePiece) {
    this.typePiece = typePiece;
  }

  public StatutDocumentaire getStatutDocumentaire() {
    return statutDocumentaire;
  }

  public void setStatutDocumentaire(StatutDocumentaire statutDocumentaire) {
    this.statutDocumentaire = statutDocumentaire;
  }

  public LocalDate getDatePiece() {
    return datePiece;
  }

  public void setDatePiece(LocalDate datePiece) {
    this.datePiece = datePiece;
  }

  public String getReferencePiece() {
    return referencePiece;
  }

  public void setReferencePiece(String referencePiece) {
    this.referencePiece = referencePiece;
  }

  public String getEmetteur() {
    return emetteur;
  }

  public void setEmetteur(String emetteur) {
    this.emetteur = emetteur;
  }

  public String getCommentaire() {
    return commentaire;
  }

  public void setCommentaire(String commentaire) {
    this.commentaire = commentaire;
  }

  public String getFichierAssocie() {
    return fichierAssocie;
  }

  public void setFichierAssocie(String fichierAssocie) {
    this.fichierAssocie = fichierAssocie;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
