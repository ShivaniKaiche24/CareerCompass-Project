package com.careercompass_backend.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	/* It reads a value from your application.properties file and 
	 * injects it into this field automatically when Spring starts up.
	 * ${app.jwt.secret} → reads app.jwt.secret=careercompass-super-secret-key-... from properties.
	 */
	@Value("${app.jwt.secret}")
	private String jwtSecret;
	
	
	@Value("${app.jwt.expiration-ms}")
	private long jwtExpiration;
	
	/*The JWT library (jjwt) does not accept a raw String for signing. It requires a proper SecretKey object.
	 *  Think of it like this: A String is just text: "my-secret-key"
	 *  A SecretKey is a cryptographic object that knows how to participate in HMAC-SHA256 signing
	 * 
	 * Keys.hmacShaKeyFor() does two things:
	 * Converts your String to bytes (the raw binary material)
	 * Wraps those bytes in a SecretKey object that the JWT library can use
	 * */
	
	private SecretKey getSigninKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}
	
	
	public String generateToken(String email, Long userId, String role) {
		return Jwts.builder()
				.subject(email)  // subject-sub claim is a standard JWT field .It answers "who is this token about" . here we use email because it is our unique identifier for the users.
				
				/* userId: So the controller can know the logged-in user's ID without a database lookup. 
				 * The ID comes from the token.*/
				.claim("userId", userId) // custom claim - data we add ourself . The JWT standard gives sub,iat(issued At) , exp(expiration time) for free . .claim("Key" , value)
				/* role: So SecurityConfig can enforce "this endpoint is ADMIN only" 
				 * without a database lookup.*/
				.claim("role", role)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
				/* This is where the signature is created. jwt takes everything built so far (header + payload),
				 *  encodes it, runs it through HMAC-SHA256 with your secret key, and produces the signature. 
				 *  This single line is what makes JWT trustworthy.*/
				.signWith(getSigninKey()) 
				.compact(); // Finalizes the builder and produces the actual token string: eyJ...header....payload....signature. This is what you return and give to the client.
	
	
	}
	
	private Claims extractAllClaims(String token) {
   	 return Jwts.parser()
   			 /*The parser will re-compute the expected signature and compare it with the one in the token. 
   			  * If they don't match, someone tampered with the token and parseSignedClaims() throws a JwtException.
   			  */
   			 .verifyWith(getSigninKey())  
   			 .build() // Finalizes the parser configuration and produces a ready-to-use parser object.
   			  /* This is the core operation. It:
                 Splits the token into header, payload, signature
                 Re-computes what the signature should be using your secret key
                 Compares with the actual signature in the token
                 Checks if the expiration date has passed
                 If all good → returns the signed claims
                 If anything is wrong → throws an exception
   			    */
   			 .parseSignedClaims(token)
   			 .getPayload(); // Returns the Claims object — a Map-like object containing all the claims. You can then call .getSubject(), .get("userId", Long.class), etc.

	}
	

	     public String extractEmail(String token) {
	    	 return extractAllClaims(token) .getSubject();
	     }
	     
	     public Long extractUserId(String token) {
	    	 return extractAllClaims(token).get("userId", Long.class); // Long.class - Tells claims  what type to convert to . Bcz JSON numbers parse as INTEGER by Default (INTEGER to LONG - throws ClassCastException)
	     }
	     
	     public String extractRole(String token) {
	    	 return extractAllClaims(token).get("role", String.class);
	     }
	     
	     
	     public boolean isTokenValid(String token , String email) {
	    	 try {
	    		 String extractedEmail = extractEmail(token);
	    		 Date expiration=extractAllClaims(token).getExpiration();
	    		 return extractedEmail.equals(email) && expiration.after(new Date());
	    		 
	    	 }catch(Exception e) {
	    		 return false;
	    	 }
	     }
}
