package com.nemia.core.flux.repository;

import com.nemia.core.flux.model.Flux;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FluxRepository extends JpaRepository<Flux, Long> {
  // ── Métriques ──

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'RECETTE' AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)"
  )
  BigDecimal sumRecettes(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'DEPENSE' AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)"
  )
  BigDecimal sumDepenses(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  @Query("SELECT COUNT(f) FROM Flux f WHERE f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)")
  long countOperations(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  // ── Alertes ──

  @Query(
    "SELECT COUNT(f) FROM Flux f WHERE f.statutJustificatif IN ('A_FOURNIR', 'INCOMPLET') AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)"
  )
  long countSansJustificatif(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.statutJustificatif IN ('A_FOURNIR', 'INCOMPLET') AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)"
  )
  BigDecimal sumMontantSansJustificatif(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  @Query(
    "SELECT COUNT(f) FROM Flux f WHERE f.qualificationPressentie = 'A_ARBITRER' AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)"
  )
  long countAArbitrer(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.qualificationPressentie = 'A_ARBITRER' AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)"
  )
  BigDecimal sumMontantAArbitrer(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  @Query(
    "SELECT COUNT(f) FROM Flux f WHERE f.statutTraitement = 'A_REVOIR' AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)"
  )
  long countARevoir(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  // ── Répartition dépenses ──

  @Query(
    "SELECT f.categorie, COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'DEPENSE' AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId) GROUP BY f.categorie ORDER BY SUM(f.montant) DESC"
  )
  List<Object[]> sumDepensesParCategorie(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  // ── Récurrence dépenses ──

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'DEPENSE' AND f.occurrence = 'RECURRENT' AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)"
  )
  BigDecimal sumDepensesRecurrentes(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  @Query(
    "SELECT COALESCE(SUM(f.montant), 0) FROM Flux f WHERE f.type = 'DEPENSE' AND f.occurrence = 'PONCTUEL' AND f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId)"
  )
  BigDecimal sumDepensesPonctuelles(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin, @Param("bienId") Long bienId);

  // ── Dernières opérations ──

  @Query("SELECT f FROM Flux f WHERE f.date >= :debut AND f.date <= :fin AND (:bienId IS NULL OR f.bienId = :bienId) ORDER BY f.date DESC")
  List<Flux> findDernieresOperations(
    @Param("debut") LocalDate debut,
    @Param("fin") LocalDate fin,
    @Param("bienId") Long bienId,
    Pageable pageable
  );

  // ── Liste filtrée + pagination ──

  @Query(
    """
    SELECT f FROM Flux f
    WHERE (:bienId IS NULL OR f.bienId = :bienId)
    AND (:exerciceId IS NULL OR f.exerciceId = :exerciceId)
    AND (:typeFlux IS NULL OR f.type = :typeFlux)
    AND (:categorie IS NULL OR f.categorie = :categorie)
    AND (:dateDebut IS NULL OR f.date >= :dateDebut)
    AND (:dateFin IS NULL OR f.date <= :dateFin)
    AND (:qualificationPressentie IS NULL OR f.qualificationPressentie = :qualificationPressentie)
    AND (:statutTraitement IS NULL OR f.statutTraitement = :statutTraitement)
    AND (:statutsJustificatif IS NULL OR f.statutJustificatif IN :statutsJustificatif)
    ORDER BY f.date DESC
    """
  )
  Page<Flux> findAllWithFilters(
    @Param("bienId") Long bienId,
    @Param("exerciceId") Long exerciceId,
    @Param("typeFlux") FluxType typeFlux,
    @Param("categorie") FluxCategory categorie,
    @Param("dateDebut") LocalDate dateDebut,
    @Param("dateFin") LocalDate dateFin,
    @Param("qualificationPressentie") QualificationPressentie qualificationPressentie,
    @Param("statutTraitement") StatutTraitement statutTraitement,
    @Param("statutsJustificatif") List<StatutJustificatif> statutsJustificatif,
    Pageable pageable
  );
}
