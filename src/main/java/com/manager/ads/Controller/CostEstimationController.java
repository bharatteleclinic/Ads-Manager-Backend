// package com.manager.ads.Controller;

// import org.springframework.beans.factory.annotation.Autowired;

// import com.manager.ads.Entity.Campaign;
// import com.manager.ads.Entity.CampaignPayment;
// import com.manager.ads.Repository.CampaignPaymentRepository;
// import com.manager.ads.Repository.CampaignRepository;
// import com.manager.ads.Service.CostEstimationPdfService;

// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.manager.ads.Entity.CostEstimationRequest;

// import lombok.AllArgsConstructor;




// @RequestMapping("/api/cost-estimation")
// public class CostEstimationController {

//     private final CampaignRepository campaignRepository;
//     private final CampaignPaymentRepository campaignPaymentRepository;
//     private final CostEstimationPdfService costEstimationPdfService;  // injected PDF service

//     // Optional: You can remove this explicit constructor since @AllArgsConstructor handles it
//     // public CostEstimationController(...) { ... }

//     @GetMapping("{campaignId}")
//     public ResponseEntity<byte[]> generateInvoice(@PathVariable Long campaignId) throws Exception {

//         // Fetch campaign
//         Campaign campaign = campaignRepository.findById(campaignId)
//                 .orElseThrow(() -> new RuntimeException("Campaign not found"));

//         // Fetch latest payment for the campaign
//         CampaignPayment payment = campaignPaymentRepository.findTopByCampaignOrderByCreatedAtDesc(campaign)
//                 .orElseThrow(() -> new RuntimeException("Payment not found for this campaign"));

//         // Generate PDF in memory
//         byte[] pdfBytes = costEstimationPdfService.generateCostEstimation(campaign, payment);

//         // Return PDF as downloadable response
//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cost_estimation_" + campaign.getId() + ".pdf")
//                 .contentType(MediaType.APPLICATION_PDF)
//                 .body(pdfBytes);
//     }
// }


//  @Autowired
//     private CostEstimationService service;
    
// @AllArgsConstructor
// @RestController
// @PostMapping("/preview")
// public ResponseEntity<byte[]> generatePreview(@RequestBody CostEstimationRequest request) throws Exception {
//     // Generate PDF using the request payload (no DB dependency)
//     byte[] pdfBytes = costEstimationPdfService.generatePreviewCostEstimation(request);

//     return ResponseEntity.ok()
//             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cost_estimation_preview.pdf")
//             .contentType(MediaType.APPLICATION_PDF)
//             .body(pdfBytes);
// }
// }


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
<<<<<<< HEAD
}
=======
}

// package com.manager.ads.Controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.manager.ads.Entity.CostEstimationRequest;
// import com.manager.ads.Service.CostEstimationPdfService;

// @RestController
// @RequestMapping("/api/cost-estimation") // ✅ Base path
// public class CostEstimationController {

//     @Autowired
//     private CostEstimationPdfService costEstimationPdfService;

//     @PostMapping("/preview")
//     public ResponseEntity<byte[]> generatePreview(@RequestBody CostEstimationRequest request) throws Exception {
//         // Generate PDF using the request payload (no DB dependency)
//         byte[] pdfBytes = costEstimationPdfService.generatePreviewCostEstimation(request);

//         return ResponseEntity.ok()
//                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cost_estimation_preview.pdf")
//                 .contentType(MediaType.APPLICATION_PDF)
//                 .body(pdfBytes);
//     }
// }
>>>>>>> db7a70c80e360a2db10d61e7963135d8d8970598
