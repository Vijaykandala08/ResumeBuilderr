package com.spring.resumebuilder.controller;

import com.spring.resumebuilder.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/templates")
@Slf4j
public class TemplatesController {

    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<?> getTemplates(Authentication authentication) {
        //STEP 1: CALL THE SERVICE METHOD
        Map<String,Object> response = templateService.getTemplates(authentication.getPrincipal());

        //STEP 2: RETURN THE RESPONSE
        return ResponseEntity.ok(response);

    }
}
