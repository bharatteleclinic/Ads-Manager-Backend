package com.manager.ads.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.manager.ads.Entity.Campaign;

import com.manager.ads.Entity.User;
import com.manager.ads.Repository.CampaignRepository;
import com.manager.ads.Repository.UserRepository;
import com.manager.ads.Service.CampaignService;


@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository; // Assuming you already have this

    public CampaignController(CampaignService campaignService, UserRepository userRepository, CampaignRepository campaignRepository) {
        this.campaignService = campaignService;
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createCampaign(
            @RequestParam String title,
            @RequestParam String type,
            @RequestParam String description,
            @RequestParam String brandName,
            @RequestParam String brandCategory,
            @RequestParam String adsType,
            @RequestParam("adFile") MultipartFile adFile,
            @RequestParam(required = true) String startDate,
            @RequestParam(required = true) String endDate,
            @RequestParam List<Integer> selectedDevices,  // list of device IDs
            @RequestParam int totalDevice,
            @RequestParam double totalPrice,
            @RequestParam Long userId,
            @RequestParam boolean draft
        ) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Campaign campaign = campaignService.createCampaign(
            title, type, description, brandName, brandCategory, adsType,
            adFile, user, startDate, endDate, totalPrice, totalDevice,
            selectedDevices, draft
            );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("campaignId", campaign.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/review/{userId}")
    public ResponseEntity<Campaign> getLatestCampaignByUserId(@PathVariable Long userId) {
        return campaignRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    } 
    
}
