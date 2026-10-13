package net.projects.springboot.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    @NotEmpty(message = "Post title should not be empty")
    private String title;
    private String url;
    private String user;
    private String email;
    @NotEmpty(message = "Post content should not be empty")
    private String content;
    @NotEmpty(message = "Post short description should be empty")
    private String shortDescription;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private LocalDateTime contentUpdatedOn;
    private Set<CommentDto> comments;
    public record PostSummaryResponse(String hook, String excerpt, List<String> tldr) {}
    // AI Summary fields
    private String aiHook;
    private String aiExcerpt;
    private Instant aiSummaryGeneratedAt;
    private boolean aiSummaryLocked;
    private List<String> aiTldr;

    public boolean isSummaryStale() {
        if (aiSummaryGeneratedAt == null) {
            return true;
        }
        if ( contentUpdatedOn== null) {
            return false;
        }
        Instant updatedInstant = contentUpdatedOn.atZone(ZoneId.systemDefault()).toInstant();
        return updatedInstant.isAfter(aiSummaryGeneratedAt);
    }

}
