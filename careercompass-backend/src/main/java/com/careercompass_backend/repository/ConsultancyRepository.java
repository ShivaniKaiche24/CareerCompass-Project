package com.careercompass_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careercompass_backend.model.Consultancy;

public interface ConsultancyRepository extends JpaRepository<Consultancy, Long> {

	// All fraud Consultancies - shown with a warning in the User Interface
	List<Consultancy> findByIsFraud(Boolean isFraud);
	
	// filter by City - most graduates look for local consulatncy companies
	List<Consultancy> findByCity (String city);
	
	// Safe Consultancies in a specific city
	List<Consultancy> findByCityAndIsFraud(String city , Boolean isFraud );
}
