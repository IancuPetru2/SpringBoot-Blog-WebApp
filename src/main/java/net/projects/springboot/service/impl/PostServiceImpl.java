package net.projects.springboot.service.impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.projects.springboot.blog.ai.SummaryService;
import net.projects.springboot.dto.PostDto;
import net.projects.springboot.entity.Post;
import net.projects.springboot.entity.User;
import net.projects.springboot.mapper.PostMapper;
import net.projects.springboot.repository.PostRepository;
import net.projects.springboot.repository.UserRepository;
import net.projects.springboot.service.PostService;
import net.projects.springboot.util.SecurityUtils;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import net.projects.springboot.dto.PostSummaryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
//import net.projects.springboot.blog.ai.ResumeService;


@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private SummaryService summaryService;



    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           SummaryService summaryService)

                           {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.summaryService = summaryService;

   }


    @Override
    public List<PostDto> findAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> findPostsByUser() {
        String email = SecurityUtils.getCurrentUser().getUsername();
        User createdBy = userRepository.findByEmail(email);
        Long userId = createdBy.getId();
        List<Post> posts = postRepository.findPostsByUser(userId);
        return posts.stream()
                .map((post) -> PostMapper.mapToPostDto(post))
                .collect(Collectors.toList());
    }


    @Override
    public Page<PostDto> findPostsByUser(int pageNo, int pageSize) {

        String email = SecurityUtils.getCurrentUser().getUsername();

        User createdBy = userRepository.findByEmail(email);

        Long userId = createdBy.getId();

        Pageable pageable = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by("createdOn").descending()
        );

        Page<Post> posts = postRepository.findPostsByUser(userId, pageable);

        return posts.map(PostMapper::mapToPostDto);
    }
    @Override
    public void createPost(PostDto postDto) {
        String email = SecurityUtils.getCurrentUser().getUsername();
        User user = userRepository.findByEmail(email);
        Post post = PostMapper.mapToPost(postDto);
        post.setCreatedBy(user);
        post.setUser(user.getName());
        post.setEmail(user.getEmail());
        postRepository.save(post);
    }

    @Override
    public PostDto findPostById(Long postId) {
        Post post = postRepository.findById(postId).get();
        return PostMapper.mapToPostDto(post);
    }

//    @Override
//    public void updatePost(PostDto postDto) {
//        String email = SecurityUtils.getCurrentUser().getUsername();
//        User createdBy = userRepository.findByEmail(email);
//        Post post = PostMapper.mapToPost(postDto);
//        post.setCreatedBy(createdBy);
//        post.setUser(createdBy.getName());
//        post.setEmail(createdBy.getEmail());
//
//        // Preserve AI summary fields — mapToPost() rebuilds a fresh entity
//        // that doesn't know about them, so pull them from the existing row.
//        postRepository.findById(postDto.getId()).ifPresent(existing -> {
//            post.setAiHook(existing.getAiHook());
//            post.setAiExcerpt(existing.getAiExcerpt());
//            post.setAiTldr(existing.getAiTldr());
//            post.setAiSummaryGeneratedAt(existing.getAiSummaryGeneratedAt());
//            post.setAiSummaryLocked(existing.isAiSummaryLocked());
//        });
//           postRepository.findById(postDto.getId())
//                    .orElseThrow(() ->{ new IllegalArgumentException("Post not found: " + postDto.getId());
//
//            post.setTitle(postDto.getTitle());
//            post.setShortDescription(postDto.getShortDescription());
//            post.setContent(postDto.getContent());
//        });
//
//        postRepository.save(post);
//    }


    @Override
    public void updatePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postDto.getId()));

        post.setTitle(postDto.getTitle());
        post.setShortDescription(postDto.getShortDescription());
        post.setContent(postDto.getContent());
        post.setContentUpdatedOn(LocalDateTime.now());
        postRepository.save(post);
    }
    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public PostDto findPostByUrl(String postUrl) {
        Post post = postRepository.findByUrl(postUrl).get();
        return PostMapper.mapToPostDto(post);
    }

    @Override
    public List<PostDto> searchPosts(String query) {
        List<Post> posts = postRepository.searchPosts(query);
        return posts.stream()
                .map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    }
    @Override
    public Page<PostDto> searchPosts(String query, int pageNo, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdOn").descending());
        Page<Post> posts = postRepository.searchPosts(query, pageable);
        return posts.map(PostMapper::mapToPostDto);
    }


    @Override
    public PostSummaryResponse generateSummary(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        String currentEmail = SecurityUtils.getCurrentUser().getUsername();
        User currentUser = userRepository.findByEmail(currentEmail);
        boolean isAdmin = SecurityUtils.getCurrentUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            if (!post.getCreatedBy().getEmail().equals(currentEmail)) {
                throw new AccessDeniedException("You can only generate summaries for your own posts");
            }
            if (post.getAiHook() != null) {
                throw new IllegalStateException("Summary already generated for this post.");
            }
            int maxSummariesPerGuest = 8;
            if (currentUser.getSummaryGenerationCount() >= maxSummariesPerGuest) {
                throw new IllegalStateException("You've used all 3 of your free AI summary generations. Contact an admin if you need more.");
            }
        }

        var summary = summaryService.generate(post.getContent());

        post.setAiHook(summary.hook());
        post.setAiExcerpt(summary.excerpt());
        post.getAiTldr().clear();
        post.getAiTldr().addAll(summary.tldr());
        post.setAiSummaryGeneratedAt(Instant.now());
        post.setAiSummaryLocked(false);
        postRepository.save(post);

        if (!isAdmin) {
            currentUser.setSummaryGenerationCount(currentUser.getSummaryGenerationCount() + 1);
            userRepository.save(currentUser);
        }

        return new PostSummaryResponse(summary.hook(), summary.excerpt(), summary.tldr());
    }


    @Override
    public Page<PostDto> searchPostsByUser(
            String query, int pageNo, int pageSize) {

        String email = SecurityUtils.getCurrentUser().getUsername();

        User user = userRepository.findByEmail(email);

        Pageable pageable = PageRequest.of(
                pageNo,
                pageSize,
                Sort.by("createdOn").descending()
        );

        Page<Post> posts = postRepository.searchPostsByUser(
                user.getId(),
                query,
                pageable
        );

        return posts.map(PostMapper::mapToPostDto);
    }
    @Override
    public Page<PostDto> findAllPosts(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdOn").descending());
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostMapper::mapToPostDto);
    }
@Override
@Transactional
public void setSummaryLocked(Long postId, boolean locked) {
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
    String currentEmail = SecurityUtils.getCurrentUser().getUsername();
    boolean isAdmin = SecurityUtils.getCurrentUser().getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    if (!isAdmin && !post.getCreatedBy().getEmail().equals(currentEmail)) {
        throw new AccessDeniedException("You can only generate summaries for your own posts");
    }
    post.setAiSummaryLocked(locked);
    postRepository.save(post);
}
}
