package com.isep.appli.controllers;

import com.isep.appli.dbModels.User;
import com.isep.appli.services.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {
    private NotificationService notificationService;

    NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notificationPage")
    public String notificationPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String checkUser = UserController.checkIsUser(user, model);
        if (!checkUser.equals("200")) {
            return checkUser;
        }
        model.addAttribute("user", user);

        model.addAttribute("notifications", notificationService.getAllFormattedNotificationByUser(user));

        return "user/notificationPage";
    }
}