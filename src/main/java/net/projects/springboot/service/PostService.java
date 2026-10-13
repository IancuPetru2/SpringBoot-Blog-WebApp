package net.projects.springboot.service;
//import net.projects.springboot.blog.ai.ResumeResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.projects.springboot.dto.PostDto;
import net.projects.springboot.dto.PostSummaryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    List<PostDto> findAllPosts();

    List<PostDto> findPostsByUser();
Page<PostDto> findPostsByUser(int pageNo, int pageSize);

    void createPost(PostDto postDto);

    PostDto findPostById(Long postId);

    void updatePost(PostDto postDto);

    void deletePost(Long postId);

    PostDto findPostByUrl(String postUrl);

    List<PostDto> searchPosts(String query);
    Page<PostDto> findAllPosts(int pageNo, int pageSize);
    Page<PostDto> searchPosts(String query, int pageNo, int pageSize);
    PostSummaryResponse generateSummary(Long postId);
    void setSummaryLocked(Long postId, boolean locked);
    Page<PostDto> searchPostsByUser(String query, int pageNo, int pageSize);
}