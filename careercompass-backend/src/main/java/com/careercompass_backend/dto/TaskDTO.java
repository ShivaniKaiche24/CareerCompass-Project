package com.careercompass_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
	
	private Long taskId;
    private String title;
    private String description;
    private Integer dayNumber;
    private LocalDate dueDate;
    private String status;
    private Long roadmapId;

}
