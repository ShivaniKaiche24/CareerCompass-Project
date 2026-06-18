package com.careercompass_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careercompass_backend.model.Roadmap;
import com.careercompass_backend.model.User;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

	// All roadmaps for a paticular user
	List<Roadmap> findByUser(User user);
	
	// Tp prevent duplicate roadmaps for an user and to also check status of roadmap
	Optional<Roadmap> findByUserAndStatus(User user , String status);
}
