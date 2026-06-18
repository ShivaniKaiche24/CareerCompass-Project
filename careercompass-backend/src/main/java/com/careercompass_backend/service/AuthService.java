package com.careercompass_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careercompass_backend.dto.LoginRequest;
import com.careercompass_backend.dto.LoginResponse;
import com.careercompass_backend.dto.RegisterRequest;
import com.careercompass_backend.model.User;
import com.careercompass_backend.repository.UserRepository;
import com.careercompass_backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtUtil jwtUtil;
	
	/* @Transactional makes this method ATOMIC.
	 * Atomic means: either ALL database operations inside this method succeed,
     * or NONE of them are committed. If an exception is thrown anywhere,
     * Spring rolls back every change made in this transaction.
	 * 
	 * The @Transactional annotation is what separates a service layer from
     * a simple helper class — services own transactional boundaries. 
	 */
	@Transactional
	public LoginResponse register(RegisterRequest request) {
		
		// Rejects duplicate emails before attempting any DB write
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered: " + request.getEmail());
		}
		
		// Map DTO fiels onto the entity
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		
		/*
         * BCrypt hashing happens HERE — in the service, not in the controller.
         * The controller's job is HTTP mechanics (parse request, return response).
         * Security decisions belong in the service layer.
         *
         * passwordEncoder.encode() does three things:
         *   1. Generates a random salt (unique per call — so same password hashes differently each time)
         *   2. Runs BCrypt with the configured 10 rounds
         *   3. Returns a 60-char string embedding the salt and hash together
         *
         * The raw password string is discarded here and never written anywhere.
         */
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		user.setDegreeType(request.getDegreeType());
		user.setSpecialization(request.getSpecialization());
		user.setPassoutYear(request.getPassoutYear());
		user.setSkills(request.getSkills());
		user.setJobTarget(request.getJobTarget());
		user.setRole("USER"); //All registrations are USER — ADMIN is set manually

		// Gap fields - default to false if client didn't send isGapcandidate
		user.setIsGapCandidate(request.getIsGapCandidate() != null ? request.getIsGapCandidate() : false );
	    user.setGapMonths(request.getGapMonths());
	    user.setGapReason(request.getGapReason());
	    
	    /*
         * save() triggers:
         *   1. Hibernate generates INSERT SQL
         *   2. @PrePersist on User fires — sets createdAt = LocalDateTime.now()
         *   3. MySQL inserts the row and returns the generated userId
         *   4. Hibernate puts the generated userId back into savedUser.getUserId()
         *
         * Note: we use the returned savedUser, not the original user object,
         * because only savedUser has the database-assigned userId populated.
         */
	    User savedUser = userRepository.save(user);
	    
	    // Generate JWT immediately — user is logged in right after registration
	    String token= jwtUtil.generateToken(
	    		savedUser.getEmail(),
	    		savedUser.getUserId(),
	    		savedUser.getRole());
	    
	    return new LoginResponse(
	    		token,
	    		savedUser.getEmail(),
	    		savedUser.getRole(),
	    		savedUser.getUserId());
	
	}
	 
	public LoginResponse login (LoginRequest request) {
		
		
		/* findByEmail returns Optional<>User
		 *  .orElseThrow() unwraps it if present, throws if absent
		 */
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException (
						"No account found with email: " + request.getEmail()));
	
		/*
         * BCrypt verification — the ONLY correct way to check a BCrypt password.
         *
         * passwordEncoder.matches(raw, stored) does:
         *   1. Extracts the salt from the stored hash (it's embedded in the hash string)
         *   2. Runs BCrypt on the raw password using the same salt
         *   3. Compares the output to the stored hash
         *
         * This is NOT a String.equals() comparison — that would compare raw to hash,
         * which would always fail because the hash looks nothing like the password.
         *
         * WRONG:  user.getPasswordHash().equals(request.getPassword())  ← never do this
         * CORRECT: passwordEncoder.matches(request.getPassword(), user.getPasswordHash())
         */
		if(!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())) {
			throw new RuntimeException("Invalid password");
		}
		
		String token = jwtUtil.generateToken(
				user.getEmail(), 
				user.getUserId(), 
				user.getRole()
				);
	
		return new LoginResponse(token, user.getEmail(), user.getRole(), user.getUserId());
	}

}
