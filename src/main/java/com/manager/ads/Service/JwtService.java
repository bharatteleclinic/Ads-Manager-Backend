package com.manager.ads.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET_KEY = "2f2760862c00e27bde8d532d49e3f3cf"; // at least 32 chars
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hr

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String number) {
        return Jwts.builder()
                .setSubject(number)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
