package net.projects.springboot.entity;



import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class AiPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    // --- AI summary fields ---

    @Column(name = "ai_hook", length = 280)
    private String aiHook;

    @Column(name = "ai_excerpt", length = 1000)
    private String aiExcerpt;

    @ElementCollection
    @CollectionTable(name = "post_ai_tldr", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "line")
    @OrderColumn(name = "position")
    private List<String> aiTldr = new ArrayList<>();

    @Column(name = "ai_summary_generated_at")
    private Instant aiSummaryGeneratedAt;

    /** True once the author has manually edited a generated field.
     *  Used to decide whether to auto-regenerate on future edits. */
    @Column(name = "ai_summary_locked", nullable = false)
    private boolean aiSummaryLocked = false;

    // --- getters / setters ---

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAiHook() { return aiHook; }
    public void setAiHook(String aiHook) { this.aiHook = aiHook; }

    public String getAiExcerpt() { return aiExcerpt; }
    public void setAiExcerpt(String aiExcerpt) { this.aiExcerpt = aiExcerpt; }

    public List<String> getAiTldr() { return aiTldr; }
    public void setAiTldr(List<String> aiTldr) { this.aiTldr = aiTldr; }

    public Instant getAiSummaryGeneratedAt() { return aiSummaryGeneratedAt; }
    public void setAiSummaryGeneratedAt(Instant aiSummaryGeneratedAt) { this.aiSummaryGeneratedAt = aiSummaryGeneratedAt; }

    public boolean isAiSummaryLocked() { return aiSummaryLocked; }
    public void setAiSummaryLocked(boolean aiSummaryLocked) { this.aiSummaryLocked = aiSummaryLocked; }
}
