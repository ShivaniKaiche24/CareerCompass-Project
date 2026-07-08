package com.careercompass_backend.dto;

import lombok.Data;

@Data
public class ResumeOptimizerRequestDTO {

	// The job description the user wants to target
    private String jobDescription;

    // Optional — user can add extra context
    private String targetRole;
}
