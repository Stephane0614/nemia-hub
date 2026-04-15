package com.nemia.core.flux.dto.referential;

import java.util.List;

public class FluxReferentialsResponse {

    private List<ReferentialItemResponse> types;
    private List<ReferentialItemResponse> categories;
    private List<ReferentialItemResponse> paymentModes;

    public FluxReferentialsResponse() {
    }

    public FluxReferentialsResponse(
            List<ReferentialItemResponse> types,
            List<ReferentialItemResponse> categories,
            List<ReferentialItemResponse> paymentModes
    ) {
        this.types = types;
        this.categories = categories;
        this.paymentModes = paymentModes;
    }

    public List<ReferentialItemResponse> getTypes() {
        return types;
    }

    public void setTypes(List<ReferentialItemResponse> types) {
        this.types = types;
    }

    public List<ReferentialItemResponse> getCategories() {
        return categories;
    }

    public void setCategories(List<ReferentialItemResponse> categories) {
        this.categories = categories;
    }

    public List<ReferentialItemResponse> getPaymentModes() {
        return paymentModes;
    }

    public void setPaymentModes(List<ReferentialItemResponse> paymentModes) {
        this.paymentModes = paymentModes;
    }
}
