package com.manager.ads.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;  

    private String type;  

    private String description;  

    private String brandName;  

    private String brandCategory; 

    @Column(nullable = false)
    private String adsType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  
    @JsonBackReference   
    private User user;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDate startDate;

    private LocalDate endDate ; 


    @ManyToMany
    @JoinTable(
        name = "consultation_devices_campaigns", 
        joinColumns = @JoinColumn(name = "campaign_id"),
        inverseJoinColumns = @JoinColumn(name = "device_id")
    )
    private List<ConsultationDevice> selectedDevices;

    @Column(name = "device_count")
    private int deviceCount;

    // helper method to update count automatically
    public void updateDeviceCount() {
        this.deviceCount = (selectedDevices != null) ? selectedDevices.size() : 0;
    }

    private Double totalPrice;

    @Column(nullable = false)
    private boolean draft = false;

    private String adUrl;

    @Column(nullable = false)
    private boolean isApproved = false;

}