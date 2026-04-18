package com.nemia.core.flux.controller;

import com.nemia.core.flux.dto.referential.FluxReferentialsResponse;
import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.Occurrence;
import com.nemia.core.flux.model.PaymentMode;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FluxReferentialController {

  @GetMapping("/api/flux/referentials")
  public FluxReferentialsResponse getFluxReferentials() {
    List<ReferentialItemResponse> types = Arrays.stream(FluxType.values())
      .map(type -> new ReferentialItemResponse(type.name(), type.getLabel()))
      .toList();

    List<ReferentialItemResponse> categories = Arrays.stream(FluxCategory.values())
      .map(category -> new ReferentialItemResponse(category.name(), category.getLabel()))
      .toList();

    List<ReferentialItemResponse> paymentModes = Arrays.stream(PaymentMode.values())
      .map(paymentMode -> new ReferentialItemResponse(paymentMode.name(), paymentMode.getLabel()))
      .toList();

    List<ReferentialItemResponse> occurrences = Arrays.stream(Occurrence.values())
      .map(occurrence -> new ReferentialItemResponse(occurrence.name(), occurrence.getLabel()))
      .toList();

    List<ReferentialItemResponse> statutJustificatifs = Arrays.stream(StatutJustificatif.values())
      .map(statut -> new ReferentialItemResponse(statut.name(), statut.getLabel()))
      .toList();

    List<ReferentialItemResponse> qualificationPressenties = Arrays.stream(QualificationPressentie.values())
      .map(qualification -> new ReferentialItemResponse(qualification.name(), qualification.getLabel()))
      .toList();

    List<ReferentialItemResponse> statutTraitements = Arrays.stream(StatutTraitement.values())
      .map(statut -> new ReferentialItemResponse(statut.name(), statut.getLabel()))
      .toList();

    return new FluxReferentialsResponse(
      types,
      categories,
      paymentModes,
      occurrences,
      statutJustificatifs,
      qualificationPressenties,
      statutTraitements
    );
  }
}
