package com.manager.ads.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Service.CampaignService;
import com.manager.ads.Service.S3Service;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final S3Service s3Service;

    // Accept both JSON (campaign data) + File
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Campaign> createCampaign(
            @RequestPart("campaign") Campaign campaign,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException, java.io.IOException {

        // If file is uploaded, upload to S3 and set adUrl
        if (file != null && !file.isEmpty()) {
            String fileUrl = s3Service.uploadFile(file);
            campaign.setAdUrl(fileUrl);
        }

        Campaign saved = campaignService.createCampaign(campaign);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/latest-url")
    public ResponseEntity<String> getLatestCampaignUrl() {
        String latestUrl = campaignService.getLatestCampaignUrl();
        if (latestUrl == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latestUrl);
    }
}
