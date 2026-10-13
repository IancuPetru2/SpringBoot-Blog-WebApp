package net.projects.springboot.blog.ai;
//import net.projects.springboot.blog.ai.ResumeResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.projects.springboot.service.PostService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;



import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

  //  private static final String MODEL = "gemini-2.0-flash";
  //  private static final String MODEL = "gemini-2.0-flash-lite";
   // private static final String MODEL = "gemini-2.0-flash";
   // private static final String MODEL = "gemini-2.5-flash-lite";
   // private static final String MODEL = "gemini-2.0-flash";
  private static final String MODEL = "gemini-3.5-flash";
//  private static final String MODEL = "gemini-3-flash";
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent";

//    private final String model;
//    private final String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public GeminiClient(
            @Value("${gemini.api.key}") String apiKey,
          //  @Value("${gemini.model}") String model,
            ObjectMapper objectMapper
    ) {
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;

    }


        public String complete (String userPrompt){

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", userPrompt)
                            ))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            try {
                String rawResponse = restTemplate.postForObject(API_URL, request, String.class);

                System.out.println(rawResponse);

                JsonNode root = objectMapper.readTree(rawResponse);

                JsonNode parts = root.path("candidates")
                        .path(0)
                        .path("content")
                        .path("parts");

                StringBuilder text = new StringBuilder();

                for (JsonNode part : parts) {
                    if (part.has("text")) {
                        text.append(part.get("text").asText());
                    }
                }

                return text.toString();

            } catch (Exception e) {
                e.printStackTrace();
                throw new GeminiClientException("Failed to get completion from Gemini API", e);
            }
        }
        public static class GeminiClientException extends RuntimeException {
            public GeminiClientException(String message, Throwable cause) {
                super(message, cause);
            }
        }


}