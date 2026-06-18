package com.careercompass_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careercompass_backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	/*
	 * Spring Data generates this query from the method name:
     * SELECT * FROM users WHERE email = ?
     * Returns Optional<User> — not User directly.
     * Optional forces the caller to explicitly handle the case
     * where no user exists with that email, preventing NullPointerException.
     * Used in AuthService during login.
	 */
	Optional<User> findByEmail(String email);
	
	/*Used during registration to check whether this email is already used or not*/
	boolean existsByEmail(String email);
}
