package com.careercompass_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * This is what the user sends in the request body. 
 * You don't want them setting status or followUpDate manually — your service handles those automatically.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequestDTO {

	private String companyName;
	private String role;
	
	// Optional — user may or may not have used a consultancy
    private Long consultancyId;
	
}
