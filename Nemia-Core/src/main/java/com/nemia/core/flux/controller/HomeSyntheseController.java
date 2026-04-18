package com.nemia.core.flux.controller;

import com.nemia.core.flux.dto.HomeSyntheseResponse;
import com.nemia.core.flux.service.HomeSyntheseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeSyntheseController {

    private final HomeSyntheseService homeSyntheseService;

    public HomeSyntheseController(HomeSyntheseService homeSyntheseService) {
        this.homeSyntheseService = homeSyntheseService;
    }

    @GetMapping("/synthese")
    public ResponseEntity<HomeSyntheseResponse> getSynthese(
            @RequestParam(required = false) String mois,
            @RequestParam(required = false) Long bienId) {

        try {
            HomeSyntheseResponse response = homeSyntheseService.getSynthese(mois, bienId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}