package com.manager.ads.Service;

import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.manager.ads.Entity.User;
import com.manager.ads.Repository.UserRepository;
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


    public String requestOtp(String input, String fname, String lname) {
        boolean isEmail = input.contains("@");
        User user;

        if (isEmail) {
            user = userRepository.findByEmail(input).orElse(null);
            if (user == null) {
                user = new User();
                user.setEmail(input);  // set only once for new user
            }
        } else {
            user = userRepository.findByNumber(input).orElse(null);
            if (user == null) {
                user = new User();
                user.setNumber(input); // set only once for new user
            }
        }

        // update details safely
        if (fname != null && !fname.isBlank()) {
            user.setFname(fname);
        }
        if (lname != null && !lname.isBlank()) {
            user.setLname(lname);
        }

        user.setVerified(false);

        // generate & set OTP
        String otp = generateOtp();
        user.setOtp(otp);


        // save (update if exists, insert if new)
        userRepository.save(user);

        // send OTP
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


    public boolean isUserExists(String input) {
        return input.contains("@") ? userRepository.findByEmail(input).isPresent()
                                   : userRepository.findByNumber(input).isPresent();
    }

   public boolean isOtpVerified(String input) {
        Optional<User> optionalUser;

        if (input.contains("@")) {
            optionalUser = userRepository.findByEmail(input);
        } else {
            optionalUser = userRepository.findByNumber(input);
        }

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);   // mark verified
            user.setOtp(null);        // clear OTP
            userRepository.save(user); // ✅ persist update
            return true;
        }

        return false;
    }

    
    /**
     * Actually create user after OTP verified
     */
    public void createUserAfterOtpVerified(String input, String fname, String lname) {
        boolean isEmail = input.contains("@");
        Optional<User> optionalUser;

        if (isEmail) {
            optionalUser = userRepository.findByEmail(input);
        } else {
            optionalUser = userRepository.findByNumber(input);
        }

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();   // ✅ update existing user
        } else {
            user = new User();           // ✅ only create if truly new
            if (isEmail) {
                user.setEmail(input);
            } else {
                user.setNumber(input);
            }
        }

        // update details
        user.setFname(fname);
        user.setLname(lname);
        user.setVerified(true);

        userRepository.save(user);  // ✅ this will now update instead of duplicate insert
    }

    public User getUserByNumber(String number) {
        return userRepository.findByNumber(number)
                .orElse(null); // returns null if not found
    }

    // Get user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null); // returns null if not found
    }

    public User getUserByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return userRepository.findByEmail(identifier).orElse(null);
        } else {
            return userRepository.findByNumber(identifier).orElse(null);
        }
    }

    public Long getUserIdByIdentifier(String identifier) {
        User user = getUserByIdentifier(identifier);
        return user != null ? user.getId() : null;
    }

}
