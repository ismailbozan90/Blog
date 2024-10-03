package org.scamlet.blogmvc.blog.Controller;

import jakarta.validation.Valid;
import org.scamlet.blogmvc.blog.Entity.Comment;
import org.scamlet.blogmvc.blog.Entity.Post;
import org.scamlet.blogmvc.blog.Entity.User;
import org.scamlet.blogmvc.blog.Service.PostService;
import org.scamlet.blogmvc.blog.Service.UserService;
import org.scamlet.blogmvc.blog.Utility.CurrentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/post/{id}")
    public String singlePost(@PathVariable Long id, Model model) {
        Optional<Post> post = postService.findById(id);
        if (post.isEmpty()) {
            return "index";
        }
        if (post.get().getThumbnail() != null) {
            String base64Thumbnail = post.get().getBase64Thumbnail();
            model.addAttribute("base64Thumbnail", base64Thumbnail);
        }
        model.addAttribute("pageTitle", post.get().getTitle());
        model.addAttribute("post", post.get());
        model.addAttribute("comments", post.get().getComments());
        model.addAttribute("emptyComment", new Comment());
        return "/post/single-post";
    }

    @GetMapping("/post/create-post")
    public String createPost(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("pageTitle", "Create Post");
        return "/post/create-post";
    }

    @PostMapping("/post/createPost")
    public String createPost(@Valid @ModelAttribute("post") Post post, BindingResult bindingResult, @RequestParam("thumbnail") MultipartFile thumbnail, Model model, RedirectAttributes redirectAttributes) {
        // Layout title
        model.addAttribute("pageTitle", "Create Post");

        /// Check thumbnail image
        if (thumbnail.isEmpty()) {
            model.addAttribute("error", "Thumbnail is required.");
            return "/post/create-post";
        }

        // Set thumbnail
        try {
            post.setThumbnail(thumbnail.getBytes());
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
            return "/post/create-post";
        }

        // Check fields
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            model.addAttribute("error", bindingResult.getAllErrors());
            return "/post/create-post";
        }

        // Get username from session
        Authentication authentication = CurrentSession.getCurrentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("error", "User can not found!");
            return "/post/create-post";
        }

        // Set post user
        User tempUser = userService.getUserByUsername(authentication.getName());
        post.setOwner(tempUser);

        // Set post date
        post.setDate(new Date());

        postService.createPost(post);

        model.addAttribute("post", new Post());
        redirectAttributes.addFlashAttribute("success", "Post created successfully.");
        return "redirect:/post/create-post";
    }


}
