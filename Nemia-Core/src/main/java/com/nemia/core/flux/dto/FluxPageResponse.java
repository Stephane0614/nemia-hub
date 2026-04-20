package com.nemia.core.flux.dto;

import java.util.List;

public class FluxPageResponse {

    private List<FluxResponse> contenu;
    private int page;
    private int taille;
    private long totalElements;
    private int totalPages;

    public FluxPageResponse(List<FluxResponse> contenu, int page, int taille, long totalElements, int totalPages) {
        this.contenu = contenu;
        this.page = page;
        this.taille = taille;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<FluxResponse> getContenu() { return contenu; }
    public int getPage() { return page; }
    public int getTaille() { return taille; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}