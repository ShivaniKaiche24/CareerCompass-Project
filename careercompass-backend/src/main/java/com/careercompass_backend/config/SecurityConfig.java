package com.careercompass_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.careercompass_backend.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

//@Configuration -- this class defines Spring Beans
//@EnableWebSecurity -- activates Spring Security web support
//@RequiredArgsConstructor -- Lombok injects JwtAuthFilter via ctor
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthFilter jwtAuthFilter;
	
	
	//BCrypt password encoder - strngth 10 means 2^10 = 1024 hashing rounds
	// Declared as @ bean so AuthService and tests can inject it
	//Never hardcode "new BCryptPasswordEncoder()" inside a service
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}
	
	// Expose AuthenticationManager as a bean - needed for programmatic auth flows
    @Bean
	public AuthenticationManager authenticationManager (
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
    
    public SecurityFilterChain filterChain(HttpSecurity http ) throws Exception {
    	http 
    	  // Disable CSRF - we use JWT in Authorization header , not cookies
    	  // CSRF attacks only work through cookies -> not applicable here
    	 .csrf(csrf -> csrf.disable())
    	 
    	 //Define URl - level access rules
    	 .authorizeHttpRequests(auth ->  auth
    			 
    	 // Public:registration and login - user has no token yet		 
         .requestMatchers("/api/auth/**").permitAll()
         
         //Public : Swagger docs- for development and demo 
         .requestMatchers(
        		 "/swagger-ui/**",
        		 "/swagger-ui.html",
        		 "/api-docs/**"
        		 ).permitAll()
         
                  // Everthing else requires a valid JWT token
                 .anyRequest().authenticated()	 
    			 )
    	 
    	 // Never create server-side sessions - every request carries its own JWT
    	 // This enforces stateless REST architecture
    	 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    			 )
    	 
    	 // Place out Jwt filter Before spring's default username/password filter
    	 //SO JWT validation happens first on every request
    	 .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
   
         return http.build();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
