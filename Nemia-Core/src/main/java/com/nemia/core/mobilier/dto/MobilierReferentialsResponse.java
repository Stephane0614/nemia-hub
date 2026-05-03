package com.nemia.core.mobilier.dto;

import com.nemia.core.flux.dto.referential.ReferentialItemResponse;
import java.util.List;

public class MobilierReferentialsResponse {

    private List<ReferentialItemResponse> categoriesMobilier;
    private List<ReferentialItemResponse> etatsUsage;
    private List<ReferentialItemResponse> statutsMobilier;

    public MobilierReferentialsResponse(
            List<ReferentialItemResponse> categoriesMobilier,
            List<ReferentialItemResponse> etatsUsage,
            List<ReferentialItemResponse> statutsMobilier) {
        this.categoriesMobilier = categoriesMobilier;
        this.etatsUsage = etatsUsage;
        this.statutsMobilier = statutsMobilier;
    }

    public List<ReferentialItemResponse> getCategoriesMobilier() { return categoriesMobilier; }
    public List<ReferentialItemResponse> getEtatsUsage() { return etatsUsage; }
    public List<ReferentialItemResponse> getStatutsMobilier() { return statutsMobilier; }
}