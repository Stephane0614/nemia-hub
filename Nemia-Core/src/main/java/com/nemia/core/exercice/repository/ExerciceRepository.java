package com.nemia.core.exercice.repository;

import com.nemia.core.exercice.model.Exercice;
import com.nemia.core.exercice.model.StatutExercice;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {
  boolean existsByLibelleExercice(String libelleExercice);

  boolean existsByLibelleExerciceAndIdNot(String libelleExercice, Long id);

  // Exercice OUVERT dont la période couvre la date donnée
  @Query(
    "SELECT e FROM Exercice e WHERE e.statutExercice = :statut " +
      "AND e.dateDebut <= :date AND e.dateFin >= :date " +
      "ORDER BY e.dateDebut DESC"
  )
  List<Exercice> findByStatutAndDateCovering(@Param("statut") StatutExercice statut, @Param("date") LocalDate date);

  // Dernier exercice OUVERT — fallback si aucun ne couvre la date du jour
  @Query("SELECT e FROM Exercice e WHERE e.statutExercice = :statut " + "ORDER BY e.dateFin DESC")
  List<Exercice> findByStatutOrderByDateFinDesc(@Param("statut") StatutExercice statut);
}
