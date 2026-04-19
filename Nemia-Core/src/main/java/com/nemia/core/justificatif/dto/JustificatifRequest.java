package com.nemia.core.justificatif.dto;

import com.nemia.core.justificatif.model.StatutDocumentaire;
import com.nemia.core.justificatif.model.TypePiece;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class JustificatifRequest {

  @NotNull
  private TypePiece typePiece;

  @NotNull
  private StatutDocumentaire statutDocumentaire;

  private LocalDate datePiece;

  @Size(max = 100)
  private String referencePiece;

  @Size(max = 150)
  private String emetteur;

  @Size(max = 500)
  private String commentaire;

  private String fichierAssocie;

  public JustificatifRequest() {}

  public TypePiece getTypePiece() { return typePiece; }
  public void setTypePiece(TypePiece typePiece) { this.typePiece = typePiece; }

  public StatutDocumentaire getStatutDocumentaire() { return statutDocumentaire; }
  public void setStatutDocumentaire(StatutDocumentaire statutDocumentaire) { this.statutDocumentaire = statutDocumentaire; }

  public LocalDate getDatePiece() { return datePiece; }
  public void setDatePiece(LocalDate datePiece) { this.datePiece = datePiece; }

  public String getReferencePiece() { return referencePiece; }
  public void setReferencePiece(String referencePiece) { this.referencePiece = referencePiece; }

  public String getEmetteur() { return emetteur; }
  public void setEmetteur(String emetteur) { this.emetteur = emetteur; }

  public String getCommentaire() { return commentaire; }
  public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

  public String getFichierAssocie() { return fichierAssocie; }
  public void setFichierAssocie(String fichierAssocie) { this.fichierAssocie = fichierAssocie; }
}