package com.manager.ads.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.CampaignPayment;

@Repository
public interface CampaignPaymentRepository extends JpaRepository<CampaignPayment, Long> {
    Optional<CampaignPayment> findTopByCampaignOrderByCreatedAtDesc(Campaign campaign);
}