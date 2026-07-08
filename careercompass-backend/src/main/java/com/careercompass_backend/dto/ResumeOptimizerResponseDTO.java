package com.careercompass_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeOptimizerResponseDTO {

	// Keywords from the JD that should be in the resume
    private List<String> keywordsToAdd;

    // Skills the user has that match the JD
    private List<String> matchingSkills;

    // Skills the user is missing for this role
    private List<String> missingSkills;

    // AI-generated resume summary for this specific job
    private String optimizedSummary;

    // Overall match score out of 100
    private Integer matchScore;
}
