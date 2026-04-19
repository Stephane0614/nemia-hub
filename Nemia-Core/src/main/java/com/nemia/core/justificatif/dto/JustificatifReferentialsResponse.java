package com.nemia.core.justificatif.dto;

import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import java.util.List;

public class JustificatifReferentialsResponse {

  private List<ReferentialItemResponse> typePieces;
  private List<ReferentialItemResponse> statutDocumentaires;

  public JustificatifReferentialsResponse() {}

  public JustificatifReferentialsResponse(
    List<ReferentialItemResponse> typePieces,
    List<ReferentialItemResponse> statutDocumentaires
  ) {
    this.typePieces = typePieces;
    this.statutDocumentaires = statutDocumentaires;
  }

  public List<ReferentialItemResponse> getTypePieces() { return typePieces; }
  public void setTypePieces(List<ReferentialItemResponse> typePieces) { this.typePieces = typePieces; }

  public List<ReferentialItemResponse> getStatutDocumentaires() { return statutDocumentaires; }
  public void setStatutDocumentaires(List<ReferentialItemResponse> statutDocumentaires) { this.statutDocumentaires = statutDocumentaires; }
}