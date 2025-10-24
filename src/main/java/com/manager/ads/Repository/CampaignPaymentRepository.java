package com.manager.ads.Repository;

import com.manager.ads.Entity.CampaignPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignPaymentRepository extends JpaRepository<CampaignPayment, Long> {
}