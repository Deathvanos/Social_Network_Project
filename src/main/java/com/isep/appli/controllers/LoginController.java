package com.isep.appli.controllers;

import com.isep.appli.dbModels.User;
import com.isep.appli.services.EmailService;
import com.isep.appli.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class LoginController {

    private final UserService userService;
    private final EmailService emailService;

    LoginController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/subscription")
    public String subscriptionPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user!=null){return "redirect:/home";}
        model.addAttribute("user", new User());
        return "subscription";
    }

    @PostMapping("/subscription")
    public String subscription(@Valid User user, BindingResult result, Model model) {
        userService.signup(user);
        emailService.sendConfirmationEmail(user);
        return "redirect:/login";
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("id") long id) {
        userService.confirmEmail(id);
        return "redirect:/login";
    }

    @PostMapping("/checkUnique")
    public ResponseEntity<Map<String, Boolean>> checkUnique(@RequestBody Map<String, String> userInfo) {
        return ResponseEntity.ok(userService.checkUnique(userInfo));
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user!=null){return "redirect:/home";}
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String checkLogin(@Valid User user, BindingResult result, Model model, HttpSession session) {
        User userSignedIn = userService.login(user.getEmail(), user.getPassword());

        if (userSignedIn == null) {
            model.addAttribute("loginError", true);
            return "login";
        }

        session.setAttribute("user", userSignedIn);

        if (userSignedIn.getIsAdmin()) {
            return "redirect:/admin/home";
        }

        return "redirect:/main_user";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}
