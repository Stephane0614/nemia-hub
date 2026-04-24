package com.nemia.core.travaux.repository;

import com.nemia.core.travaux.model.Travaux;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravauxRepository extends JpaRepository<Travaux, Long> {
  List<Travaux> findByBienId(Long bienId);
}
