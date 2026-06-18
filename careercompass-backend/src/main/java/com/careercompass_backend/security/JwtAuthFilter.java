package com.careercompass_backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	
	//  JwtUtil is injected via constructor (Lombok @RequiredArgsConstructor)
	private final JwtUtil jwtUtil;
	
	/* This method signature is forced by OncePerRequestFilter that we must implement it.
	 * HttpServletRequest request — the incoming HTTP request. You read headers from here.
	 * HttpServletResponse response — the outgoing response. You could write to it (e.g., send a 401), but we let SecurityConfig handle that.
	 * FilterChain filterChain — the chain of filters. 
	 * You MUST call filterChain.doFilter(request, response) to pass the request to the next filter or controller.
	 *  If you forget this, the request dies in your filter.
	 */	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			                         HttpServletResponse response,
			                         FilterChain filterChain )
			                         throws ServletException ,IOException {
		
		// Read the Authorization header from the incoming request. Every secured API call should send this:- Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.. If the client didn't send this header, authHeader is null.
		String authHeader = request.getHeader("Authorization");
		
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return ;
		}
		
		 String token = authHeader.substring(7);
		 
		 try {
			 /*Call JwtUtil.extractEmail(). This internally calls extractAllClaims() which 
			  * calls parseSignedClaims() which verifies the signature. 
			  * If the token is malformed or tampered with, an exception is thrown here — 
			  * caught below, returns false, request continues without authentication.*/
			 String email = jwtUtil.extractEmail(token);
			 
			 /*SecurityContextHolder.getContext().getAuthentication() == null — Check if this request has already been authenticated. 
			  * If authentication is already set (from another filter running before this one), 
			  * don't overwrite it. This prevents double-processing.*/
			 if(email != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
				 
				 if(jwtUtil.isTokenValid(token, email)) {
					 String role = jwtUtil.extractRole(token);
					 SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role); // SimpleGrantedAuthority is Spring Security's way of representing a permission or role. You wrap the role string in this object so Spring Security understands it.
				 
					/*UsernamePasswordAuthenticationToken is Spring Security's standard object representing an authenticated user. It has three parts:
                      Principal (email) — who is this user? We use email as the identifier.
                      Credentials (null) — the password. We don't need it anymore (it was checked at login). null is correct here.
                      Authorities (List.of(authority)) — what can this user do? We pass the role.*/ 
					 UsernamePasswordAuthenticationToken authToken =  
							 new UsernamePasswordAuthenticationToken(email,
							 null,
							 List.of(authority));
					 authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Attaches extra request-level metadata to the authentication object — things like the IP address and session ID. This is optional for our application but it is good practice. It makes the authentication object richer for logging, auditing, and security monitoring tools.
				
					 
					 /*SecurityContextHolder is a thread-local storage.
					  *  Every thread (every incoming request runs in its own thread) has its own SecurityContext.
					  *   Setting the authentication here means: "for the duration of this request's thread, this user is authenticated."*/
					 SecurityContextHolder.getContext().setAuthentication(authToken);
				 
					 SecurityContextHolder.getContext().getAuthentication();
					 
					 
				 }
			 }
			 
		 }catch(Exception e) {
			 System.err.println("JWT validation error :" + e.getMessage());
			 
		 }
		 /*This line ALWAYS runs — whether the token was valid, invalid, or absent. 
		  * It passes the request to the next step in the filter chain.
		  *  If you put a return before this or remove this line, your controller will never receive the request.*/
		 filterChain.doFilter(request, response);
		
	}

}
