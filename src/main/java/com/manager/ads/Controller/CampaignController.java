package com.manager.ads.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Service.CampaignService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/campaigns")
@AllArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;


    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        System.out.println("Received campaign: " + campaign); 
        Campaign saved = campaignService.createCampaign(campaign);
        return ResponseEntity.ok(saved);
    }
}