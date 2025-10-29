package com.manager.ads.Entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campaign_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fname;
    private String lname;
    @Column(unique = true, nullable = true)
    private String email;

    @Column(unique = true, nullable = true)
    private String number;

    private String otp;      
    private boolean verified;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Campaign> campaigns;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
