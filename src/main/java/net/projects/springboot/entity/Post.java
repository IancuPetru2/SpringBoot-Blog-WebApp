package net.projects.springboot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String url;

    @Column(nullable = false)
    private String user;
    private String email;

    @Lob
    @Column(nullable = false)
    private String content;
    private String shortDescription;

//    @CreationTimestamp
   @Column(name = "created_on")
    private LocalDateTime createdOn;

   // @UpdateTimestamp
   @Column(name = "updated_on")
    private LocalDateTime updatedOn;

//    @Column(name = "content_updated_on")
//    private LocalDateTime contentUpdatedOn;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private Set<Comment> comments = new HashSet<>();

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

    @Column(name = "ai_summary_locked", nullable = false)
    private boolean aiSummaryLocked = false;

    @Column(columnDefinition = "TEXT")
    private String aiResume;

    @Column(columnDefinition = "JSON")
    private String aiResumeJson;

    @Column(name = "ai_resume_generated_at")
    private Instant aiResumeGeneratedAt;
    @Column(name = "content_updated_on")
    private LocalDateTime contentUpdatedOn;
    public boolean isSummaryStale() {
        if (aiSummaryGeneratedAt == null) {
            return true; // no summary generated yet — treat as "needs publishing" once one exists
        }
        if (contentUpdatedOn == null) {
            return false;
        }
        Instant updatedInstant = contentUpdatedOn.atZone(ZoneId.systemDefault()).toInstant();
        return updatedInstant.isAfter(aiSummaryGeneratedAt);
    }

}
