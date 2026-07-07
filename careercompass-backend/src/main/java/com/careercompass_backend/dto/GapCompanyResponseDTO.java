package com.careercompass_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GapCompanyResponseDTO {

	 private Long companyId;
	    private String companyName;
	    private String companyType;   // PRODUCT / SERVICE / STARTUP / MNC
	    private String city;

	    // How many months gap does this company accept
	    private Integer maxGapMonths;

	    // ATS_SCREENING / DIRECT_HR / REFERRAL_ONLY
	    // DIRECT_HR is best for gap candidates
	    // because a human reads the resume first
	    private String screeningType;

	    // Does this company recognize CDAC as a valid qualification
	    private Boolean cdacRecognized;

	    private Double minPackageLpa;
	    private Double maxPackageLpa;

	    // How to approach — DIRECT / LINKEDIN / CONSULTANCY
	    private String applyVia;

	    private String notes;
}
