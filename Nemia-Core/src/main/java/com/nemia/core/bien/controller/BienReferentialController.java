package com.nemia.core.bien.controller;

import com.nemia.core.bien.dto.BienReferentialsResponse;
import com.nemia.core.bien.model.RegimeVise;
import com.nemia.core.bien.model.StatutActiviteBien;
import com.nemia.core.bien.model.TypeLocation;
import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BienReferentialController {

  @GetMapping("/api/biens/referentials")
  public BienReferentialsResponse getBienReferentials() {
    List<ReferentialItemResponse> statutActivites = Arrays.stream(StatutActiviteBien.values())
      .map(s -> new ReferentialItemResponse(s.name(), s.getLabel()))
      .toList();

    List<ReferentialItemResponse> typeLocations = Arrays.stream(TypeLocation.values())
      .map(t -> new ReferentialItemResponse(t.name(), t.getLabel()))
      .toList();

    List<ReferentialItemResponse> regimeVises = Arrays.stream(RegimeVise.values())
      .map(r -> new ReferentialItemResponse(r.name(), r.getLabel()))
      .toList();

    return new BienReferentialsResponse(statutActivites, typeLocations, regimeVises);
  }
}
