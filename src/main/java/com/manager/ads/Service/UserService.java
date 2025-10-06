package com.manager.ads.Service;

import org.springframework.stereotype.Service;
import com.manager.ads.Entity.User;
import com.manager.ads.Repository.UserRepository;

import java.util.Random;
import java.util.Optional;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final OtpService otpService; // Your existing OTP sender service (Gmail/SMS)

    public UserService(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    // Generate 4-digit OTP
    private String generateOtp() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * Send OTP for signup or login
     */
    public String requestOtp(String input, String fname, String lname) {
        boolean isEmail = input.contains("@");
        User user;

        if (isEmail) {
            user = userRepository.findByEmail(input).orElse(null);
            if (user == null) {
                user = new User();
                user.setEmail(input);
            }
        } else {
            user = userRepository.findByNumber(input).orElse(null);
            if (user == null) {
                user = new User();
                user.setNumber(input);
            }
        }

        // Always update details
        user.setFname(fname);
        user.setLname(lname);
        user.setVerified(false);

        // Set OTP
        String otp = generateOtp();
        user.setOtp(otp);

        userRepository.save(user);

        // Send OTP
        if (isEmail) {
            otpService.sendOtpViaGmail(input, otp);
            return "OTP sent to email " + input;
        } else {
            otpService.sendOtpViaFast2Sms(input, otp);
            otpService.sendOtpViaAiSensy(input, otp);
            return "OTP sent to phone " + input;
        }
    }


    public boolean verifyOtp(String input, String otp) {
        boolean isEmail = input.contains("@");
        Optional<User> optionalUser = isEmail ? userRepository.findByEmail(input)
                                              : userRepository.findByNumber(input);

        return optionalUser
                .filter(u -> otp != null && otp.equals(u.getOtp()))
                .map(u -> {
                    u.setOtp(null); // clear OTP after verification
                    u.setVerified(true);
                    userRepository.save(u);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Check if user exists (used before final signup)
     */
    public boolean isUserExists(String input) {
        return input.contains("@") ? userRepository.findByEmail(input).isPresent()
                                   : userRepository.findByNumber(input).isPresent();
    }

    public boolean isOtpVerified(String input) {
        Optional<User> optionalUser;

        if (input.contains("@")) {
            // treat input as email
            optionalUser = userRepository.findByEmail(input);
        } else {
            // treat input as phone number
            optionalUser = userRepository.findByNumber(input);
        }

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);   // ✅ mark verified
            user.setOtp(null);        // ✅ clear OTP
            return true;
        }

        return false;
    }

    
    /**
     * Actually create user after OTP verified
     */
    public void createUserAfterOtpVerified(String input, String fname, String lname) {
        boolean isEmail = input.contains("@");
        User user = new User();
        if (isEmail) user.setEmail(input);
        else user.setNumber(input);

        user.setFname(fname);
        user.setLname(lname);
        user.setVerified(true);
        userRepository.save(user);
    }

    public void storeUserToken(String identifier, String token) {
        Optional<User> optionalUser = userRepository.findByEmail(identifier);

        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByNumber(identifier);
        }

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setToken(token);   // assume you added a `token` column in User table
            user.setLoggedIn(true); // optional: track logged in status
            userRepository.save(user);
        }
    }

    
    public boolean logout(String identifier) {
        Optional<User> optionalUser = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByNumber(identifier));

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setToken(null);       // remove token
            user.setLoggedIn(false);   // mark as logged out
            userRepository.save(user);
            return true;
        }
        return false;
    }

}
