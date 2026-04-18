package com.nemia.core.bien.dto;

import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import java.util.List;

public class BienReferentialsResponse {

  private List<ReferentialItemResponse> statutActivites;
  private List<ReferentialItemResponse> typeLocations;
  private List<ReferentialItemResponse> regimeVises;

  public BienReferentialsResponse() {}

  public BienReferentialsResponse(
    List<ReferentialItemResponse> statutActivites,
    List<ReferentialItemResponse> typeLocations,
    List<ReferentialItemResponse> regimeVises
  ) {
    this.statutActivites = statutActivites;
    this.typeLocations = typeLocations;
    this.regimeVises = regimeVises;
  }

  public List<ReferentialItemResponse> getStatutActivites() { return statutActivites; }
  public void setStatutActivites(List<ReferentialItemResponse> statutActivites) { this.statutActivites = statutActivites; }

  public List<ReferentialItemResponse> getTypeLocations() { return typeLocations; }
  public void setTypeLocations(List<ReferentialItemResponse> typeLocations) { this.typeLocations = typeLocations; }

  public List<ReferentialItemResponse> getRegimeVises() { return regimeVises; }
  public void setRegimeVises(List<ReferentialItemResponse> regimeVises) { this.regimeVises = regimeVises; }
}