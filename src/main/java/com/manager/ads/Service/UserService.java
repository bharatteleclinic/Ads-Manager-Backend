package com.manager.ads.Service;

import org.springframework.stereotype.Service;
import com.manager.ads.Entity.User;
import com.manager.ads.Repository.UserRepository;

import java.util.Random;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OtpService otpService;

    public UserService(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    // Generate 4-digit OTP
    private String generateOtp() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    // Signup with fname, lname, email, number
    public String signup(User user) {
        Optional<User> optionalUser = userRepository.findByNumber(user.getNumber());

        User existingUser;
        if (optionalUser.isPresent()) {
            // ✅ Reuse existing user instead of blocking signup
            existingUser = optionalUser.get();
        } else {
            existingUser = new User();
            existingUser.setNumber(user.getNumber());
        }

        existingUser.setFname(user.getFname());
        existingUser.setLname(user.getLname());
        existingUser.setEmail(user.getEmail());
        existingUser.setVerified(false);

        String otp = generateOtp();
        existingUser.setOtp(otp);
        userRepository.save(existingUser);

        // Send OTP via SMS & WhatsApp
        otpService.sendOtpViaFast2Sms(user.getNumber(), otp);
        otpService.sendOtpViaAiSensy(user.getNumber(), otp);

        return "Signup OTP sent to " + user.getNumber();
    }

    // Verify signup OTP
    public boolean verifySignupOtp(String number, String otp) {
        return userRepository.findByNumber(number)
                .filter(u -> otp != null && otp.equals(u.getOtp()))
                .map(u -> {
                    u.setOtp(null); // clear OTP after verification
                    u.setVerified(true);
                    userRepository.save(u);
                    return true;
                })
                .orElse(false);
    }

    // Request login OTP (only number)
    public String requestLoginOtp(String number) {
        Optional<User> optionalUser = userRepository.findByNumber(number);

        if (optionalUser.isEmpty() || !optionalUser.get().isVerified()) {
            throw new RuntimeException("User not found or not verified");
        }

        User user = optionalUser.get();
        String otp = generateOtp();
        user.setOtp(otp);
        userRepository.save(user);

        // Send OTP via SMS & WhatsApp
        otpService.sendOtpViaFast2Sms(number, otp);
        otpService.sendOtpViaAiSensy(number, otp);

        return "Login OTP sent to " + number;
    }

    // Verify login OTP
    public boolean verifyLoginOtp(String number, String otp) {
        return userRepository.findByNumber(number)
                .filter(u -> otp != null && otp.equals(u.getOtp()))
                .map(u -> {
                    u.setOtp(null); // clear OTP after verification
                    userRepository.save(u);
                    return true;
                })
                .orElse(false);
    }
}

