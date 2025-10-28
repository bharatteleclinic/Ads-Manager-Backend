package com.manager.ads.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationDeviceResponse {
    private Long id;
    private String deviceSerial;
    private String modelNumber;
    private Long enterpriseId;
    private Double latitude;
    private Double longitude;
    private String city;
    private String state;
    private String pin;
}
