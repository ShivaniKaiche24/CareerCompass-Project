package com.careercompass_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressLogDTO {

	private Long logId;
	private LocalDate logDate;
	private Integer tasksCompleted;
	private Integer tasksTotal;
	private Integer streakCount;
	private Long userId;
	
}
