package com.nemia.core.exercice.repository;

import com.nemia.core.exercice.model.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {
  boolean existsByLibelleExercice(String libelleExercice);

  boolean existsByLibelleExerciceAndIdNot(String libelleExercice, Long id);
}
