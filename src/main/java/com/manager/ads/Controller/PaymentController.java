package com.manager.ads.Controller;

import com.manager.ads.Entity.Campaign;
import com.manager.ads.Entity.CampaignPayment;
import com.manager.ads.Repository.CampaignPaymentRepository;
import com.manager.ads.Repository.CampaignRepository;
import com.manager.ads.Service.PaymentService;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

        double totalPrice = campaign.getTotalPrice();

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

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestParam Long paymentId,
                                           @RequestParam String razorpayPaymentId,
                                           @RequestParam String razorpaySignature) {
        CampaignPayment payment = campaignPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        boolean isValid = paymentService.verifySignature(payment.getRazorpayOrderId(),
                razorpayPaymentId, razorpaySignature);

        if (isValid) {
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setStatus("paid");
            campaignPaymentRepository.save(payment);
            return ResponseEntity.ok(Map.of("message", "Payment verified successfully"));
        } else {
            payment.setStatus("failed");
            campaignPaymentRepository.save(payment);
            return ResponseEntity.status(400).body(Map.of("message", "Invalid payment signature"));
        }
    }
}