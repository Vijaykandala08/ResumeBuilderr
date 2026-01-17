package com.spring.resumebuilder.controller;


import com.spring.resumebuilder.dto.CreatedResumeRequest;
import com.spring.resumebuilder.model.Resume;
import com.spring.resumebuilder.respository.ResumeRepository;
import com.spring.resumebuilder.service.FileUploadService;
import com.spring.resumebuilder.service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.spring.resumebuilder.util.AppConstants.*;

@RestController
@RequestMapping(RESUME)
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;
    private final FileUploadService fileUploadService;

    @PostMapping
    public ResponseEntity<?> createResume(@Valid @RequestBody CreatedResumeRequest request, Authentication authentication) {
        log.info("Inside createResume method {} {}" ,request,authentication);
        //step 1: Call the service method
        Resume newResume = resumeService.createResume(request,authentication.getPrincipal());
        log.info("Resume created successfully {}" ,newResume);
        //step 2: return the response
        return ResponseEntity.status(HttpStatus.CREATED).body(newResume);

    }

    @GetMapping
    public ResponseEntity<?> getUserResumes(Authentication authentication){
        //step 1: Call the service method
        List<Resume> resumes = resumeService.getUserResumes(authentication.getPrincipal());

        //step 2: return the response
        return ResponseEntity.ok(resumes);

    }

    @GetMapping(ID)
    public ResponseEntity<?> getResumeById( @PathVariable String id,
                                            Authentication authentication){
        //step 1: call the service method
        Resume existingResume = resumeService.getResumesById(id,authentication.getPrincipal());
        //step 2: return the response
        return ResponseEntity.ok(existingResume);

    }

    @PutMapping(ID)
    public ResponseEntity<?> updateResume(@PathVariable String id,
                                          @RequestBody Resume updatedData,
                                          Authentication authentication){
        //step 1: Call the service method
        Resume updatedResume = resumeService.updateResume(id,updatedData,authentication.getPrincipal());

        //step 2: return the response
        return ResponseEntity.ok(updatedData);



    }

    @PutMapping(UPLOAD_IMAGES)
    public ResponseEntity<?> uploadResumeImages(@PathVariable String id,
                                                @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                                @RequestPart(value = "profileImage",required = false ) MultipartFile profileImage,
                                                Authentication authentication) throws IOException {
        // step 1: Call the service method
        Map<String,String> response = fileUploadService.uploadResumeImages(id,authentication.getPrincipal(),thumbnail,profileImage);
        log.info("response={}" ,response);
        //step 2: return the response
        return ResponseEntity.ok(response);


    }

    @DeleteMapping(ID)
    public ResponseEntity<?> deleteResume(@PathVariable String id){
        return null;

    }


}
