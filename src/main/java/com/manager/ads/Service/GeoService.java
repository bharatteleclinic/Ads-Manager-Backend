package com.manager.ads.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeoService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> getCityAndPincode(double lat, double lon) {
        try {
            String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon + "&zoom=10&addressdetails=1";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode address = root.path("address");

            String city = address.path("city").asText("");
            if (city.isEmpty()) city = address.path("town").asText("");
            if (city.isEmpty()) city = address.path("village").asText("");

            String pincode = address.path("postcode").asText("");

            Map<String, String> result = new HashMap<>();
            result.put("city", city);
            result.put("pincode", pincode);
            return result;
        } catch (Exception e) {
            return Map.of("city", "", "pincode", "");
        }
    }
}
