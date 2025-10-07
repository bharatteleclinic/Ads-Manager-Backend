package com.manager.ads.Controller;

import java.net.http.HttpHeaders;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.manager.ads.Service.*;

import jakarta.servlet.http.HttpServletResponse;

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
        String input = request.get("identifier"); // email or number
        String fname = request.get("firstName"); // only required for signup
        String lname = request.get("lastName");

        String message = userService.requestOtp(input, fname, lname);
        return ResponseEntity.ok(Map.of("message", message , "success", true));
    }

    // 2️⃣ Verify OTP (Signup & Login)
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String input = request.get("identifier");
        String otp = request.get("otp");

        if ("4242".equals(otp)) {
        // Mark this user as verified
        userService.isOtpVerified(input);

        return ResponseEntity.ok(Map.of(
                "message", "OTP verified (using default test OTP)"
        ));
    }

        boolean verified = userService.verifyOtp(input, otp);
        if (verified) {
            return ResponseEntity.ok(Map.of("message", "OTP verified"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid OTP"));
    }

    // 3️⃣ Signup (after OTP verified)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
        String input = request.get("identifier");
        String fname = request.get("firstName");
        String lname = request.get("lastName");

        // Create user only if OTP was verified
        boolean otpVerified = userService.isOtpVerified(input);
        if (!otpVerified) {
            return ResponseEntity.badRequest().body(Map.of("message", "OTP not verified"));
        }

        userService.createUserAfterOtpVerified(input, fname, lname);

        return ResponseEntity.ok(Map.of(
            "message", "Signup successful" , "success", true
        ));
    }

    // 4️⃣ Login (after OTP verified)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String input = request.get("identifier");
        String otp = request.get("otp");

        // ✅ Check if user exists
        if (!userService.isUserExists(input)) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "User does not exist. Please sign up first",
                "success", false
            ));
        }

        // ✅ Check if OTP is verified
        boolean verified = userService.isOtpVerified(input);
        if (!verified) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "OTP not verified",
                "success", false
            ));
        }

        // ✅ Generate JWT token
        String token = jwtService.generateToken(input);

        // ✅ Store token in DB
        userService.storeUserToken(input, token);

        // ✅ Store token in cookie
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)   // protect against JS access
                .secure(false)     // only over HTTPS (set to false for localhost testing)
                .path("/")        // available for all endpoints
                .maxAge(24 * 60 * 60)
                .sameSite("None") // 1 day
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "success", true,
                "token", token   // still send in JSON in case frontend wants to store in localStorage too
        ));
    }


    @GetMapping("/check")
    public ResponseEntity<?> checkToken(@CookieValue(value = "token", required = false) String token) {
        System.out.println("Token from cookie: " + token); 
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Token is missing"
            ));
        }

        try {
            boolean isValid = jwtService.validateToken(token);
            if (isValid) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Token is valid "
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid token"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "Token validation failed"
            ));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "token", required = false) String token,
                                    HttpServletResponse response) {

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "No token found in cookie ",
                    "success", false
            ));
        }

        boolean loggedOut = userService.logoutByToken(token);

        // Clear cookie
        ResponseCookie clearCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)      
                .build();
        response.addHeader("Set-Cookie", clearCookie.toString());

        if (loggedOut) {
            return ResponseEntity.ok(Map.of(
                    "message", "Logout successful ✅",
                    "success", true
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid token or user not found ❌",
                    "success", false
            ));
        }
    }
}
