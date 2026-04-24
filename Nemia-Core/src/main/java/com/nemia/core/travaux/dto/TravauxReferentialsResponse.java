package com.nemia.core.travaux.dto;

import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import java.util.List;

public class TravauxReferentialsResponse {

  private List<ReferentialItemResponse> finalitesPressenties;
  private List<ReferentialItemResponse> statutsTravaux;

  public TravauxReferentialsResponse(List<ReferentialItemResponse> finalitesPressenties, List<ReferentialItemResponse> statutsTravaux) {
    this.finalitesPressenties = finalitesPressenties;
    this.statutsTravaux = statutsTravaux;
  }

  public List<ReferentialItemResponse> getFinalitesPressenties() {
    return finalitesPressenties;
  }

  public List<ReferentialItemResponse> getStatutsTravaux() {
    return statutsTravaux;
  }
}
