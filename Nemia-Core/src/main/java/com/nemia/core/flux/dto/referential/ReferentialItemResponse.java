package com.nemia.core.flux.dto.referential;

public class ReferentialItemResponse {

    private String code;
    private String label;

    public ReferentialItemResponse() {
    }

    public ReferentialItemResponse(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}