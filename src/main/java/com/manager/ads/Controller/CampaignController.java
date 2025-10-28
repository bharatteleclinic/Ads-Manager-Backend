package com.manager.ads.Controller;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.ConsultationDevice;
import com.manager.ads.Entity.User;
import com.manager.ads.Repository.CampaignRepository;
import com.manager.ads.Repository.ConsultationDeviceRepository;
import com.manager.ads.Repository.UserRepository;
import com.manager.ads.Service.CampaignService;


@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final ConsultationDeviceRepository consultationDeviceRepository; // Assuming you already have this

    public CampaignController(CampaignService campaignService, UserRepository userRepository, CampaignRepository campaignRepository, ConsultationDeviceRepository consultationDeviceRepository) {
        this.campaignService = campaignService;
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
        this.consultationDeviceRepository = consultationDeviceRepository;
    }

    @PostMapping(consumes = "multipart/form-data")
    public Campaign createCampaign(
            @RequestParam String title,
            @RequestParam String type,
            @RequestParam String description,
            @RequestParam String objective,
            @RequestParam String brandCategory,
            @RequestParam String adsType,
            @RequestParam("adFile") MultipartFile adFile,
            @RequestParam Long userId
    ) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return campaignService.createCampaign(
                title, type, description, objective, brandCategory, adsType, adFile, user
        );
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateCampaign(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String objective,
            @RequestParam(required = false) String brandCategory,
            @RequestParam(required = false) String adsType,
            @RequestParam(required = false) MultipartFile adFile,
            @RequestParam(required = false) Long userId
    ) throws Exception {

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        }

        Campaign updatedCampaign = campaignService.updateCampaign(
                id, title, type, description, objective, brandCategory, adsType, adFile, user
        );

        return ResponseEntity.ok(Map.of(
                "message", "Campaign updated successfully",
                "campaignId", updatedCampaign.getId(),
                "adUrl", updatedCampaign.getAdUrl()
        ));
    }

    @PostMapping("/{id}/add-devices")
    public ResponseEntity<?> addDevicesToCampaign(
            @PathVariable Long id,
            @RequestBody List<Long> deviceIds) {

        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        List<ConsultationDevice> devicesToAdd = consultationDeviceRepository.findAllById(deviceIds);

        // Initialize if null
        if (campaign.getSelectedDevices() == null) {
            campaign.setSelectedDevices(new ArrayList<>());
        }

        for (ConsultationDevice device : devicesToAdd) {
            if (!campaign.getSelectedDevices().contains(device)) {
                campaign.getSelectedDevices().add(device);
            }
        }

        campaign.updateDeviceCount();

        double totalPrice = campaignService.getPriceForCampaign(campaign.getAdsType(), campaign.getDeviceCount());
        campaign.setTotalPrice(totalPrice);

        campaignRepository.save(campaign);
        return ResponseEntity.ok(Map.of(
            "message", "Devices added successfully",
            "deviceCount", campaign.getDeviceCount(),
            "totalPrice", totalPrice
        ));
    }

    @PutMapping("/{id}/update-devices")
    public ResponseEntity<?> updateDevicesForCampaign(
            @PathVariable Long id,
            @RequestBody List<Long> deviceIds) {

        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        List<ConsultationDevice> updatedDevices = consultationDeviceRepository.findAllById(deviceIds);

        campaign.setSelectedDevices(updatedDevices);

        campaign.updateDeviceCount();

        double totalPrice = campaignService.getPriceForCampaign(
                campaign.getAdsType(),
                campaign.getDeviceCount()
        );
        campaign.setTotalPrice(totalPrice);

        campaignRepository.save(campaign);

        return ResponseEntity.ok(Map.of(
            "message", "Devices updated successfully",
            "deviceCount", campaign.getDeviceCount(),
            "totalPrice", totalPrice
        ));
    }

    @GetMapping("/review/{userId}")
    public ResponseEntity<Campaign> getLatestCampaignByUserId(@PathVariable Long userId) {
        return campaignRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    } 
    
    @PostMapping("/save-draft")
    public ResponseEntity<Campaign> saveDraft(@RequestBody Campaign campaign) {
        campaign.setDraft(true);
        campaign.setCreatedAt(LocalDateTime.now());
        Campaign saved = campaignRepository.save(campaign);
        return ResponseEntity.ok(saved);
    }

}
