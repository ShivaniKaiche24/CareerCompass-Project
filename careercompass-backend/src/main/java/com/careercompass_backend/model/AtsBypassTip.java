package com.careercompass_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ats_bypass_tips")
@Data
@NoArgsConstructor
public class AtsBypassTip {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long tipId;

	    @Column(nullable = false)
	    private String title;            // "Add career break entry to resume"

	    @Column(columnDefinition = "TEXT")
	    private String description;      // Detailed instructions

	    /*
	     * Which week of the roadmap should this tip be injected into?
	     * Week 1 = immediate actions (resume formatting, LinkedIn gap section)
	     * Week 2 = outreach actions (direct HR contact, referral hunting)
	     * RoadmapService reads this field to know where to inject the task.
	     */
	    private Integer roadmapWeek;

	    /*
	     * Category groups tips for display and filtering.
	     * RESUME / LINKEDIN / OUTREACH / INTERVIEW / GENERAL
	     */
	    private String category;

	    /*
	     * If true, this tip is ONLY shown to gap candidates.
	     * If false, it's a general tip that benefits all users.
	     * RoadmapService filters: WHERE gap_specific = true AND is_active = true
	     * when building a gap candidate's roadmap.
	     */
	    private Boolean gapSpecific = true;

	    private Boolean isActive = true;  // Allows disabling tips without deleting the
}