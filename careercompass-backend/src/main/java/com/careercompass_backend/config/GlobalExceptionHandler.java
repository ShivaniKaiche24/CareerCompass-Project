package com.careercompass_backend.config;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.careercompass_backend.dto.ErrorResponseDTO;

@RestControllerAdvice    // This annotation tells Spring — "this class handles exceptions thrown anywhere in the application." It intercepts exceptions before Spring's default error handler gets them. @RestControllerAdvice is @ControllerAdvice + @ResponseBody combined — it returns JSON automatically.
public class GlobalExceptionHandler {

	// ─────────────────────────────────────────────────
    // HANDLES: "Task not found", "User not found",
    //          "Roadmap not found" etc.
    // STATUS: 404 Not Found
    //
    // Why RuntimeException and not a custom exception?
    // For a project of this size, RuntimeException is fine.
    // In production you would create custom exceptions like
    // ResourceNotFoundException, but that adds complexity
    // without much benefit at fresher level.
    // ─────────────────────────────────────────────────
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex){
		
		// Check if this is a "not found" type error
        // by looking at the message
		if(ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
			
			ErrorResponseDTO error = new ErrorResponseDTO(
					ex.getMessage() ,
					HttpStatus.NOT_FOUND.value(), 
					LocalDateTime.now()
		  );
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
		
		// Check if this is an unauthorized access error
		if(ex.getMessage() != null && (
				ex.getMessage().toLowerCase().contains("not allowed") || 
				ex.getMessage().toLowerCase().contains("cannot update") ||
                ex.getMessage().toLowerCase().contains("cannot"))) {
			
			
			ErrorResponseDTO error = new ErrorResponseDTO(
                    ex.getMessage(),
                    HttpStatus.FORBIDDEN.value(),
                    LocalDateTime.now()
            );
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(error);
		}
		
		 // Everything else — 400 Bad Request
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
		
	}
	

    // ─────────────────────────────────────────────────
    // HANDLES: @Valid annotation failures
    // When request body fails validation —
    // missing required fields, invalid email format etc.
    // STATUS: 400 Bad Request
    //
    // Why separate handler?
    // @Valid throws MethodArgumentNotValidException,
    // not RuntimeException. Different exception = different handler.
    // ─────────────────────────────────────────────────
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDTO> handleValidationException (
			MethodArgumentNotValidException ex) {
		
		// Extract the first validation error message
		String messaoge = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.findFirst()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
				.orElse("Validation Failed");
		
		ErrorResponseDTO error = new ErrorResponseDTO(
				messaoge,
				HttpStatus.BAD_REQUEST.value(),
				LocalDateTime.now());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	 // ─────────────────────────────────────────────────
    // HANDLES: Any unexpected exception
    // The safety net — catches anything that slips
    // through the handlers above
    // STATUS: 500 Internal Server Error
    // ─────────────────────────────────────────────────
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleGenericException(
			Exception ex){
		
		ErrorResponseDTO error = new ErrorResponseDTO(
                "Something went wrong. Please try again.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
		
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(error);
		
	}
	
}
