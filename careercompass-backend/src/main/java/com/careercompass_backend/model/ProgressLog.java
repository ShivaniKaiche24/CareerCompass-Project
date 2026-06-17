package com.careercompass_backend.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "progress_log")
@Data
@NoArgsConstructor
public class ProgressLog {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long logId;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;

	    @Column(nullable = false)
	    private LocalDate logDate;

	    private Integer tasksCompleted;  // How many tasks were marked COMPLETED today
	    private Integer tasksTotal;      // How many tasks were due today

	    /*
	     * The streak counter.
	     * Logic: if tasksCompleted > 0, streakCount = yesterday's streakCount + 1
	     *        if tasksCompleted == 0, streakCount resets to 0
	     * This is calculated in ProgressService and stored here.
	     * Storing it (not calculating it on the fly) makes the dashboard fast —
	     * one SELECT instead of aggregating the entire history.
	     */
	    private Integer streakCount;
}
