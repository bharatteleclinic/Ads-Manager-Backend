package com.manager.ads.Service;

import org.springframework.stereotype.Service;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Repository.CampaignRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }
}

