package com.hedvig.botService.enteties;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignCodeRepository extends JpaRepository<CampaignCode, Integer> {
	List<CampaignCode> findByMemberId(String id);
}