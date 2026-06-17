package com.careercompass_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="consultanices")
@Data
@NoArgsConstructor
public class Consultancy {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long counstantId;
	
	@Column(nullable = false)
	private String name;
	
	private String city;
	
	private String phone;
	
	@Column(nullable = false)
	private Boolean isFraud =false;
	
	private String reportedBy;        // Who flagged this consultancy

	 @Column(columnDefinition = "TEXT")  
	private String reviewNotes;       // Details of the fraud complaint
}
