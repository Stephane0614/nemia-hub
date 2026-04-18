package com.nemia.core.exercice.repository;

import com.nemia.core.exercice.model.Exercice;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {
  Optional<Exercice> findByLibelleExercice(String libelleExercice);
}
