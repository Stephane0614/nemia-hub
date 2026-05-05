package com.nemia.core.emprunt.controller;

import com.nemia.core.emprunt.dto.EmpruntReferentialsResponse;
import com.nemia.core.emprunt.model.StatutEmprunt;
import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmpruntReferentialController {

  @GetMapping("/api/emprunts/referentials")
  public EmpruntReferentialsResponse getReferentials() {
    List<ReferentialItemResponse> statuts = Arrays.stream(StatutEmprunt.values())
      .map(s -> new ReferentialItemResponse(s.name(), s.getLabel()))
      .toList();

    return new EmpruntReferentialsResponse(statuts);
  }
}
