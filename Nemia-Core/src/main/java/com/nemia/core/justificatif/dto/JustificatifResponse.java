package com.nemia.core.justificatif.dto;

import com.nemia.core.justificatif.model.StatutDocumentaire;
import com.nemia.core.justificatif.model.TypePiece;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JustificatifResponse {

  private Long id;
  private TypePiece typePiece;
  private StatutDocumentaire statutDocumentaire;
  private LocalDate datePiece;
  private String referencePiece;
  private String emetteur;
  private String commentaire;
  private String fichierAssocie;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String fichierNom;
  private String fichierType;
  private Long fichierTaille;

  public JustificatifResponse() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getFichierNom() {
    return fichierNom;
  }

  public void setFichierNom(String fichierNom) {
    this.fichierNom = fichierNom;
  }

  public String getFichierType() {
    return fichierType;
  }

  public void setFichierType(String fichierType) {
    this.fichierType = fichierType;
  }

  public Long getFichierTaille() {
    return fichierTaille;
  }

  public void setFichierTaille(Long fichierTaille) {
    this.fichierTaille = fichierTaille;
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
