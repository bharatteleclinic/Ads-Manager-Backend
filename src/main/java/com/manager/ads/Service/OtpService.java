package com.manager.ads.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class OtpService {

    private final String AISENSY_API_KEY;
    private final String AISENSY_BASE_URL;

    private final String FAST2SMS_API_KEY;
    private final String ENTITY_ID;
    private final String MESSAGE_ID;
    private final String FAST2SMS_URL;

    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final String REFRESH_TOKEN;
    private final String TOKEN_URL;
    private final String GMAIL_SEND_URL;
    private final String SENDER_EMAIL;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Constructor
    public OtpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        Dotenv dotenv = Dotenv.load(); 
        AISENSY_API_KEY = dotenv.get("AISENSY_API_KEY");
        AISENSY_BASE_URL = dotenv.get("AISENSY_BASE_URL");

        FAST2SMS_API_KEY = dotenv.get("FAST2SMS_API_KEY");
        ENTITY_ID = dotenv.get("ENTITY_ID");
        MESSAGE_ID = dotenv.get("MESSAGE_ID");
        FAST2SMS_URL = dotenv.get("FAST2SMS_URL");

        CLIENT_ID = dotenv.get("CLIENT_ID");
        CLIENT_SECRET = dotenv.get("CLIENT_SECRET");
        REFRESH_TOKEN = dotenv.get("REFRESH_TOKEN");
        TOKEN_URL = dotenv.get("TOKEN_URL");
        GMAIL_SEND_URL = dotenv.get("GMAIL_SEND_URL");
        SENDER_EMAIL = dotenv.get("SENDER_EMAIL");
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

    // private final RestTemplate restTemplate = new RestTemplate();

    private String getAccessToken() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("refresh_token", REFRESH_TOKEN);
        params.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get access token: " + response.getBody());
        }

        JsonNode json = objectMapper.readTree(response.getBody());
        return json.get("access_token").asText();
    }

    public boolean sendOtpViaGmail(String toEmail, String otp) {
        try {
            String accessToken = getAccessToken();

            String subject = "Your OTP Code";
            String body = "Hello,\n\nYour OTP code is: " + otp + "\n\nRegards,\nBharat TeleClinic";

            // Build raw MIME message manually (no jakarta.mail needed)
            String rawMessage = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString((
                            "To: " + toEmail + "\r\n" +
                            "From: " + SENDER_EMAIL + "\r\n" +
                            "Subject: " + subject + "\r\n\r\n" +
                            body
                    ).getBytes(StandardCharsets.UTF_8));

            Map<String, String> payload = Map.of("raw", rawMessage);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(GMAIL_SEND_URL, request, String.class);

            System.out.println("📧 Gmail Response " + response.getStatusCode() + ": " + response.getBody());

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("💥 Gmail OTP error: " + e.getMessage());
            return false;
        }
    }    
}
