package com.careercompass_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.careercompass_backend.dto.ResumeOptimizerRequestDTO;
import com.careercompass_backend.dto.ResumeOptimizerResponseDTO;
import com.careercompass_backend.model.User;
import com.careercompass_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeOptimizerService {

	   private final UserRepository userRepository;
	    private final RestTemplate restTemplate;
	    private final ObjectMapper objectMapper = new ObjectMapper();

	    @Value("${gemini.api.key}")
	    private String apiKey;

	    @Value("${gemini.api.url}")
	    private String apiUrl;

	    public ResumeOptimizerResponseDTO optimizeResume(
	            Long userId,
	            ResumeOptimizerRequestDTO request) {

	        // Load user profile
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException(
	                        "User not found: " + userId));

	        // Build prompt — give Gemini the user profile
	        // AND the job description
	        String prompt = buildOptimizerPrompt(user, request);

	        // Call Gemini
	        String geminiResponse = callGemini(prompt);

	        // Parse and return
	        return parseOptimizerResponse(geminiResponse);
	    }

	    private String buildOptimizerPrompt(
	            User user,
	            ResumeOptimizerRequestDTO request) {

	        StringBuilder prompt = new StringBuilder();

	        prompt.append("You are an expert ATS resume optimizer. ");
	        prompt.append("Analyze this candidate's profile against ");
	        prompt.append("the job description and return a JSON response.\n\n");

	        prompt.append("CANDIDATE PROFILE:\n");
	        prompt.append("- Degree: ").append(user.getDegreeType()).append("\n");
	        prompt.append("- Skills: ").append(user.getSkills()).append("\n");
	        prompt.append("- Target Role: ").append(user.getJobTarget()).append("\n");
	        if (user.getIsGapCandidate()) {
	            prompt.append("- Has career gap of: ")
	                  .append(user.getGapMonths())
	                  .append(" months\n");
	        }

	        prompt.append("\nJOB DESCRIPTION:\n");
	        prompt.append(request.getJobDescription()).append("\n");

	        prompt.append("\nReturn ONLY this JSON, no markdown:\n");
	        prompt.append("{\n");
	        prompt.append("  \"keywordsToAdd\": [\"keyword1\", \"keyword2\"],\n");
	        prompt.append("  \"matchingSkills\": [\"skill1\", \"skill2\"],\n");
	        prompt.append("  \"missingSkills\": [\"skill1\", \"skill2\"],\n");
	        prompt.append("  \"optimizedSummary\": \"2-sentence professional summary\",\n");
	        prompt.append("  \"matchScore\": 75\n");
	        prompt.append("}");

	        return prompt.toString();
	    }

	    private String callGemini(String prompt) {
	        String url = apiUrl + "?key=" + apiKey;

	        Map<String, Object> part = new HashMap<>();
	        part.put("text", prompt);

	        Map<String, Object> content = new HashMap<>();
	        content.put("parts", List.of(part));

	        Map<String, Object> requestBody = new HashMap<>();
	        requestBody.put("contents", List.of(content));

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        HttpEntity<Map<String, Object>> httpRequest =
	                new HttpEntity<>(requestBody, headers);


	        // Retry up to 3 times on 503
	        int attempts = 0;
	        while (attempts < 3) {
	            try {
	                ResponseEntity<String> response =
	                        restTemplate.postForEntity(
	                                url, httpRequest, String.class);

	                JsonNode root = objectMapper.readTree(
	                        response.getBody());
	                return root.path("candidates").get(0)
	                        .path("content").path("parts").get(0)
	                        .path("text").asText();

	            } catch (Exception e) {
	                attempts++;
	                log.warn("Gemini attempt {} failed: {}",
	                        attempts, e.getMessage());

	                if (attempts >= 3) {
	                    log.error("Gemini call failed after 3 attempts");
	                    throw new RuntimeException(
	                            "Resume optimization temporarily unavailable. " +
	                            "Please try again in a few minutes.");
	                }

	                // Wait 3 seconds before retrying
	                try {
	                    Thread.sleep(3000);
	                } catch (InterruptedException ie) {
	                    Thread.currentThread().interrupt();
	                }
	            }
	        }

	        throw new RuntimeException("Gemini call failed");
	    }

	    @SuppressWarnings("unchecked")
	    private ResumeOptimizerResponseDTO parseOptimizerResponse(
	            String responseText) {

	        try {
	            String clean = responseText.trim()
	                    .replaceAll("^```(?:json)?\\s*", "")
	                    .replaceAll("```\\s*$", "")
	                    .trim();

	            JsonNode root = objectMapper.readTree(clean);

	            ResumeOptimizerResponseDTO dto =
	                    new ResumeOptimizerResponseDTO();

	            // Parse keywords list
	            List<String> keywords = new ArrayList<>();
	            root.path("keywordsToAdd")
	                    .forEach(n -> keywords.add(n.asText()));
	            dto.setKeywordsToAdd(keywords);

	            // Parse matching skills
	            List<String> matching = new ArrayList<>();
	            root.path("matchingSkills")
	                    .forEach(n -> matching.add(n.asText()));
	            dto.setMatchingSkills(matching);

	            // Parse missing skills
	            List<String> missing = new ArrayList<>();
	            root.path("missingSkills")
	                    .forEach(n -> missing.add(n.asText()));
	            dto.setMissingSkills(missing);

	            dto.setOptimizedSummary(
	                    root.path("optimizedSummary").asText());
	            dto.setMatchScore(
	                    root.path("matchScore").asInt());

	            return dto;

	        } catch (Exception e) {
	            log.error("Failed to parse optimizer response: {}",
	                    e.getMessage());
	            throw new RuntimeException(
	                    "Failed to parse AI response");
	        }
	    }
}
