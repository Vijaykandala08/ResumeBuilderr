package com.spring.resumebuilder.service;

import com.spring.resumebuilder.dto.AuthResponse;
import com.spring.resumebuilder.dto.CreatedResumeRequest;
import com.spring.resumebuilder.model.Resume;
import com.spring.resumebuilder.respository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public Resume createResume(CreatedResumeRequest request, Object principalObject) {
        log.info("Creating resume {} {}" ,request,principalObject);
        //step 1: Create resume object
        Resume newResume = new Resume();

        //step 2: Get the current profile
        AuthResponse response = authService.getProfile(principalObject);

        //step 3: Update the resume object
        newResume.setUserId(response.getId());
        newResume.setTitle(request.getTitle());

        //step 4: Set default data for resume
        setDefaultResumeData(newResume);
        log.info("Resume created successfully {}" ,newResume);

        //step 5: save the resume data
        return  resumeRepository.save(newResume);


    }

    private void setDefaultResumeData(Resume newResume) {
        newResume.setProfileInfo(new Resume.ProfileInfo());
        newResume.setContactInfo(new Resume.ContactInfo());
        newResume.setWorkExperiences(new ArrayList<>());
        newResume.setSkills(new ArrayList<>());
        newResume.setProjects(new ArrayList<>());
        newResume.setCertifications(new ArrayList<>());
        newResume.setLanguages(new ArrayList<>());
        newResume.setInterests(new ArrayList<>());
    }

    public List<Resume> getUserResumes(Object principal) {
        //step 1: Get the current profile
        AuthResponse response = authService.getProfile(principal);
        log.info("Getting user resumes {}" ,response);
        //step 2: Call the repository finder method
        List<Resume> resumes = resumeRepository.findByUserIdOrderByUpdatedAtDesc(response.getId());
        log.info("Getting user resumes {}" ,resumes);
        //step 3: return the response
        return resumes;
    }

    public Resume getResumesById(String resumeId, Object principal) {
        //step 1: Get the current profile
        AuthResponse response = authService.getProfile(principal);
        log.info("Getting resume {}" ,response);
        //step 2: Call the repo finder method
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                .orElseThrow(()-> new RuntimeException("User not found with these resumeId"));
        log.info("Getting resume {}" ,existingResume);
        //step 3: return the result
        return existingResume;
    }

    public Resume updateResume(String resumeId, Resume updatedData,Object principal) {
        //step 1: get the current profile
        AuthResponse response = authService.getProfile(principal);

        //step 2: call the repository finder method
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                .orElseThrow(()-> new RuntimeException("User not found"));

        log.info("Updating resume {}" ,existingResume);

        //step 3: update the new data
        existingResume.setTitle(updatedData.getTitle());
        existingResume.setThumbnailLink(updatedData.getThumbnailLink());
        existingResume.setTemplate(updatedData.getTemplate());
        existingResume.setProfileInfo(updatedData.getProfileInfo());
        existingResume.setContactInfo(updatedData.getContactInfo());
        existingResume.setWorkExperiences(updatedData.getWorkExperiences());
        existingResume.setEducation(updatedData.getEducation());
        existingResume.setSkills(updatedData.getSkills());
        existingResume.setProjects(updatedData.getProjects());
        existingResume.setCertifications(updatedData.getCertifications());
        existingResume.setLanguages(updatedData.getLanguages());
        existingResume.setInterests(updatedData.getInterests());
        //step 4: save the details into database
        resumeRepository.save(existingResume);
        return existingResume;
    }

    public void deleteResume(String resumeId, Object principal) {
        //step 1: get the current profile
        AuthResponse response = authService.getProfile(principal);
        //step 2: call the repository finder method
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                .orElseThrow(()-> new RuntimeException("Resume not found"));

        resumeRepository.delete(existingResume);

    }
}
