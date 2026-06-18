package com.careercompass_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careercompass_backend.model.AtsBypassTip;

public interface AtsBypassTipRepository extends JpaRepository<AtsBypassTip, Long> {

    /*
     * This is the query that powers the gap candidate roadmap injection.
     * RoadmapService calls this when isGapCandidate = true.
     * Returns all active gap-specific tips, ordered by roadmap week
     * so they are injected in the correct order (Week 1 tips first).
     */
    List<AtsBypassTip> findByGapSpecificAndIsActiveOrderByRoadmapWeekAsc(
        Boolean gapSpecific, Boolean isActive
    );

    // Tips for a specific week — used if we want to inject only Week 1 or Week 2
    List<AtsBypassTip> findByRoadmapWeekAndIsActive(Integer roadmapWeek, Boolean isActive);

	
}
