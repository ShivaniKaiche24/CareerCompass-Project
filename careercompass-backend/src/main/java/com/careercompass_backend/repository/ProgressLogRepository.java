package com.careercompass_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careercompass_backend.model.ProgressLog;
import com.careercompass_backend.model.User;

public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {

	 // Find today's log entry — to update if it exists instead of creating duplicate
	Optional<ProgressLog> findByUserAndLogDate (User user , LocalDate logDate);
	
	// All logs for a user — ordered by date for streak calculation
	List<ProgressLog> findByUserOrderByLogDateDesc(User user);
	
   }
