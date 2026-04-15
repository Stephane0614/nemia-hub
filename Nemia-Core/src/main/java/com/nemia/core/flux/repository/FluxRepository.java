package com.nemia.core.flux.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nemia.core.flux.model.Flux;

public interface FluxRepository extends JpaRepository<Flux, Long> {
}