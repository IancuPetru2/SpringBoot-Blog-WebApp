package net.projects.springboot.mapper;

import net.projects.springboot.dto.PostDto;
import net.projects.springboot.entity.Post;

import java.util.stream.Collectors;

public class PostMapper {

    // map Post entity to PostDto
    public static PostDto mapToPostDto(Post post){
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .url(post.getUrl())
                .user(post.getCreatedBy() != null ? post.getCreatedBy().getName() : post.getUser())
                .email(post.getCreatedBy() != null ? post.getCreatedBy().getEmail() : post.getEmail())
                .content(post.getContent())
                .shortDescription(post.getShortDescription())
                .createdOn(post.getCreatedOn())
                .updatedOn(post.getUpdatedOn())
                .comments(post.getComments().stream()
                        .map((comment) -> CommentMapper.mapToCommentDto(comment))
                        .collect(Collectors.toSet())
                )
                . aiHook(post.getAiHook())
                .aiExcerpt(post.getAiExcerpt())
                .aiTldr(post.getAiTldr())
                .aiSummaryGeneratedAt(post.getAiSummaryGeneratedAt())
                .aiSummaryLocked(post.isAiSummaryLocked())
                .contentUpdatedOn(post.getContentUpdatedOn())
                .build();
    }

    // map Postdto to Post entity
    public static Post mapToPost(PostDto postDto){
        return Post.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .user(postDto.getUser())
                .email(postDto.getEmail())
                .content(postDto.getContent())
                .url(postDto.getUrl())
                .shortDescription(postDto.getShortDescription())
                .createdOn(postDto.getCreatedOn())
                .updatedOn(postDto.getUpdatedOn())
                .build();
    }
}
