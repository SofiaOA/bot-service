package com.hedvig.botService.enteties;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingDataRespository extends JpaRepository<TrackingEntity, Integer> {
	List<TrackingEntity> findByMemberId(String id);
}