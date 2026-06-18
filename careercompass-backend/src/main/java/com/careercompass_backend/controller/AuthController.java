package com.careercompass_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.careercompass_backend.dto.LoginRequest;
import com.careercompass_backend.dto.LoginResponse;
import com.careercompass_backend.dto.RegisterRequest;
import com.careercompass_backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/*
 * @RestController = @Controller + @ResponseBody
 * Every method in this class serializes its return value to JSON automatically.
 * You never call ObjectMapper manually.
 */
@RestController

    /*@RequestMapping("/api/auth") applies to ALL methods in this class.
     * So register() maps to POST /api/auth/register
     *  and login() maps to POST /api/auth/login
     */
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	
	
	@PostMapping("/register")
	public ResponseEntity<LoginResponse> register (@Valid @RequestBody RegisterRequest request) {    // @Valid triggers Bean Validation on RegisterRequest BEFORE the method body runs. 
		LoginResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
    /*
     * Login returns 200 OK — not 201.
     * We are NOT creating a new resource. We are authenticating against
     * an existing one and returning a token.
     *
     * ResponseEntity.ok() is shorthand for ResponseEntity.status(200).body(...)
     */

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login (
			@Valid @RequestBody LoginRequest request){
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}
}
