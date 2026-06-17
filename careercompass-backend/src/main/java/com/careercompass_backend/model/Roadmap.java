package com.careercompass_backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roadmaps")
@Data
@NoArgsConstructor
public class Roadmap {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roadmapId;
	
	/* ManyToOne - Many roadmaps can belong to One User
	 * FetchType.Lazy - when we load a roadmap , 
	 * LAZY do not automatically loads thr full User.
	 * 
	 * @JoinColumn - this tells hibernate that it is a foreign key column in the roadmaps table 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id" , nullable = false)
	private User user;
	
	@Column(nullable = false)
	private String title;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	@Column(nullable = false)
	private String status = "ACTIVE";
	
	private LocalDateTime generatedAt;
	
	@PrePersist
	
	protected void onCreate() {
		this.generatedAt = LocalDateTime.now();
	}

}
