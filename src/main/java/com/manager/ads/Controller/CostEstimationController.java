package com.manager.ads.Controller;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.CampaignPayment;
import com.manager.ads.Repository.CampaignPaymentRepository;
import com.manager.ads.Repository.CampaignRepository;
import com.manager.ads.Service.CostEstimationPdfService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/cost-estimation")
public class CostEstimationController {

    private final CampaignRepository campaignRepository;
    private final CampaignPaymentRepository campaignPaymentRepository;
    private final CostEstimationPdfService costEstimationPdfService;  // injected PDF service

    // Optional: You can remove this explicit constructor since @AllArgsConstructor handles it
    // public CostEstimationController(...) { ... }

    @GetMapping("{campaignId}")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long campaignId) throws Exception {

        // Fetch campaign
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        // Fetch latest payment for the campaign
        CampaignPayment payment = campaignPaymentRepository.findTopByCampaignOrderByCreatedAtDesc(campaign)
                .orElseThrow(() -> new RuntimeException("Payment not found for this campaign"));

        // Generate PDF in memory
        byte[] pdfBytes = costEstimationPdfService.generateCostEstimation(campaign, payment);

        // Return PDF as downloadable response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cost_estimation_" + campaign.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}