package com.manager.ads.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "consultation_devices")
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_serial")
    private String deviceSerial;

    @Column(name = "model_number")
    private String modelNumber;

    @Column(name = "enterprise_id")
    private Long enterpriseId;

    @Column(name = "location")
    private String location;
}    

