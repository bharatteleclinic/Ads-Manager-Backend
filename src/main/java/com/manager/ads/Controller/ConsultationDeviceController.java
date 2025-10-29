package com.manager.ads.Controller;

import org.springframework.web.bind.annotation.*;

import com.manager.ads.Entity.ConsultationDevice;
import com.manager.ads.Repository.ConsultationDeviceRepository;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class ConsultationDeviceController {

    private final ConsultationDeviceRepository repository;

    public ConsultationDeviceController(ConsultationDeviceRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ConsultationDevice> getAllDevices() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ConsultationDevice getDeviceById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + id));
    }
}
