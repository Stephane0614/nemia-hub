package com.nemia.core.justificatif.controller;

import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import com.nemia.core.justificatif.dto.JustificatifReferentialsResponse;
import com.nemia.core.justificatif.model.StatutDocumentaire;
import com.nemia.core.justificatif.model.TypePiece;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JustificatifReferentialController {

  @GetMapping("/api/justificatifs/referentials")
  public JustificatifReferentialsResponse getJustificatifReferentials() {
    List<ReferentialItemResponse> typePieces = Arrays.stream(TypePiece.values())
      .map(t -> new ReferentialItemResponse(t.name(), t.getLabel()))
      .toList();

    List<ReferentialItemResponse> statutDocumentaires = Arrays.stream(StatutDocumentaire.values())
      .map(s -> new ReferentialItemResponse(s.name(), s.getLabel()))
      .toList();

    return new JustificatifReferentialsResponse(typePieces, statutDocumentaires);
  }
}
