package com.manager.ads.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manager.ads.Entity.CampaignPayment;

@Repository
public interface CampaignPaymentRepository extends JpaRepository<CampaignPayment, Long> {
}