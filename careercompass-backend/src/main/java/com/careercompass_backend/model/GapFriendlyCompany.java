package com.careercompass_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gap_friendly_companies")
@Data
@NoArgsConstructor
public class GapFriendlyCompany {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    @Column(nullable = false)
    private String companyName;

    /*
     * PRODUCT / SERVICE / STARTUP / MNC
     * Freshers target different types for different reasons:
     * startups for flexibility, service companies for volume hiring,
     * product companies for better technical growth.
     */
    private String companyType;

    private String city;

    /*
     * The field that makes this table useful.
     * A company that accepts gaps of 0–6 months is very different
     * from one that accepts up to 24 months.
     * Gap candidates filter: WHERE max_gap_months >= my gap months.
     */
    private Integer maxGapMonths;

    /*
     * ATS_SCREENING / DIRECT_HR / REFERRAL_ONLY
     * This is the most critical filter for gap candidates.
     * DIRECT_HR means a human reads the resume — the ATS doesn't kill it first.
     * Gap candidates should preferentially apply to DIRECT_HR companies.
     */
    private String screeningType;

    /*
     * Whether this company treats CDAC as a valid qualification.
     * Some companies' HR departments don't recognise CDAC — they see "not a
     * degree" and reject. This flag identifies companies that do.
     */
    private Boolean cdacRecognized = false;

    private Double minPackageLpa;    // Minimum CTC offered (in LPA)
    private Double maxPackageLpa;    // Maximum CTC offered

    /*
     * DIRECT / CONSULTANCY / REFERRAL / LINKEDIN
     * Tells the user how to approach this company.
     * Applied in the wrong channel can mean zero response
     * even if the company is otherwise gap-friendly.
     */
    private String applyVia;

    /*
     * Has a placed candidate confirmed this data?
     * Unverified data is still useful but flagged differently in the UI.
     */
    private Boolean communityVerified = false;

    private Boolean isActive = true;  // For soft-deleting stale records

    @Column(columnDefinition = "TEXT")
    private String notes;

}
