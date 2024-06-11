package com.isep.appli.controllers;

import com.isep.appli.dbModels.*;
import com.isep.appli.services.MessageService;
import com.isep.appli.services.NotificationService;
import com.isep.appli.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Date;
import java.util.List;

@Controller
public class ChatController {
    private final MessageService messageService;
    private final UserService userService;
    private final NotificationService notificationService;

    ChatController(MessageService messageService, UserService userService, NotificationService notificationService) {
        this.messageService = messageService;
        this.userService= userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/chatPage")
    public String chatPageWithoutdiscussionSelected() {
        return "redirect:/chatPage/0";
    }

    @GetMapping("/chatPage/{discussionUserId}")
    public String chatPage(@PathVariable Long discussionUserId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String checkUser = UserController.checkIsUser(user, model);
        if (!checkUser.equals("200")){return checkUser;}

        model.addAttribute("user", user);
        model.addAttribute("discussionUsers", messageService.getFormattedDiscussionList(user));

        Message message = new Message();
        message.setSenderId(user.getId());

        if (discussionUserId != 0) {
            User discussionUser = userService.getUserById(discussionUserId);
            model.addAttribute("discussionUser", messageService.getFormattedDiscussionUser(user, discussionUser));

            List<Message> messages = messageService.getAllMessagesByDiscussionUsersId(user.getId(), discussionUserId);
            model.addAttribute("messages", messages);

            message.setDestinationId(discussionUserId);
        }

        model.addAttribute("message", message);

        return "user/chatPage";
    }

    @PostMapping("sendMessage")
    public String sendMessage(@Valid Message message) {
        message.setDate(new Date());
        messageService.save(message);

        Notification notification = new Notification();
        notification.setUserId(message.getDestinationId());
        notification.setUserFrom(message.getSenderId());
        notification.setType("MESSAGE");
        notification.setDate(new Date());
        notification.setReadStatus(false);
        notificationService.save(notification);

        Long discussionUserId = message.getDestinationId();
        return "redirect:/chatPage/" + discussionUserId;
    }
}