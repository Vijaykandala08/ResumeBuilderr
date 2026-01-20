package com.spring.resumebuilder.service;

import com.spring.resumebuilder.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.spring.resumebuilder.util.AppConstants.PREMIUM;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateService {
    private final AuthService  authService;

    public Map<String,Object> getTemplates(Object principal){
        //step 1: get the current profile
        AuthResponse authResponse = authService.getProfile(principal);

        //step 2: get the available templates based on  subscription
        List<String> availableTemplates;
        Boolean isPremium = PREMIUM.equalsIgnoreCase(authResponse.getSubscriptionPlan());
        if(isPremium){
            availableTemplates = List.of("01","02","03");
        } else {
            availableTemplates = List.of("01");
        }
        //step 3: add the data into map
        Map<String,Object> restrictions = new HashMap<>();
        restrictions.put("availableTemplates",availableTemplates);
        restrictions.put("allTemplates",List.of("01","02","03"));
        restrictions.put("subscriptionPlan",authResponse.getSubscriptionPlan());
        restrictions.put("isPremium",isPremium);
        //step 4: return the result
        return restrictions;
    }
}
