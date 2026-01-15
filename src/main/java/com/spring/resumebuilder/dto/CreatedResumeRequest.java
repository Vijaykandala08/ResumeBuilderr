package com.spring.resumebuilder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CreatedResumeRequest {

    @NotBlank(message = "Title is required")
    private String title;
}
