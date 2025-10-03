package com.manager.ads.Entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
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

    private String otp;        // latest OTP
    private boolean verified;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Campaign> campaigns;// true only after signup OTP is validated
}
