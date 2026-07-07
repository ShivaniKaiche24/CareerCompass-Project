package com.careercompass_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.careercompass_backend.model.Application;
import com.careercompass_backend.model.User;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

	// All Aplication for User
	List<Application> findByUser(User user);
	
	// All by status 
	List<Application> findByStatus(String status);
	
	//Application that need follow-up today
	@Query("SELECT a FROM Application a WHERE a.user.userId = :userId AND a.followUpDate = :today AND a.status = 'APPLIED' ")
	List<Application> findFollowUpsForToday(@Param("userId") Long userId , @Param("today") LocalDate today);

    // Count for dashboard - how many application total
	long countByUser(User user);
	
}
