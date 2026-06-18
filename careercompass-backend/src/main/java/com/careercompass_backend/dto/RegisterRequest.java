package com.careercompass_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
	
	@NotBlank(message = "Name is Required")
	private String name;
	
	@NotBlank(message = "Email is Required")
	@Email(message = "Please provide a valid email address")
	private String email;
	
	@NotBlank(message = "Password is required")
	private String password;
	
	private String degreeType;
	
	private String specialization;
	
	private Integer passoutYear;
	
	private String skills;
	
	private String jobTarget;
	
	private Boolean isGapCandidate= false;
	
	private Integer gapMonths;
	
	private String gapReason;
	

}
