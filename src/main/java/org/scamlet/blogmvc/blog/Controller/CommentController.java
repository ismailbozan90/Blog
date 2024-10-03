package org.scamlet.blogmvc.blog.Controller;

import jakarta.validation.Valid;
import org.scamlet.blogmvc.blog.Entity.Comment;
import org.scamlet.blogmvc.blog.Entity.Post;
import org.scamlet.blogmvc.blog.Entity.User;
import org.scamlet.blogmvc.blog.Service.CommentService;
import org.scamlet.blogmvc.blog.Service.PostService;
import org.scamlet.blogmvc.blog.Service.UserService;
import org.scamlet.blogmvc.blog.Utility.CurrentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Controller
public class CommentController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public CommentController(UserService userService, PostService postService, CommentService commentService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
    }


    @PostMapping("/comment/sendComment")
    public String createPost(@RequestParam("post-id") Long id, @RequestParam("message") String comment, Model model, RedirectAttributes redirectAttributes) {
        Optional<Post> post = postService.findById(id);
        if (post.isEmpty()) {
            return "index";
        }

        // Layout title
        model.addAttribute("pageTitle", post.get().getTitle());

        if (comment.length() < 4 || comment.length() > 100) {
            model.addAttribute("error", "Comment length must be between 4 and 100 characters");
            return "redirect:/post/" + post.get().getId();
        }

        // Get username from session
        Authentication authentication = CurrentSession.getCurrentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("error", "User can not found!");
            return "redirect:/post/" + post.get().getId();
        }

        // Set comment user
        User tempUser = userService.getUserByUsername(authentication.getName());

        // Create comment
        Comment tempComment = new Comment();
        tempComment.setOwner(tempUser);
        tempComment.setPost(post.get());
        tempComment.setComment(comment);
        tempComment.setDate(new Date());

        commentService.saveComment(tempComment);

        if (post.get().getThumbnail() != null) {
            String base64Thumbnail = post.get().getBase64Thumbnail();
            model.addAttribute("base64Thumbnail", base64Thumbnail);
        }
        model.addAttribute("post", post.get());
        model.addAttribute("comments", post.get().getComments());
        model.addAttribute("emptyComment", new Comment());
        redirectAttributes.addFlashAttribute("success", "Comment created successfully.");
        return "redirect:/post/" + post.get().getId();
    }

}
