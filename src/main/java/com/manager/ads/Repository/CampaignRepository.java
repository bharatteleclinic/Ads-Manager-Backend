package com.manager.ads.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manager.ads.Entity.Campaign;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

  Optional<Campaign> findTopByUserIdOrderByCreatedAtDesc(Long userId);
  
  List<Campaign> findByUserIdAndDraftTrue(Long userId);
  
}


