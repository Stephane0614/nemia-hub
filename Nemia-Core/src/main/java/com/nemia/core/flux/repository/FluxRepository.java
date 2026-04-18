package com.nemia.core.flux.repository;

import com.nemia.core.flux.model.Flux;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FluxRepository extends JpaRepository<Flux, Long> {
  // ── Métriques ──

  @Query("SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'RECETTE' AND f.date >= :debut AND f.date <= :fin")
  BigDecimal sumRecettes(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  @Query("SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'DEPENSE' AND f.date >= :debut AND f.date <= :fin")
  BigDecimal sumDepenses(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  @Query("SELECT COUNT(f) FROM Flux f WHERE f.date >= :debut AND f.date <= :fin")
  long countOperations(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  // ── Alertes ──

  @Query("SELECT COUNT(f) FROM Flux f WHERE f.statutJustificatif IN ('A_FOURNIR', 'INCOMPLET') AND f.date >= :debut AND f.date <= :fin")
  long countSansJustificatif(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.statutJustificatif IN ('A_FOURNIR', 'INCOMPLET') AND f.date >= :debut AND f.date <= :fin"
  )
  BigDecimal sumMontantSansJustificatif(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  @Query("SELECT COUNT(f) FROM Flux f WHERE f.qualificationPressentie = 'A_ARBITRER' AND f.date >= :debut AND f.date <= :fin")
  long countAArbitrer(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.qualificationPressentie = 'A_ARBITRER' AND f.date >= :debut AND f.date <= :fin"
  )
  BigDecimal sumMontantAArbitrer(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  @Query("SELECT COUNT(f) FROM Flux f WHERE f.statutTraitement = 'A_REVOIR' AND f.date >= :debut AND f.date <= :fin")
  long countARevoir(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  // ── Répartition dépenses ──

  @Query(
    "SELECT f.categorie, COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'DEPENSE' AND f.date >= :debut AND f.date <= :fin GROUP BY f.categorie ORDER BY SUM(f.montant) DESC"
  )
  List<Object[]> sumDepensesParCategorie(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  // ── Récurrence dépenses ──

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'DEPENSE' AND f.occurrence = 'RECURRENT' AND f.date >= :debut AND f.date <= :fin"
  )
  BigDecimal sumDepensesRecurrentes(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'DEPENSE' AND f.occurrence = 'PONCTUEL' AND f.date >= :debut AND f.date <= :fin"
  )
  BigDecimal sumDepensesPonctuelles(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  // ── Dernières opérations ──

  @Query("SELECT f FROM Flux f WHERE f.date >= :debut AND f.date <= :fin ORDER BY f.date DESC")
  List<Flux> findDernieresOperations(
    @Param("debut") LocalDate debut,
    @Param("fin") LocalDate fin,
    org.springframework.data.domain.Pageable pageable
  );
}
