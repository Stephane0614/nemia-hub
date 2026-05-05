package com.nemia.core.emprunt.dto;

import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import java.util.List;

public class EmpruntReferentialsResponse {

  private List<ReferentialItemResponse> statutsEmprunt;

  public EmpruntReferentialsResponse(List<ReferentialItemResponse> statutsEmprunt) {
    this.statutsEmprunt = statutsEmprunt;
  }

  public List<ReferentialItemResponse> getStatutsEmprunt() {
    return statutsEmprunt;
  }
}
