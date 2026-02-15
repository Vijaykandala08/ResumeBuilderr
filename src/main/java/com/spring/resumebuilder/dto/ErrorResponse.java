package com.spring.resumebuilder.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private String message;
    private Object errors;
    private int status;
    private LocalDateTime timestamp;
}
