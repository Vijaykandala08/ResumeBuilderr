package com.spring.resumebuilder.controller;

import com.spring.resumebuilder.dto.AuthResponse;
import com.spring.resumebuilder.dto.LoginRequest;
import com.spring.resumebuilder.dto.RegisterRequest;
import com.spring.resumebuilder.service.AuthService;
import com.spring.resumebuilder.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.spring.resumebuilder.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;

    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        log.info("Inside RegisterService: register() {}", request);
            AuthResponse response = authService.register(request);
            log.info("Register Response:{}", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log.info("Inside VerifyEmailService: verifyEmail() {}", token);
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Email Verify successful"));

    }

    @PostMapping(UPLOAD_PROFILE)
    public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file) throws IOException {
        log.info("Inside UploadImageService: uploadImage() {}", file);
        Map<String,String> response = fileUploadService.uploadSingleImage(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(VALIDATE)
    public String testValidationToken(){
        return "Token validation is working";

    }

}
