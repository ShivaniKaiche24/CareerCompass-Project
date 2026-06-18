package com.careercompass_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.careercompass_backend.model.GapFriendlyCompany;

public interface GapFriendlyCompanyRepository extends JpaRepository<GapFriendlyCompany, Long> {

	 // The key gap candidate query:
    // "Find companies that accept my gap size and don't use ATS screening"
    @Query("SELECT g FROM GapFriendlyCompany g WHERE g.maxGapMonths >= :gapMonths AND g.isActive = true")
    List<GapFriendlyCompany> findByMaxGapMonthsGreaterThanEqual(@Param("gapMonths") Integer gapMonths);

 // DIRECT_HR companies — ATS bypass candidates
    List<GapFriendlyCompany> findByScreeningTypeAndIsActive(String screeningType, Boolean isActive);

    // Companies that recognise CDAC qualification
    List<GapFriendlyCompany> findByCdacRecognizedAndIsActive(Boolean cdacRecognized, Boolean isActive);

    // Combined filter — the most useful query for gap CDAC candidates
    @Query("SELECT g FROM GapFriendlyCompany g WHERE g.cdacRecognized = true AND g.screeningType = 'DIRECT_HR' AND g.maxGapMonths >= :months AND g.isActive = true")
    List<GapFriendlyCompany> findBestMatchForCdacGapCandidate(@Param("months") Integer gapMonths);
    
}
