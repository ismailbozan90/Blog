package org.scamlet.blogmvc.blog.Controller;

import jakarta.servlet.ServletContext;
import jakarta.validation.Valid;
import org.scamlet.blogmvc.blog.Entity.Post;
import org.scamlet.blogmvc.blog.Entity.User;
import org.scamlet.blogmvc.blog.Service.CommentService;
import org.scamlet.blogmvc.blog.Service.PostService;
import org.scamlet.blogmvc.blog.Service.UserService;
import org.scamlet.blogmvc.blog.Utility.CurrentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final CommentService commentService;
    private final PostService postService;

    @Autowired
    public UserController(UserService userService, CommentService commentService, PostService postService) {
        this.userService = userService;
        this.commentService = commentService;
        this.postService = postService;
    }

    @GetMapping("/login")
    public String login() {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return "redirect:/";
        }
        return "/auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return "redirect:/";
        }
        model.addAttribute("user", new User());
        return "/auth/register";
    }

    @PostMapping("/registerUser")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam String password_repeat, Model model, RedirectAttributes redirectAttributes) {
        if (userService.checkUserEmail(user.getEmail())) {
            model.addAttribute("error", "Email address already in use.");
            return "/auth/register";
        } else if (userService.checkUserName(user.getUserName())) {
            model.addAttribute("error", "User name already in use.");
            return "/auth/register";
        }

        if (!user.getPassword().equals(password_repeat)) {
            model.addAttribute("error", "Passwords do not match.");
            return "/auth/register";
        } else if (password_repeat.length() < 8 || password_repeat.length() > 16) {
            model.addAttribute("error", "Password must be between 8 and 16 characters.");
            return "/auth/register";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors());
            return "/auth/register";
        }

        userService.registerUser(user);
        model.addAttribute("user", new User());
        redirectAttributes.addFlashAttribute("success", "User registered successfully.");
        return "redirect:/register";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = CurrentSession.getCurrentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        User user = userService.getUserByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", user.getUserName() + " Profile");
        model.addAttribute("commentCount", commentService.getUserCommentCount(user.getId()));
        model.addAttribute("postCount", postService.countUserPosts(user.getId()));
        model.addAttribute("profilePicture", user.getProfilePicture());
        model.addAttribute("profileBackground", user.getProfileBackground());

        return "/profile/profile";
    }

    @GetMapping("/profile/edit-profile")
    public String editProfile(Model model) {
        Authentication authentication = CurrentSession.getCurrentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        User user = userService.getUserByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Edit "+ user.getUserName() + " Profile");
        model.addAttribute("profilePicture", user.getProfilePicture());
        model.addAttribute("profileBackground", user.getProfileBackground());

        return "/profile/edit-profile";
    }

    @PostMapping("/profile/changePassword")
    public String changePassword(@RequestParam("password") String password, @RequestParam("password_repeat") String password_repeat, @RequestParam("user_id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> tempUser = userService.getUserById(id);
        if (tempUser.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("pageTitle", "Edit "+ tempUser.get().getUserName() + " Profile");

        if (!password.equals(password_repeat)) {
            model.addAttribute("user", tempUser.get());
            model.addAttribute("profilePicture", tempUser.get().getProfilePicture());
            model.addAttribute("profileBackground", tempUser.get().getProfileBackground());
            model.addAttribute("passwordError", "Passwords do not match.");
            return "/profile/edit-profile";
        } else if (password_repeat.length() < 8 || password_repeat.length() > 16) {
            model.addAttribute("user", tempUser.get());
            model.addAttribute("profilePicture", tempUser.get().getProfilePicture());
            model.addAttribute("profileBackground", tempUser.get().getProfileBackground());
            model.addAttribute("passwordError", "Password must be between 8 and 16 characters.");
            return "/profile/edit-profile";
        }

        userService.changeUserPassword(tempUser.get(), password);

        model.addAttribute("user", tempUser.get());
        model.addAttribute("profilePicture", tempUser.get().getProfilePicture());
        model.addAttribute("profileBackground", tempUser.get().getProfileBackground());
        redirectAttributes.addFlashAttribute("passwordSuccess", "Password changed successfully.");
        return "redirect:/profile/edit-profile";
    }

    @PostMapping("/profile/changeProfilePicture")
    public String changeProfilePicture(@RequestParam("profile-picture") MultipartFile profilePicture, @RequestParam("user_id") Long id, Model model, RedirectAttributes redirectAttributes) {

        Optional<User> tempUser = userService.getUserById(id);
        if (tempUser.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("pageTitle", "Edit "+ tempUser.get().getUserName() + " Profile");

        /// Check profile picture
        if (profilePicture.isEmpty()) {
            model.addAttribute("user", tempUser.get());
            model.addAttribute("profilePicture", tempUser.get().getProfilePicture());
            model.addAttribute("profileBackground", tempUser.get().getProfileBackground());
            model.addAttribute("profilePictureError", "Profile picture is required.");
            return "/profile/edit-profile";
        }

        try {
            String fileName = System.currentTimeMillis() + "_" + profilePicture.getOriginalFilename();
            String filePath = new File("src/main/resources/static/img/profile-picture-img/").getAbsolutePath() + "/" + fileName;
            File dest = new File(filePath);
            profilePicture.transferTo(dest);
            tempUser.get().setProfilePicture("/img/profile-picture-img/" + fileName);
        } catch (IOException e) {
            model.addAttribute("user", tempUser.get());
            model.addAttribute("profilePicture", tempUser.get().getProfilePicture());
            model.addAttribute("profileBackground", tempUser.get().getProfileBackground());
            model.addAttribute("profilePictureError", "Failed to upload image: " + e.getMessage());
            return "/profile/edit-profile";
        }

        userService.updateUser(tempUser.get());

        model.addAttribute("user", tempUser.get());
        model.addAttribute("profilePicture", tempUser.get().getProfilePicture());
        model.addAttribute("profileBackground", tempUser.get().getProfileBackground());
        redirectAttributes.addFlashAttribute("profilePictureSuccess", "Profile picture changed successfully.");
        return "redirect:/profile/edit-profile";
    }

    @PostMapping("/profile/changeBackgroundPicture")
    public String changeBackgroundPicture(@RequestParam("background-picture") MultipartFile backgroundPicture, @RequestParam("user_id") Long id, Model model, RedirectAttributes redirectAttributes) {

        Optional<User> tempUser = userService.getUserById(id);
        if (tempUser.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("pageTitle", "Edit "+ tempUser.get().getUserName() + " Profile");

        if (backgroundPicture.isEmpty()) {
            model.addAttribute("user", tempUser.get());
            model.addAttribute("profilePicture", tempUser.get().getProfilePicture());
            model.addAttribute("profileBackground", tempUser.get().getProfileBackground());
            model.addAttribute("profilePictureError", "Background picture is required.");
            return "/profile/edit-profile";
        }

        try {
            String fileName = System.currentTimeMillis() + "_" + backgroundPicture.getOriginalFilename();
            String filePath = new File("src/main/resources/static/img/background-picture-img/").getAbsolutePath() + "/" + fileName;
            File dest = new File(filePath);
            backgroundPicture.transferTo(dest);
            tempUser.get().setProfileBackground("/img/background-picture-img/" + fileName);
        } catch (IOException e) {
            model.addAttribute("user", tempUser.get());
            model.addAttribute("profilePicture", tempUser.get().getProfilePicture());
            model.addAttribute("profileBackground", tempUser.get().getProfileBackground());
            model.addAttribute("profilePictureError", "Failed to upload image: " + e.getMessage());
            return "/profile/edit-profile";
        }

        userService.updateUser(tempUser.get());

        model.addAttribute("user", tempUser.get());
        model.addAttribute("profilePicture", tempUser.get().getProfilePicture());
        model.addAttribute("profileBackground", tempUser.get().getProfileBackground());
        redirectAttributes.addFlashAttribute("profilePictureSuccess", "Background picture changed successfully.");
        return "redirect:/profile/edit-profile";
    }

}
