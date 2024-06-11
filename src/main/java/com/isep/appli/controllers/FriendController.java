package com.isep.appli.controllers;

import com.isep.appli.dbModels.Friendship;
import com.isep.appli.dbModels.Notification;
import com.isep.appli.dbModels.User;
import com.isep.appli.services.NotificationService;
import com.isep.appli.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Date;
import java.util.List;

@Controller
public class FriendController {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/friend")
    public String friendPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String checkUser = UserController.checkIsUser(user, model);
        if (!checkUser.equals("200")) {
            return checkUser;
        }

        List<User> friends = userService.getFriends(user);
        List<User> allUsers = userService.getAllUsersExceptCurrentAndFriends(user);
        List<Friendship> pendingRequests = userService.getPendingFriendRequests(user);

        model.addAttribute("friends", friends);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("pendingRequests", pendingRequests);

        return "user/friend";
    }

    @PostMapping("/sendFriendRequest")
    public String sendFriendRequest(@RequestParam Long friendId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        User friend = userService.getUserById(friendId);

        if (user != null && friend != null) {
            userService.sendFriendRequest(user, friend);
        }

        Notification notification = new Notification();
        notification.setUserId(friend.getId());
        notification.setUserFrom(user.getId());
        notification.setType("FRIEND_REQUEST");
        notification.setDate(new Date());
        notification.setReadStatus(false);
        notificationService.save(notification);

        return "redirect:/friend";
    }

    @PostMapping("/acceptFriendRequest")
    public String acceptFriendRequest(@RequestParam Long friendId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        User friend = userService.getUserById(friendId);

        if (user != null && friend != null) {
            userService.acceptFriendRequest(user, friend);
        }

        Notification notification = new Notification();
        notification.setUserId(friend.getId());
        notification.setUserFrom(user.getId());
        notification.setType("FRIEND_REQUEST_ACCEPTANCE");
        notification.setDate(new Date());
        notification.setReadStatus(false);
        notificationService.save(notification);

        return "redirect:/friend";
    }

    @PostMapping("/declineFriendRequest")
    public String declineFriendRequest(@RequestParam Long friendId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        User friend = userService.getUserById(friendId);

        if (user != null && friend != null) {
            userService.declineFriendRequest(user, friend);
        }

        return "redirect:/friend";
    }

    @PostMapping("/removeFriend")
    public String deleteFriendRequest(@RequestParam Long friendId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        User friend = userService.getUserById(friendId);

        if (user != null && friend != null) {
            System.out.println(friendId);
            userService.removeFriend(user, friend);
            userService.removeFriend(friend, user);
        }

        return "redirect:/friend";
    }
}

