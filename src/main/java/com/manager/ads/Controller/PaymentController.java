package com.manager.ads.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.CampaignPayment;
import com.manager.ads.Repository.CampaignPaymentRepository;
import com.manager.ads.Repository.CampaignRepository;
import com.manager.ads.Service.InvoiceService;
import com.manager.ads.Service.PaymentService;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final CampaignRepository campaignRepository;
    private final CampaignPaymentRepository campaignPaymentRepository;

    public PaymentController(PaymentService paymentService,
                             CampaignRepository campaignRepository,
                             CampaignPaymentRepository campaignPaymentRepository) {
        this.paymentService = paymentService;
        this.campaignRepository = campaignRepository;
        this.campaignPaymentRepository = campaignPaymentRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRazorpayOrder(@RequestParam Long campaignId) throws Exception {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        double totalPrice = campaign.getTotalPrice(); // from your campaign

        String receiptId = "campaign_" + campaign.getId();
        String razorpayOrderJson = paymentService.createOrder(totalPrice, receiptId);
        JSONObject orderObj = new JSONObject(razorpayOrderJson);

        // Save order info
        CampaignPayment payment = new CampaignPayment();
        payment.setCampaign(campaign);
        payment.setRazorpayOrderId(orderObj.getString("id"));
        payment.setAmount(totalPrice);
        payment.setStatus(orderObj.getString("status"));
        campaignPaymentRepository.save(payment);

        return ResponseEntity.ok(orderObj.toMap());
    }

    // @PostMapping("/verify")
    // public ResponseEntity<?> verifyPayment(@RequestParam Long paymentId,
    //                                        @RequestParam String razorpayPaymentId,
    //                                        @RequestParam String razorpaySignature) {
    //     CampaignPayment payment = campaignPaymentRepository.findById(paymentId)
    //             .orElseThrow(() -> new RuntimeException("Payment not found"));

    //     boolean isValid = paymentService.verifySignature(payment.getRazorpayOrderId(),
    //             razorpayPaymentId, razorpaySignature);

    //     if (isValid) {
    //         payment.setRazorpayPaymentId(razorpayPaymentId);
    //         payment.setStatus("paid");
    //         campaignPaymentRepository.save(payment);
    //         return ResponseEntity.ok(Map.of("message", "Payment verified successfully"));
    //     } else {
    //         payment.setStatus("failed");
    //         campaignPaymentRepository.save(payment);
    //         return ResponseEntity.status(400).body(Map.of("message", "Invalid payment signature"));
    //     }
    // }


// @PostMapping("/verify")
//     public ResponseEntity<?> verifyPayment(@RequestParam Long paymentId,
//                                            @RequestParam String razorpayPaymentId,
//                                            @RequestParam String razorpaySignature) {
//         CampaignPayment payment = campaignPaymentRepository.findById(paymentId)
//                 .orElseThrow(() -> new RuntimeException("Payment not found"));

//         boolean isValid = paymentService.verifySignature(
//                 payment.getRazorpayOrderId(),
//                 razorpayPaymentId,
//                 razorpaySignature
//         );

//         if (isValid) {
//             payment.setRazorpayPaymentId(razorpayPaymentId);
//             payment.setStatus("paid");
//             campaignPaymentRepository.save(payment);

//             try {
//                 // ✅ Generate Invoice PDF as byte[]
//                 byte[] invoiceBytes = invoiceService.generateInvoicePDF(payment.getId());

//                 // ✅ Save the file to local folder (optional)
//                 Path invoiceDir = Paths.get("invoices");
//                 Files.createDirectories(invoiceDir);
//                 Path invoicePath = invoiceDir.resolve("invoice-" + payment.getId() + ".pdf");
//                 Files.write(invoicePath, invoiceBytes);

//                 //save file path in DB
//                  payment.setInvoicePath(invoicePath.toString());
//                  campaignPaymentRepository.save(payment);

//                 return ResponseEntity.ok(Map.of(
//                         "message", "Payment verified successfully",
//                         "invoiceFile", invoicePath.getFileName().toString(),
//                         "downloadUrl", "/api/payments/invoice/download/" + payment.getId()
//                 ));
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 return ResponseEntity.status(500).body(Map.of(
//                         "message", "Payment verified, but invoice generation failed",
//                         "error", e.getMessage()
//                 ));
//             }
//         } else {
//             payment.setStatus("failed");
//             campaignPaymentRepository.save(payment);
//             return ResponseEntity.status(400).body(Map.of("message", "Invalid payment signature"));
//         }
//     }
// @PostMapping("/verify")
// public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> payload) {
//     String orderId = (String) payload.get("razorpay_order_id");
//     String paymentId = (String) payload.get("razorpay_payment_id");
//     String status = (String) payload.get("status");
//     Double amount = Double.valueOf(payload.get("amount").toString());
//     Long campaignId = Long.valueOf(payload.get("campaign_id").toString());

//     // Save payment and generate invoice
//     Campaign campaign = campaignRepository.findById(campaignId)
//             .orElseThrow(() -> new RuntimeException("Campaign not found"));

//     CampaignPayment payment = new CampaignPayment();
//     payment.setRazorpayOrderId(orderId);
//     payment.setRazorpayPaymentId(paymentId);
//     payment.setStatus(status);
//     payment.setAmount(amount);
//     payment.setCampaign(campaign);

//     campaignPaymentRepository.save(payment);

//     // Generate invoice dynamically
//     byte[] pdfBytes = invoiceService.generateInvoicePDF(payment.getId());
//     Path filePath = Paths.get("invoices", "invoice-" + payment.getId() + ".pdf");
//     Files.createDirectories(filePath.getParent());
//     Files.write(filePath, pdfBytes);

//     Map<String, Object> response = new HashMap<>();
//     response.put("message", "Payment verified & invoice generated successfully");
//     response.put("paymentId", payment.getId());
//     response.put("invoicePath", filePath.toString());

//     return ResponseEntity.ok(response);
// }
@PostMapping("/verify")
public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> payload) {
    try {
        String orderId = (String) payload.get("razorpay_order_id");
        String paymentId = (String) payload.get("razorpay_payment_id");
        String status = (String) payload.get("status");
        Double amount = Double.valueOf(payload.get("amount").toString());
        Long campaignId = Long.valueOf(payload.get("campaign_id").toString());

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        CampaignPayment payment = new CampaignPayment();
        payment.setRazorpayOrderId(orderId);
        payment.setRazorpayPaymentId(paymentId);
        payment.setStatus(status);
        payment.setAmount(amount);
        payment.setCampaign(campaign);

        campaignPaymentRepository.save(payment);

        // ✅ Wrap this in try-catch
        byte[] pdfBytes = invoiceService.generateInvoicePDF(payment.getId());
        Path filePath = Paths.get("invoices", "invoice-" + payment.getId() + ".pdf");
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, pdfBytes);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment verified & invoice generated successfully");
        response.put("paymentId", payment.getId());
        response.put("invoicePath", filePath.toString());

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(Map.of(
                "message", "Error verifying payment or generating invoice",
                "error", e.getMessage()
        ));
    }
}

@Autowired
private InvoiceService invoiceService;

@GetMapping("/invoice/download/{paymentId}")
    public ResponseEntity<?> downloadInvoice(@PathVariable Long paymentId) {
        try {
            byte[] pdfData = invoiceService.generateInvoicePDF(paymentId);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=invoice-" + paymentId + ".pdf")
                    .body(pdfData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(Map.of(
                    "message", "Invoice not found or could not be generated",
                    "error", e.getMessage()
            ));
        }
    }
}