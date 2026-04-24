package com.nemia.core.bien.repository;

import com.nemia.core.bien.model.Bien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BienRepository extends JpaRepository<Bien, Long> {
  boolean existsByNomUsuelAndAdresseSimplifiee(String nomUsuel, String adresseSimplifiee);

  boolean existsByNomUsuelAndAdresseSimplifieeAndIdNot(String nomUsuel, String adresseSimplifiee, Long id);
}
