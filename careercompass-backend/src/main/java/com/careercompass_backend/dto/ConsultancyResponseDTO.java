package com.careercompass_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultancyResponseDTO {

	  private Long consultantId;
	    private String name;
	    private String city;
	    private String phone;

	    // The most important field for gap candidates
	    // true = avoid this consultancy
	    private Boolean isFraud;

	    // Why they were flagged — shown as a warning
	    private String reviewNotes;
	
}
