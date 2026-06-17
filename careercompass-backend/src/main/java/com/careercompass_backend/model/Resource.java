package com.careercompass_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
public class Resource {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long resourceId;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String type;  // CDAC / CONSULTANCY / JOB_PORTAL / TRAINING
	
	private String city;  // used for location based filtering
	
	private String url;
	
	/*
     * The most important field in this entity.
     * Admin-verified: true = safe to use, false = fraud / poor quality.
     * Graduates filter resources by isGenuine=true to get a safe list.
     */
	@Column(nullable = false) 
	private Boolean isGenuine= true;
	
	private String feeRange; // Eg- 5,0000Rs - 15,000Rs
	
	@Column(columnDefinition = "TEXT")
	private String notes;

}
