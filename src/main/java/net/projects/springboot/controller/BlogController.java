package net.projects.springboot.controller;

import net.projects.springboot.dto.CommentDto;
import net.projects.springboot.dto.PostDto;
import net.projects.springboot.service.PostService;
import net.projects.springboot.util.ROLE;
import net.projects.springboot.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BlogController {

    private PostService postService;

    public BlogController(PostService postService) {
        this.postService = postService;
    }



    // handler method to handle view post request
    @GetMapping("/post/{postUrl}")
    private String showPost(@PathVariable("postUrl") String postUrl,
                            Model model){
        PostDto post = postService.findPostByUrl(postUrl);

        CommentDto commentDto = new CommentDto();
        model.addAttribute("post", post);
        model.addAttribute("comment", commentDto);
        return "blog/blog_post";
    }



    @GetMapping("/page/search")
    public String searchPosts(@RequestParam(value = "query") String query,
                              @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                              @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                              Model model){

        Page<PostDto> page = postService.searchPosts(query, pageNo, pageSize);
        model.addAttribute("postsResponse", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());
        model.addAttribute("query", query);
        return "blog/view_posts";
    }
    @GetMapping("/admin/posts/search")
    public String searchAdminPosts(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            Model model) {

        String role = SecurityUtils.getRole();

        Page<PostDto> page;

        if (ROLE.ROLE_ADMIN.name().equals(role)) {
            page = query.isBlank()
                    ? postService.findAllPosts(pageNo, pageSize)
                    : postService.searchPosts(query, pageNo, pageSize);
        } else {
            page = query.isBlank()
                    ? postService.findPostsByUser(pageNo, pageSize)
                    : postService.searchPostsByUser(query, pageNo, pageSize);
        }

        model.addAttribute("posts", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());
        model.addAttribute("query", query);

        return "admin/posts";
    }
    @GetMapping("/")
    public String viewBlogPosts(
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            Model model) {

        Page<PostDto> page = postService.findAllPosts(pageNo, pageSize);

        System.out.println("Posts: " + page.getContent().size());
        System.out.println("Total elements: " + page.getTotalElements());
        System.out.println("Total pages: " + page.getTotalPages());

        model.addAttribute("postsResponse", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());

        return "blog/view_posts";
    }

}
