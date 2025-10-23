package com.manager.ads.Controller;


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

        // Add only unique devices (avoid duplicates)
        for (ConsultationDevice device : devicesToAdd) {
            if (!campaign.getSelectedDevices().contains(device)) {
                campaign.getSelectedDevices().add(device);
            }
        }

        // Update count
        campaign.updateDeviceCount();

        double totalPrice = campaignService.getPriceForCampaign(campaign.getAdsType(), campaign.getDeviceCount());
        campaign.setTotalPrice(totalPrice); // optional, if you want to store total

        campaignRepository.save(campaign);
        return ResponseEntity.ok(Map.of(
            "message", "Devices added successfully",
            "deviceCount", campaign.getDeviceCount(),
            "totalPrice", totalPrice
        ));
        }

}
