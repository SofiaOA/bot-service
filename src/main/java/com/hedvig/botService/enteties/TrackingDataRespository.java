package com.hedvig.botService.enteties;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingDataRespository extends JpaRepository<TrackingEntity, Integer> {
  List<TrackingEntity> findByMemberId(String id);
}
