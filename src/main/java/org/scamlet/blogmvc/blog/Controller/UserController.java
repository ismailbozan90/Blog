package org.scamlet.blogmvc.blog.Controller;

import jakarta.validation.Valid;
import org.scamlet.blogmvc.blog.Entity.User;
import org.scamlet.blogmvc.blog.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        return "redirect:/auth/register";
    }
}
