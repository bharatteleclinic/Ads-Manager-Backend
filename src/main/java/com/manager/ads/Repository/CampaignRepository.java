package com.manager.ads.Repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manager.ads.Entity.Campaign;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}

