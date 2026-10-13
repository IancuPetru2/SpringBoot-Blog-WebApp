package net.projects.springboot.blog.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SummaryService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public SummaryService(GeminiClient geminiClient, ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    public PostSummary generate(String postContent) {
        String prompt = """
                Read this blog post and return ONLY valid JSON, no preamble, no markdown fences, \
                matching exactly this shape:
                {"hook": "one punchy sentence under 15 words", "excerpt": "2-3 sentences summarizing the post for a blog listing page", "tldr": ["bullet 1", "bullet 2", "bullet 3"]}

                Blog post:
                %s
                """.formatted(postContent);

        String rawJson = geminiClient.complete(prompt)
                .replace("```json", "")
                .replace("```", "")
                .trim();

        JsonNode parsed = null;
        try {
            parsed = objectMapper.readTree(rawJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String hook = parsed.path("hook").asText("");
        String excerpt = parsed.path("excerpt").asText("");

        List<String> tldr = new ArrayList<>();
        for (JsonNode item : parsed.path("tldr")) {
            tldr.add(item.asText());
        }

        return new PostSummary(hook, excerpt, tldr);
    }

    public record PostSummary(String hook, String excerpt, List<String> tldr) {}
}