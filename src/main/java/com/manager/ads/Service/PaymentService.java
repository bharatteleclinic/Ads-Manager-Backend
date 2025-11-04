package com.manager.ads.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

    private RazorpayClient client;
    private String razorpaySecret;
    private String razorpayKey;

   @PostConstruct
    public void init() throws Exception {
        // load .env variables
        Dotenv dotenv = Dotenv.load();
        razorpayKey = dotenv.get("RAZORPAY_KEY");
        razorpaySecret = dotenv.get("RAZORPAY_SECRET");

        if (razorpayKey == null || razorpaySecret == null) {
            throw new RuntimeException("Razorpay credentials not found in .env");
        }

        client = new RazorpayClient(razorpayKey, razorpaySecret);
    }


    public String createOrder(double amountInINR, String receiptId) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        int amountInPaise = 1 * 100 ;
        // int amountInPaise = (int) (amountInINR * 100); // Razorpay expects amount in paise
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receiptId);
        orderRequest.put("payment_capture", 1); // auto-capture

        Order order = client.orders.create(orderRequest);
        return order.toString();
    }

    /**
     * Verify Razorpay payment signature
     */
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            Utils.verifyPaymentSignature(attributes, razorpaySecret);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}