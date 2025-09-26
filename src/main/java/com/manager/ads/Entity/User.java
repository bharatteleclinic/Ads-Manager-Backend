package com.manager.ads.Entity;

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
    private String email;

    @Column(unique = true, nullable = false)
    private String number;

    private String otp;        // latest OTP
    private boolean verified;  // true only after signup OTP is validated
}
