package com.nemia.core.bien.repository;

import com.nemia.core.bien.model.Bien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BienRepository extends JpaRepository<Bien, Long> {}