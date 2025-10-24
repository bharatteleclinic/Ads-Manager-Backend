package com.manager.ads.Controller;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import com.manager.ads.Entity.ConsultationDevice;
import com.manager.ads.Entity.ConsultationDeviceResponse;
import com.manager.ads.Repository.ConsultationDeviceRepository;
import com.manager.ads.Service.GeoService;

import java.util.*;


@RestController
@RequestMapping("/api/devices")
public class ConsultationDeviceController {

    private final ConsultationDeviceRepository repository;
    private final GeoService geoService;

    public ConsultationDeviceController(ConsultationDeviceRepository repository, GeoService geoService) {
        this.repository = repository;
        this.geoService = geoService;
    }

    @GetMapping
    public List<ConsultationDeviceResponse> getAllDevices() {
        List<Map<String, Object>> rows = repository.findAllWithLatLong();
        List<ConsultationDeviceResponse> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Double lat = (Double) row.get("latitude");
            Double lon = (Double) row.get("longitude");

            Map<String, String> address = geoService.getCityAndPincode(lat, lon);

            ConsultationDeviceResponse dto = new ConsultationDeviceResponse(
                ((Number) row.get("id")).longValue(),
                (String) row.get("deviceSerial"),
                (String) row.get("modelNumber"),
                row.get("enterpriseId") != null ? ((Number) row.get("enterpriseId")).longValue() : null,
                lat,
                lon,
                address.get("city"),
                address.get("pincode")
            );
            result.add(dto);
        }

        return result;
    }

    @GetMapping("/{id}")
    public ConsultationDevice getDeviceById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + id));
    }
}

