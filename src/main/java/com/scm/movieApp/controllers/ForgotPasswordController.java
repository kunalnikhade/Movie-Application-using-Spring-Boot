package com.scm.movieApp.controllers;

import com.scm.movieApp.dto.ChangePassword;
import com.scm.movieApp.dto.MailBody;
import com.scm.movieApp.entities.ForgotPassword;
import com.scm.movieApp.entities.Users;
import com.scm.movieApp.repository.ForgotPasswordRepository;
import com.scm.movieApp.repository.UserRepository;
import com.scm.movieApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/api/forgotPassword")
public class ForgotPasswordController
{
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public ForgotPasswordController(EmailService emailService, UserRepository userRepository, ForgotPasswordRepository forgotPasswordRepository, BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping(value = "/verifyEmail/{email}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> verifyEmail(@PathVariable String email)
    {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Integer otp = generateOtp();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is a verification email for forgot password request : " +otp)
                .subject("Forgot Password Verification Request")
                .build();

        // Check if forgot password already exists for the user
        ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user)
                .orElse(ForgotPassword.builder().user(user).build());

        // Update or create fields
        forgotPassword.setOtp(otp);
        forgotPassword.setExpirationTime(new Date(System.currentTimeMillis() + 20 * 10000));

        emailService.simpleMessageSender(mailBody);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("Verification email sent");
    }

    @PostMapping(value = "/verifyOtp/{otp}/{email}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email)
    {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Otp: " + otp));

        if(forgotPassword.getExpirationTime().before(Date.from(Instant.now())))
        {
            forgotPasswordRepository.deleteById(forgotPassword.getForgotPasswordId());
            return new ResponseEntity<>("OTP has Expired", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping(value = "/changePassword/{email}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword changePassword, @PathVariable String email)
    {
        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword()))
        {
            return new ResponseEntity<>("Passwords do not match", HttpStatus.NOT_ACCEPTABLE);
        }

        String encryptedPassword = bCryptPasswordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email,encryptedPassword);

        return ResponseEntity.ok("Password changed successfully");
    }

    private Integer generateOtp()
    {
        Random random = new Random();
        return random.nextInt(100_100, 999_999);
    }
}
