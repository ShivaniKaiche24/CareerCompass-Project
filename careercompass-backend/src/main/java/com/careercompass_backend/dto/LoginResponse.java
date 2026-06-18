package com.careercompass_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
	
	   private String token;     // JWT — client stores this, sends it on every request
	    private String email;     // Allows client to display the logged-in user's email
	    private String role;      // USER or ADMIN — drives UI-level access control
	    private Long userId;      // Needed by the frontend to construct URLs like /users/{userId}/roadmap


}
