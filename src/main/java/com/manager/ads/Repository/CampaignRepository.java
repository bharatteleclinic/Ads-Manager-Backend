package com.manager.ads.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.ads.Entity.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}

