package com.careercompass_backend.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
public class Application {
	
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long applicationId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	/*
     * Optional FK to Consultancy.
     * nullable = true because many applications are direct — no consultancy involved.
     * When a consultancy is used, we store which one so we can later analyse
     * "which consultancies actually produce results vs which waste time".
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "consultant_id",nullable = false)
	private Consultancy consultancy;
	
	@Column(nullable = false)
	private String companyName;
	
	@Column(nullable = false)
	private String role ;
	
	private LocalDate applyDate;
	
	@Column(nullable = false)
	private String status = "APPLIED";
	
	private LocalDate followUpDate;
	

}
