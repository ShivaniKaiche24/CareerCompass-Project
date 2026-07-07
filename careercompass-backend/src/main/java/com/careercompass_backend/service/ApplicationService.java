package com.careercompass_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careercompass_backend.dto.ApplicationRequestDTO;
import com.careercompass_backend.dto.ApplicationResponseDTO;
import com.careercompass_backend.model.Application;
import com.careercompass_backend.model.User;
import com.careercompass_backend.model.Consultancy;
import com.careercompass_backend.repository.ApplicationRepository;
import com.careercompass_backend.repository.ConsultancyRepository;
import com.careercompass_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final UserRepository userRepository;
	private final ConsultancyRepository consultancyRepository;
	// ─────────────────────────────────────────────────
    // LOG A NEW JOB APPLICATION
    // applyDate and followUpDate set automatically
    // status always starts as APPLIED
    // ─────────────────────────────────────────────────
	@Transactional
	public ApplicationResponseDTO logApplication(Long userId , ApplicationRequestDTO request) {
		
		 // Load user — fail fast if not found
		User user = userRepository.findById(userId)
				.orElseThrow(()-> new RuntimeException("User not found" + userId));
		
		// Build Application Entity 
		Application application = new Application();
		application.setUser(user);
		application.setCompanyName(request.getCompanyName());
		application.setRole(request.getRole());
		
		// Set automatically — user doesn't control these
		application.setApplyDate(LocalDate.now());
		application.setStatus("APPLIED");
		
		 // Follow up 7 days after applying
        // This is what makes the tracker useful —
        // you never forget to follow up
		application.setFollowUpDate(LocalDate.now().plusDays(7));
		
		 // Consultancy is optional — only set if provided
		if(request.getConsultancyId() != null) {
			Consultancy consultancy = consultancyRepository.findById(request.getConsultancyId())
					.orElseThrow(() -> new RuntimeException(
                            "Consultancy not found: "
                            + request.getConsultancyId()));
			application.setConsultancy(consultancy);
			
		}
		
		Application saved = applicationRepository.save(application);
		log.info("Application logged: user={}, company={}",userId ,request.getCompanyName());
		
		return toDTO(saved);
	}
	
	
	// ─────────────────────────────────────────────────
    // GET ALL APPLICATIONS FOR A USER
    // ─────────────────────────────────────────────────
	@Transactional
	public List<ApplicationResponseDTO> getAllApplication(Long userId){
		
		User user = userRepository.findById(userId)
				.orElseThrow(()-> new RuntimeException("User not Found " + userId));
		
		List<Application> application = applicationRepository.findByUser(user);
		return application.stream().map(this:: toDTO).collect(Collectors.toList());
	}
	
	
	// ─────────────────────────────────────────────────
    // UPDATE APPLICATION STATUS
    // APPLIED → INTERVIEW_SCHEDULED → OFFER / REJECTED
    // ownership check — users can only update their own
    // ─────────────────────────────────────────────────
	@Transactional
	public ApplicationResponseDTO updateStatus (Long applicationId , Long userId , String newStatus) {
		
		Application application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new RuntimeException(
                        "Application not found: " + applicationId));
		
		// Ownership check 
		if(!application.getUser().getUserId().equals(userId)) {
			throw new RuntimeException(
                    "You cannot update this application");
		}
		application.setStatus(newStatus);
		Application saved = applicationRepository.save(application);
		
		log.info("Application {} status updated to {} by user {}", applicationId,newStatus,userId);
		
		return toDTO(saved);
	}
	

    // ─────────────────────────────────────────────────
    // GET FOLLOW-UPS DUE TODAY
    // Applications where followUpDate = today
    // and status is still APPLIED
    // ─────────────────────────────────────────────────
	@Transactional
	public List<ApplicationResponseDTO>getFollowUpsForToday(Long userId) {
		List<Application> application = applicationRepository.findFollowUpsForToday(userId, LocalDate.now());
		return application.stream().map(this::toDTO).collect(Collectors.toList());
	}
	
	// DTO ---> toDTO method
	private ApplicationResponseDTO toDTO(Application application) {
		
	   ApplicationResponseDTO dto = new ApplicationResponseDTO();
	   
	   dto.setApplicationId(application.getApplicationId());
	   dto.setCompanyName(application.getCompanyName());
	   dto.setRole(application.getRole());
	   dto.setApplyDate(application.getApplyDate());
	   dto.setFollowUpDate(application.getFollowUpDate());
	   dto.setStatus(application.getStatus());
	   dto.setUserId(application.getUser().getUserId());
	   
	   // Consultancy is optional — only set ID if it exists
	   if(application.getConsultancy() != null) {
		   dto.setConsultancyId(application.getConsultancy().getCounstantId());
	   }
		
	   return dto;
	}
	
	
}
