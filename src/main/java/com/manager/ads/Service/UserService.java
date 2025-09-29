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
            user = userRepository.findByEmail(input).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(input);
                newUser.setFname(fname);
                newUser.setLname(lname);
                newUser.setVerified(false);
                return newUser;
            });
        } else {
            user = userRepository.findByNumber(input).orElseGet(() -> {
                User newUser = new User();
                newUser.setNumber(input);
                newUser.setFname(fname);
                newUser.setLname(lname);
                newUser.setVerified(false);
                return newUser;
            });
        }

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

    /**
     * Verify OTP (email or phone)
     */
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
        return userRepository.findByEmail(input)
                .map(User::isVerified)
                .orElse(false);
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
}
