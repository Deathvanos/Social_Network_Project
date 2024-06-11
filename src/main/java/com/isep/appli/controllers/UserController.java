package com.isep.appli.controllers;

import com.isep.appli.dbModels.Content;
import com.isep.appli.dbModels.Message;
import com.isep.appli.dbModels.User;
import com.isep.appli.models.*;
import com.isep.appli.services.ContentService;
import com.isep.appli.services.EmailService;
import com.isep.appli.services.NotificationService;
import com.isep.appli.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private ContentService contentService;



	static public String checkIsUser(User user, Model model) {
		if (user==null) {return "errors/error-401";}
		model.addAttribute("user", user);
		return "200";
	}


	@GetMapping("/main_user")
	public String userHomePage(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		String checkUser = checkIsUser(user, model);
		if (!checkUser.equals("200")){return checkUser;}

		ContentController.getMainContent(model, user, contentService);

		model.addAttribute("user", user);
		model.addAttribute("numberOfUnreadNotifications", notificationService.getNumberOfUnreadNotifications(user));

		return "user/mainUser";
	}

	@GetMapping("/user/{userId}")
	public String userPage(@PathVariable Long userId, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		String checkUser = checkIsUser(user, model);
		if (!checkUser.equals("200")){return checkUser;}

		User displayedUser = userService.getUserById(userId);
		model.addAttribute("displayedUser", displayedUser);

		Message newMessage = new Message();
		newMessage.setSenderId(user.getId());
		newMessage.setDestinationId(displayedUser.getId());
		model.addAttribute("newMessage", newMessage);

		ContentController.getAllUserContentWithParam(model, displayedUser, contentService);

		return "user/userPage";
	}


	@GetMapping("/user-profile")
	public String checkLogin(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		String checkUser = checkIsUser(user, model);
		if (!checkUser.equals("200")){return checkUser;}

		ContentController.getAllUserContentWithParam(model, user, contentService);

		model.addAttribute("user", user);
		return "user/user-profile";
	}

	@PostMapping("/modify-user")
	public String updateUser(@ModelAttribute ModifyUserInfoForm newUserInfo, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		Map<String, String> checkUniqueParameters = new HashMap<>();
		if (!newUserInfo.getUsername().isEmpty()) {
			checkUniqueParameters.put("username", newUserInfo.getUsername());
		}
		if (!newUserInfo.getEmail().isEmpty()) {
			checkUniqueParameters.put("email", newUserInfo.getEmail());
		}

		userService.modifyUserInfo(user, newUserInfo);
		if (newUserInfo.getEmail() != null && !newUserInfo.getEmail().isEmpty()) {
			emailService.sendConfirmationEmail(user);
		}
		return "redirect:/login";
	}

	@GetMapping("/searchUser")
	public String searchUserPage(Model model, HttpSession session) {
		return "user/searchUserPage";
	}

	@PostMapping("/searchUser")
	public String searchUser(@RequestParam ("userRequest") String userRequest, Model model) {
		List<User> userList = userService.getAllUsersByStringName(userRequest);
		model.addAttribute("userList", userList);
		return "user/searchUserPage";
	}
}
