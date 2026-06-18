package com.careercompass_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careercompass_backend.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
	
	// Filter by City and Type - the main use case on the resource directory page
	List<Resource> findByCityAndType(String city ,String type );
	
	// Only verified resources
	List<Resource> findByIsGenuine(Boolean isGenuine);
	
	//Filter by type only (e.g. all consultancies , all job portals)
	List<Resource> findByType(String type);
	
	// Find by city only
	List<Resource> findByCity(String city);

}
