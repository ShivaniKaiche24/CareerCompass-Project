package com.careercompass_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {

	// The error message — what went wrong
    private String error;

    // HTTP status code — 404, 400, 403, 500
    private int status;

    // When the error happened
    private LocalDateTime timestamp;
}
