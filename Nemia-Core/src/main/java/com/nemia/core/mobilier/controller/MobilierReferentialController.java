package com.nemia.core.mobilier.controller;

import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import com.nemia.core.mobilier.dto.MobilierReferentialsResponse;
import com.nemia.core.mobilier.model.CategorieMobilier;
import com.nemia.core.mobilier.model.EtatUsage;
import com.nemia.core.mobilier.model.StatutMobilier;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MobilierReferentialController {

    @GetMapping("/api/mobilier/referentials")
    public MobilierReferentialsResponse getReferentials() {
        List<ReferentialItemResponse> categories = Arrays.stream(CategorieMobilier.values())
            .map(c -> new ReferentialItemResponse(c.name(), c.getLabel()))
            .toList();

        List<ReferentialItemResponse> etats = Arrays.stream(EtatUsage.values())
            .map(e -> new ReferentialItemResponse(e.name(), e.getLabel()))
            .toList();

        List<ReferentialItemResponse> statuts = Arrays.stream(StatutMobilier.values())
            .map(s -> new ReferentialItemResponse(s.name(), s.getLabel()))
            .toList();

        return new MobilierReferentialsResponse(categories, etats, statuts);
    }
}