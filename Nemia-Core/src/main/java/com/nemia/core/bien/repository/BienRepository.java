package com.nemia.core.bien.repository;

import com.nemia.core.bien.model.Bien;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BienRepository extends JpaRepository<Bien, Long> {

  Optional<Bien> findByNomUsuelAndAdresseSimplifiee(String nomUsuel, String adresseSimplifiee);
}