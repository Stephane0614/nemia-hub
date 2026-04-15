package com.nemia.core.flux.controller;

import com.nemia.core.flux.dto.referential.FluxReferentialsResponse;
import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.PaymentMode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

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

        return new FluxReferentialsResponse(types, categories, paymentModes);
    }
}