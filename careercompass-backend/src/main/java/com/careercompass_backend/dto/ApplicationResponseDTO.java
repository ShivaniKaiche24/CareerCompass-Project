package com.careercompass_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 * The Application entity contains a User object which contains passwordHash.
 *  You never return entities directly. This DTO contains only what the frontend needs to display.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponseDTO {

	private Long applicationId;
	private String companyName;
    private String role;
    private LocalDate applyDate;
    private String status;
    private LocalDate followUpDate;
    private Long userId;

    // nullable — not every application goes through a consultancy
    private Long consultancyId;
}
