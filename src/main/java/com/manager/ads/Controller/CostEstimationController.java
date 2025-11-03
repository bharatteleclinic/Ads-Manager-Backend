package com.manager.ads.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.manager.ads.Entity.CostEstimationRequest;

import com.manager.ads.Service.CostEstimationPdfService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/cost-estimation")
@AllArgsConstructor
public class CostEstimationController {

    @Autowired
    private final CostEstimationPdfService costEstimationPdfService;

    // ✅ Preview endpoint (no DB dependency)
    @PostMapping("/preview")
    public ResponseEntity<byte[]> generatePreview(@RequestBody CostEstimationRequest request) throws Exception {
        // Generate PDF using the request payload (no DB dependency)
        byte[] pdfBytes = costEstimationPdfService.generatePreviewCostEstimation(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cost_estimation_preview.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

        
}