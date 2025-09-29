package com.manager.ads.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 1️⃣ Send OTP (Signup & Login)
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        String fname = request.get("fname"); // only required for signup
        String lname = request.get("lname");

        String message = userService.requestOtp(input, fname, lname);
        return ResponseEntity.ok(Map.of("message", message));
    }

    // 2️⃣ Verify OTP (Signup & Login)
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        String otp = request.get("otp");

        boolean verified = userService.verifyOtp(input, otp);
        if (verified) {
            return ResponseEntity.ok(Map.of("message", "OTP verified ✅"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid OTP ❌"));
    }

    // 3️⃣ Signup (after OTP verified)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        String fname = request.get("fname");
        String lname = request.get("lname");

        // Create user only if OTP was verified
        boolean otpVerified = userService.isOtpVerified(input);
        if (!otpVerified) {
            return ResponseEntity.badRequest().body(Map.of("message", "OTP not verified ❌"));
        }

        userService.createUserAfterOtpVerified(input, fname, lname);

        return ResponseEntity.ok(Map.of(
                "message", "Signup successful ✅"
        ));
    }

    // 4️⃣ Login (after OTP verified)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String input = request.get("input");

        // Check if OTP is verified for this login session
        boolean verified = userService.isOtpVerified(input);
        if (!verified) {
            return ResponseEntity.badRequest().body(Map.of("message", "OTP not verified ❌"));
        }

        // Generate JWT token after OTP verification
        String token = jwtService.generateToken(input);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful ✅",
                "loggedIn", true,
                "token", token
        ));
    }
}
