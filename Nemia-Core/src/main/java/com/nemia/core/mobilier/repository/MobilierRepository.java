package com.nemia.core.mobilier.repository;

import com.nemia.core.mobilier.model.Mobilier;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobilierRepository extends JpaRepository<Mobilier, Long> {
  List<Mobilier> findByBienId(Long bienId);
}
