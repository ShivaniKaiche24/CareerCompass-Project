package com.careercompass_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDTO {

	private Long taskId;
	private String title;
	private String description;
	private Integer dayNumber;
	private LocalDate dueDate;
	private String status; // PENDING / COMPLETED / MISSED
	private Long roadmapId;
	
}
