package com.careercompass_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {
	
	private final RestTemplate restTemplate;
	
	 /*
     * @Value reads from application.properties at startup.
     * If the key is missing or blank, Spring throws an error immediately —
     * you find out at startup, not during your first API call.
     */
	@Value("${gemini.api.key}")
	private String apiKey;
	
	@Value("${gemini.api.url}")
	private String apiUrl;
	
	
	  /*
     * ObjectMapper converts between Java objects and JSON.
     * It is thread-safe and expensive to create, so we use one instance.
     */
	private final ObjectMapper objectMapper = new ObjectMapper();

	/*
	 * MAIN METHOD — generates roadmap weeks from Gemini
	 * 
	 * Returns: Map<Integer,List<String>>
	 * Key=week numbers
	 * Value = list of task titles for that week
	 * 
	 * Ex Output-
	 * {
	 *    1:["Install Python","Learn variables", "Write first script"],
    //    2: ["Study functions", "Practice loops", "Complete HackerRank basics"] 
	 * }
	 */
	
	public Map<Integer,List<String>> generateRoadmapWeeks(
			String degreeType,
			String jobTarget,
			String skills,
			Integer passoutYear,
			Boolean isGapCandidate,
			Integer gapMonths
			) {
		
		  // Build the prompt that instructs Gemini exactly what to produce
		String prompt=buildRoadmapPrompt(degreeType, jobTarget, skills, passoutYear, isGapCandidate, gapMonths);
		
        String geminiResponse = callGeminiApi(prompt);
        
        // If Gemini failed, return fallback directly
        if (geminiResponse == null) {
            log.warn("Gemini unavailable — using fallback roadmap");
            return buildFallbackRoadmap("Gemini API unavailable");
        }
        
        return parseRoadmapFromResponse(geminiResponse);
        
  
	}
	
	  
    /*
     * Prompt Builder
     * A bad prompt = unpredictable , unusable output.
     * A good prompt = structured , consistent ,parseable output
     * 
     * Key principles used here
     * 1. Tell Gemini EXACTLY what format to return (JSON schema)
     * 2. Tell it what NOT to include (no markdown, no explanation)
     * 3. Give it all the user context it needs
     * 4. Specify the number of weeks based on the role
     */
     
    private String buildRoadmapPrompt(
    		String degreeType,
            String jobTarget,
            String skills,
            Integer passoutYear,
            Boolean isGapCandidate,
            Integer gapMonths 
            ) {
    	StringBuilder prompt = new StringBuilder();

    	 prompt.append("You are a senior IT career counsellor. ");
    	 prompt.append("Generate a structured week-by-week career preparation roadmap ");
         prompt.append("for the following candidate.\n\n"); 
    
         prompt.append("CANDIDATE PROFILE:\n");
         prompt.append("- Degree: ").append(degreeType != null ?degreeType : "Graduate").append("\n");
         prompt.append("- Target Role: ").append(jobTarget != null ?jobTarget: "Software Developer").append("\n");
         prompt.append("- Skills: ").append(skills != null && !skills.isEmpty() ? skills : "Basic programming knowledge").append("\n");
         prompt.append("- Passout Year").append(passoutYear != null ? passoutYear: "Recent").append("\n");
         
         if(Boolean.TRUE.equals(isGapCandidate) && gapMonths != null) {
        	 prompt.append("- Career Gap: ").append(gapMonths).append("months\n");
        	 prompt.append("- Note: This candidate has a career gap. The roadmap should acknowledge ");
             prompt.append("this and include tasks to build confidence and address the gap professionally.\n");
         
         }
 
         
         prompt.append("\nINSTRUCTIONS:\n");
         prompt.append("1. Generate an 8-week roadmap tailored exactly to the target role: ").append(jobTarget).append("\n");
         prompt.append("2. Each week must have 4-5 specific, actionable tasks.\n");
         prompt.append("3. Tasks must be concrete — not vague. Bad: 'Learn Java'. Good: 'Complete Java OOP concepts: classes, inheritance, polymorphism with 3 practice programs'.\n");
         prompt.append("4. Week 1 should cover fundamentals/setup. Week 8 should cover job application and interview prep.\n");
         prompt.append("5. Include tools, frameworks, and resources relevant to the role in 2026.\n");
         prompt.append("6. Consider the candidate's existing skills and do not repeat what they already know.\n");

         prompt.append("\nOUTPUT FORMAT — respond with ONLY this JSON, no markdown, no explanation, no code blocks:\n");
         prompt.append("{\n");
         prompt.append("  \"summary\": \"2-sentence overview of this roadmap\",\n");
         prompt.append("  \"weeks\": {\n");
         prompt.append("    \"1\": [\"task 1\", \"task 2\", \"task 3\", \"task 4\"],\n");
         prompt.append("    \"2\": [\"task 1\", \"task 2\", \"task 3\", \"task 4\"],\n");
         prompt.append("    \"3\": [\"task 1\", \"task 2\", \"task 3\", \"task 4\"],\n");
         prompt.append("    \"4\": [\"task 1\", \"task 2\", \"task 3\", \"task 4\"],\n");
         prompt.append("    \"5\": [\"task 1\", \"task 2\", \"task 3\", \"task 4\"],\n");
         prompt.append("    \"6\": [\"task 1\", \"task 2\", \"task 3\", \"task 4\"],\n");
         prompt.append("    \"7\": [\"task 1\", \"task 2\", \"task 3\", \"task 4\"],\n");
         prompt.append("    \"8\": [\"task 1\", \"task 2\", \"task 3\", \"task 4\"]\n");
         prompt.append("  }\n");
         prompt.append("}");
         
         return prompt.toString();

}
	/*
	 * GEMINI API CALLER
	 * 
	 */
    
    private String callGeminiApi(String prompt) {
    	
    	//Build the request URL - API key in query param
    	String url = apiUrl + "?key=" + apiKey;
    	
    	//Build the request body as a nested Map
    	// This gets serialized to JSON by RestTemplate automatically
    	Map<String,Object> part = new HashMap<>();
    	part.put("text", prompt);
    	
    	Map<String , Object> content = new HashMap<>();
    	content.put("parts", List.of(part));
    	
    	Map<String,Object> requestBody = new HashMap<>();
    	requestBody.put("contents", List.of(content));
    	
    	//Add generation config to control output format
    	Map<String,Object> generationConfig = new HashMap<>();
    	generationConfig.put("temperature", 0.7); // 0 = deterministic, 1 = creative
    	generationConfig.put("maxOutputTokens", 2048); // enough for 8 weeks of tasks
    	requestBody.put("generationConfig", generationConfig);
    	
    	// Set Headers
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody,headers);
    	
    	try {
    		
    		log.info("Gemini URL: {}", url);
    		// Make the POST request to Gemini
    		ResponseEntity<String> response = restTemplate.postForEntity(
    				url, request, String.class);
    		
    		// Parse Gemini's response structure to extract the text
    		return extractTextFromGeminiResponse(response.getBody());
    		
    		
    	}catch (Exception e) {
    	    log.error("Gemini API call failed: {}", e.getMessage());
    	    // Return null instead of throwing — caller will use fallback
    	    return null;
    	}
    	
  
    	
    	
    }
    
    /*
	 * Gemini Response Parser
	 */
	
	private String extractTextFromGeminiResponse(String responseBody) {
		
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			
			/*
			 * Navigate the JSON tree:
			 * root->candidates[0] -> content ->parts[0] ->text
			 */
			
		 JsonNode textNode = root
				 .path("candidates")
				 .get(0)
				 .path("content")
				 .path("parts")
				 .get(0)
				 .path("text");
		 
		 if(textNode.isMissingNode()) {
			 log.error("Unexpected Gemini response structure: {}", responseBody);
			 throw new RuntimeException("Could not extract text from Gemini response");
		 }
		 
		 return textNode.asText();
			
		}catch (Exception e) {
			log.error("Failed to parse Gemini response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        
		}
	}
	
	// ROADMAP JSON PARSER
    //
    // Gemini returns the roadmap as a JSON string inside its text response.
    // We parse that JSON into Map<Integer, List<String>>.
    //
    // This method also handles the case where Gemini adds markdown
    // code blocks (```json ... ```) despite being told not to —
    // AI models sometimes ignore format instructions. We strip them.
    
	
	 @SuppressWarnings("unchecked")
	    private Map<Integer, List<String>> parseRoadmapFromResponse(String responseText) {
	        try {
	            // Clean up any markdown code blocks Gemini may have added
	            // despite our instruction to return plain JSON
	            String cleanJson = responseText.trim();
	            if (cleanJson.startsWith("```")) {
	                // Remove opening ``` or ```json
	                cleanJson = cleanJson.replaceAll("^```(?:json)?\\s*", "");
	                // Remove closing ```
	                cleanJson = cleanJson.replaceAll("```\\s*$", "");
	                cleanJson = cleanJson.trim();
	            }

	            // Parse the cleaned JSON
	            JsonNode root = objectMapper.readTree(cleanJson);

	            // Extract the "weeks" object
	            JsonNode weeksNode = root.path("weeks");

	            if (weeksNode.isMissingNode()) {
	                log.error("No 'weeks' field in Gemini response: {}", cleanJson);
	                throw new RuntimeException("AI response missing 'weeks' field");
	            }

	            // Convert JsonNode weeks into Map<Integer, List<String>>
	            Map<Integer, List<String>> roadmapWeeks = new LinkedHashMap<>();

	            weeksNode.fields().forEachRemaining(entry -> {
	                int weekNumber = Integer.parseInt(entry.getKey());
	                List<String> tasks = new ArrayList<>();

	                // Each week's value is a JSON array of task strings
	                entry.getValue().forEach(taskNode -> tasks.add(taskNode.asText()));

	                roadmapWeeks.put(weekNumber, tasks);
	            });

	            log.info("Successfully parsed {} weeks from Gemini response", roadmapWeeks.size());
	            return roadmapWeeks;

	        } catch (Exception e) {
	            log.error("Failed to parse roadmap JSON: {}", e.getMessage());
	            // If parsing fails, return a safe fallback roadmap
	            // This prevents the user from seeing a 500 error
	            return buildFallbackRoadmap(e.getMessage());
	        }
	    }

	
	// FALLBACK ROADMAP
	    //
	    // If Gemini is unavailable or returns unparseable output,
	    // we return a generic roadmap instead of a 500 error.
	    // This is called "graceful degradation" — the system degrades
	    // to a lesser feature rather than crashing completely.
	    //
	    // Interview answer: "I implemented a fallback roadmap so the user
	    // is never left with a broken experience if the AI API is unavailable.
	    // This is graceful degradation — a production best practice."
	    
	 
	 private Map<Integer, List<String>> buildFallbackRoadmap(String errorMessage) {
	        log.warn("Using fallback roadmap due to AI parse error: {}", errorMessage);

	        Map<Integer, List<String>> fallback = new LinkedHashMap<>();

	        fallback.put(1, List.of(
	            "Set up your development environment for your target role",
	            "Research the top 5 required skills for your target role on LinkedIn Jobs",
	            "Identify 3 beginner-friendly online courses for your field",
	            "Create a dedicated GitHub repository for your learning projects"
	        ));
	        fallback.put(2, List.of(
	            "Complete beginner fundamentals for your target role",
	            "Build your first small project to apply what you learned",
	            "Join a relevant online community (Reddit, Discord, Stack Overflow)",
	            "Read 3 articles about current trends in your target field"
	        ));
	        fallback.put(3, List.of(
	            "Deepen your core technical knowledge",
	            "Build a slightly more complex project",
	            "Study how your target technology is used in real companies",
	            "Start solving problems on a relevant practice platform"
	        ));
	        fallback.put(4, List.of(
	            "Learn intermediate concepts in your target area",
	            "Add a feature to your existing project",
	            "Review 5 job descriptions and note required skills you lack",
	            "Start filling skill gaps identified from job descriptions"
	        ));
	        fallback.put(5, List.of(
	            "Build a complete end-to-end project for your portfolio",
	            "Write a README that explains your project clearly",
	            "Push your project to GitHub with proper commit messages",
	            "Get peer feedback on your project from an online community"
	        ));
	        fallback.put(6, List.of(
	            "Polish your resume for your target role",
	            "Update LinkedIn with your projects and target role headline",
	            "Write a gap statement if applicable",
	            "Research 10 companies hiring for your target role"
	        ));
	        fallback.put(7, List.of(
	            "Apply to 10 positions on Naukri and LinkedIn",
	            "Reach out to 5 people in your target role for informational interviews",
	            "Prepare answers to the top 10 technical interview questions",
	            "Practice explaining your projects in 2 minutes"
	        ));
	        fallback.put(8, List.of(
	            "Apply to 10 more positions",
	            "Complete 2 mock technical interviews",
	            "Follow up on all applications older than 7 days",
	            "Continue building skills while job searching"
	        ));

	        return fallback;
	    }

	// SUMMARY EXTRACTOR — gets the AI-generated roadmap summary
	   
	 public String extractSummary(String responseText) {
	        try {
	            String cleanJson = responseText.trim()
	                .replaceAll("^```(?:json)?\\s*", "")
	                .replaceAll("```\\s*$", "")
	                .trim();

	            JsonNode root = objectMapper.readTree(cleanJson);
	            JsonNode summaryNode = root.path("summary");

	            return summaryNode.isMissingNode()
	                ? "AI-generated personalised roadmap for your career goal."
	                : summaryNode.asText();

	        } catch (Exception e) {
	            return "AI-generated personalised roadmap for your career goal.";
	        }
	    }
}
