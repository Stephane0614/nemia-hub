package com.nemia.core.emprunt.repository;

import com.nemia.core.emprunt.model.Emprunt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
  List<Emprunt> findByBienId(Long bienId);
}
