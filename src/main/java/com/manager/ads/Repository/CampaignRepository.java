package com.manager.ads.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.ads.Entity.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findTopByOrderByCreatedAtDesc();
}

