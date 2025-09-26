package com.manager.ads.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.manager.ads.Entity.User;
import com.manager.ads.Service.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Signup - send OTP
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        String message = userService.signup(user);
        return ResponseEntity.ok(message);
    }

    // Verify signup OTP
    @PostMapping("/signup/verify")
    public ResponseEntity<?> verifySignupOtp(@RequestParam String number, @RequestParam String otp) {
        boolean verified = userService.verifySignupOtp(number, otp);
        return verified ? ResponseEntity.ok("Signup verified ✅")
                        : ResponseEntity.badRequest().body("Invalid OTP ❌");
    }

    // Request login OTP
    @PostMapping("/login/request")
    public ResponseEntity<?> requestLoginOtp(@RequestParam String number) {
        String message = userService.requestLoginOtp(number);
        return ResponseEntity.ok(message);
    }

    // Verify login OTP and return JWT
    @PostMapping("/login/verify")
    public ResponseEntity<?> verifyLoginOtp(@RequestParam String number, @RequestParam String otp) {
        boolean success = userService.verifyLoginOtp(number, otp);
        if (success) {
            String token = jwtService.generateToken(number);
            return ResponseEntity.ok(Map.of("message", "Login successful ✅", "token", token));
        }
        return ResponseEntity.badRequest().body("Invalid OTP ❌");
    }
}