package com.manager.ads.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

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

    private String objective;  

    private String brandCategory; 

    @Column(nullable = false)
    private String adsType;

    @ManyToOne(fetch = FetchType.LAZY)  // Many campaigns can belong to one user
    @JoinColumn(name = "user_id")       // Foreign key column
    private User user;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String adUrl;

    // private Double costPerDevicePerMonth;
    
    // // Optional: number of devices
    // private Integer devicesCount;
    
    // private Double totalCost;

    // getters and setters
}
