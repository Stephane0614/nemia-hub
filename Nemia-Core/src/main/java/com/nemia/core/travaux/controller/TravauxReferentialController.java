package com.nemia.core.travaux.controller;

import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import com.nemia.core.travaux.dto.TravauxReferentialsResponse;
import com.nemia.core.travaux.model.FinalitePressentie;
import com.nemia.core.travaux.model.StatutTravaux;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TravauxReferentialController {

  @GetMapping("/api/travaux/referentials")
  public TravauxReferentialsResponse getReferentials() {
    List<ReferentialItemResponse> finalites = Arrays.stream(FinalitePressentie.values())
      .map(f -> new ReferentialItemResponse(f.name(), f.getLabel()))
      .toList();

    List<ReferentialItemResponse> statuts = Arrays.stream(StatutTravaux.values())
      .map(s -> new ReferentialItemResponse(s.name(), s.getLabel()))
      .toList();

    return new TravauxReferentialsResponse(finalites, statuts);
  }
}
