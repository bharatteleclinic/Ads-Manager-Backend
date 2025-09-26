package com.manager.ads.Service;

import java.util.*;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;




@Service
public class OtpService {
    private static final String AISENSY_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4NTExYjJlNzMwODMxNTgzNTU0ODA5YSIsIm5hbWUiOiJCaGFyYXQgVGVsZUNsaW5pYyBQdnQgTFRELiIsImFwcE5hbWUiOiJBaVNlbnN5IiwiY2xpZW50SWQiOiI2NjczZWE0M2UwYTE0NzBiZmQwNGNkZDkiLCJhY3RpdmVQbGFuIjoiQkFTSUNfTU9OVEhMWSIsImlhdCI6MTc1NjI3NzE0MH0.NUTaztsv-ISw-y__TuIPPH2N6e-ceHONkaGeYkITvqQ";
    private static final String AISENSY_BASE_URL = "https://backend.aisensy.com/campaign/t1/api/v2";

    private final RestTemplate restTemplate;

    public OtpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean sendOtpViaAiSensy(String phoneNumber, String otp) {
        try {
    
            if (phoneNumber.startsWith("+")) {
                phoneNumber = phoneNumber.substring(1);
            }
            if (!phoneNumber.startsWith("91")) {
                phoneNumber = "91" + phoneNumber;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("apiKey", AISENSY_API_KEY);
            payload.put("campaignName", "OTP Campaign");
            payload.put("destination", phoneNumber);
            payload.put("userName", "Bharat TeleClinic Pvt LTD.");
            payload.put("templateParams", List.of(otp)); // ✅ MUST be a List
            payload.put("source", "new-landing-page form");
            payload.put("media", new HashMap<>());        // keep empty
            payload.put("buttons", List.of(Map.of(
                    "type", "button",
                    "sub_type", "url",
                    "index", 0,
                    "parameters", List.of(Map.of("type", "text", "text", "TESTCODE20"))
            )));
            payload.put("carouselCards", List.of());
            payload.put("location", new HashMap<>());
            payload.put("attributes", new HashMap<>());
            payload.put("paramsFallbackValue", Map.of("FirstName", "user"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(AISENSY_BASE_URL, request, String.class);

            System.out.println("📡 Response " + response.getStatusCode() + ": " + response.getBody());

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.err.println("💥 AiSensy OTP error: " + e.getMessage());
            return false;
        }
    }

    private static final String FAST2SMS_URL = "https://www.fast2sms.com/dev/bulkV2";
    private static final String FAST2SMS_API_KEY = "DQ9H3zxYZIc7Hw3pBZjlw0G3SB21f5jMQvQg7Zwxw4SLQ3p0tnCxwSbZ60fC";
    private static final String ENTITY_ID = "1207173614440376237";
    private static final String MESSAGE_ID = "178255";

    public boolean sendOtpViaFast2Sms(String phoneNumber, String otp) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("sender_id", "BHTLCL");   // Your registered sender id
            payload.put("route", "dlt");
            payload.put("message", MESSAGE_ID);   // Template ID configured in Fast2SMS
            payload.put("language", "english");
            payload.put("variables_values", otp); // OTP injection
            payload.put("numbers", phoneNumber);
            payload.put("entity_id", ENTITY_ID);

            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", FAST2SMS_API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(FAST2SMS_URL, request, String.class);

            System.out.println("📡 Fast2SMS Response: " + response.getStatusCode() + " - " + response.getBody());

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.err.println("❌ Error sending OTP via Fast2SMS: " + e.getMessage());
            return false;
        }
    }
}

