package com.careercompass_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.careercompass_backend.model.Roadmap;
import com.careercompass_backend.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
	
	
	// Today's task for user - imp query in this system
	// @Query It fetches all tasks belonging to a specific user whose due date is today (or any date passed as parameter).	
	@Query("SELECT t FROM Task t WHERE t.roadmap.user.userId = :userId AND t.dueDate = :date")
	List<Task> findByTodaysTaskForUser(@Param("userId") Long userId , @Param("date") LocalDate date);

	// All tasks for a roadmap — used to build progress percentage
	List<Task> findByRoadmap(Roadmap roadmap);
	
	 // Overdue tasks — status still PENDING but dueDate has passed
    @Query("SELECT t FROM Task t WHERE t.roadmap.user.userId= :userId AND t.dueDate < :today AND t.status = 'PENDING'")
	List<Task> findOverdueTasksForUser(@Param("userId") Long userId,@Param("today") LocalDate today);
    
    // Tasks by status - used in dashboard calculation
    List<Task> findByroadmapAndStatus(Roadmap roadmap , String status);
}
