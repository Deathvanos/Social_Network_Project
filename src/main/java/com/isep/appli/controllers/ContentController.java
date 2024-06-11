package com.isep.appli.controllers;

import com.isep.appli.dbModels.Content;
import com.isep.appli.dbModels.User;
import com.isep.appli.services.ContentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.isep.appli.controllers.UserController.checkIsUser;

@Controller
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }



    // For @GetMapping("/main_user") in UserController
    public static void getMainContent(Model model, User user, ContentService contentService) {
        List<Map<String, Object>> userContents = contentService.getUserAndFriendContents(user);
        if (!userContents.isEmpty()) {
            model.addAttribute("contents", userContents);
        }
    }



    // For @GetMapping("/user-profile") in UserController
    public static void getAllUserContentWithParam(Model model, User user, ContentService contentService) {
        // for creating content
        model.addAttribute("content", new Content());
        // for viewing all content
        List<Map<String, Object>> userContents = contentService.getAllUserContentsWithLikes(user.getId());
        if (!userContents.isEmpty()) {
            model.addAttribute("contents", userContents);
        }
    }


    @PostMapping("/user-profile")
    public String checkLogin(@Valid Content content, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String checkUser = checkIsUser(user, model);
        if (!checkUser.equals("200")){return checkUser;}



        content.setUserPseudo(user.getUsername());
        content.setUser(user);

        content.getUser().getId();

        contentService.createContent(content);

        return "redirect:/user-profile";
    }

    @GetMapping("/add_like_{page}/{idContent}")
    public String addLike(@PathVariable Long idContent, @PathVariable String page, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String checkUser = UserController.checkIsUser(user, model);
        if (!checkUser.equals("200")) {
            return checkUser;
        }
        contentService.addLike(user.getId(), idContent);
        return "redirect:/"+page+"#" + idContent;
    }

    @GetMapping("/delete_content/{idContent}")
    public String deleteContent(@PathVariable Long idContent, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String checkUser = UserController.checkIsUser(user, model);
        if (!checkUser.equals("200")) {
            return checkUser;
        }
        // verification content belongs to user
        Content content = contentService.getById(idContent);
        if (Objects.equals(user.getId(), content.getUser().getId())) {
            contentService.destroyContent(idContent);
        }
        return "redirect:/user-profile#" + idContent;
    }





}
