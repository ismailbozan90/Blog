package org.scamlet.blogmvc.blog.Controller;

import org.scamlet.blogmvc.blog.Entity.Post;
import org.scamlet.blogmvc.blog.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CommonController {

    private final PostService postService;

    @Autowired
    public CommonController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        //List<Post> posts = postService.getPosts();
        Page<Post> posts = postService.findPage(page, size);
        for (Post post : posts) {
            if (post.getThumbnail() != null) {
                String base64Thumbnail = post.getBase64Thumbnail();
                model.addAttribute("base64Thumbnail_" + post.getId(), base64Thumbnail);
            }
        }
        model.addAttribute("posts", posts);
        model.addAttribute("pageTitle", "Home Page");
        return "index";
    }

    @GetMapping("/admin-panel")
    public String adminPanel() {
        return "/admin/admin-panel";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/errors/access-denied";
    }

}
