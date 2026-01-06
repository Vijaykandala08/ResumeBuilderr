package com.spring.resumebuilder.service;

import com.spring.resumebuilder.dto.AuthResponse;
import com.spring.resumebuilder.dto.RegisterRequest;
import com.spring.resumebuilder.exception.ResourceExistsException;
import com.spring.resumebuilder.model.User;
import com.spring.resumebuilder.respository.Userrespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${app.base.url}")
    private String appBaseUrl;

    private final Userrespository userrespository;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        log.info("Inside AuthService: register() {}", request);
        if (userrespository.existsByEmail(request.getEmail())) {
            throw new ResourceExistsException("User already exists with this email");
        }
        User newUser = toDocument(request);

        userrespository.save(newUser);

        sendVerificationEmail(newUser);

        //TODO: send verification email

        return toResponse(newUser);

    }

    public void sendVerificationEmail(User newUser) {
        log.info("Inside AuthService: sendVerificationEmail() {}", newUser);
        try {
            String link = appBaseUrl + "/api/auth/verify-email?token=" + newUser.getVerificationToken();
            String html = "<div style='font-family:sans-serif; max-width:600px; margin:0 auto; padding:20px;'>" +
                    "<h2 style='color:#333;'>Verify your email</h2>" +
                    "<p style='color:#666; font-size:16px; line-height:1.6;'>" +
                    "Hi <strong>" + newUser.getName() + "</strong>,<br><br>" +
                    "Please click the button below to verify your email address and activate your account." +
                    "</p>" +
                    "<div style='text-align:center; margin:30px 0;'>" +
                    "<a href='" + link + "' " +
                    "style='display:inline-block; padding:12px 24px; background:#6366f1; " +
                    "color:#fff; text-decoration:none; border-radius:6px; font-weight:bold;'>" +
                    "Verify Email</a>" +
                    "</div>" +
                    "<p style='color:#999; font-size:14px;'>" +
                    "If you didn't create an account, you can safely ignore this email.<br>" +
                    "This verification link will expire in 24 hours." +
                    "</p>" +
                    "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'>" +
                    "<p style='color:#999; font-size:12px;'>" +
                    "If the button doesn't work, copy and paste this link in your browser:<br>" +
                    "<span style='color:#666;'>" + link + "</span>" +
                    "</p>" +
                    "</div>";

            emailService.sendHtmlEmail(newUser.getEmail(),"verify your email",html);

            log.info("Verification email sent to {}", newUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}",
                    newUser.getEmail(), e.getMessage());
            // Don't throw exception here if using async to prevent user registration failure
        }
    }

        private AuthResponse toResponse(User newUser){
            return AuthResponse.builder()
                    .id(newUser.getId())
                    .name(newUser.getName())
                    .email(newUser.getEmail())
                    .profileImageUrl(newUser.getProfileImageUrl())
                    .emailVerified(newUser.isEmailVerified())
                    .subscriptionPlan(newUser.getSubscriptionPlan())
                    .createdAt(newUser.getCreatedAt())
                    .updatedAt(newUser.getUpdatedAt())
                    .build();
        }

        private User toDocument(RegisterRequest request){
            return User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .profileImageUrl(request.getProfileImageUrl())
                    .subscriptionPlan("Basic")
                    .emailVerified(false)
                    .verificationToken(UUID.randomUUID().toString())
                    .verificationExpires(LocalDateTime.now().plusHours(24))
                    .build();

        }

        public void verifyEmail(String token){
        log.info("Inside AuthService: verifyEmail() {}", token);
            User user = userrespository.findByVerificationToken(token)
                    .orElseThrow(()-> new RuntimeException("Invalid or Expired Verification Token"));

            if(user .getVerificationExpires() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification token has expired.Please request new one");
            }
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setVerificationExpires(null);
            userrespository.save(user);
        }

}
