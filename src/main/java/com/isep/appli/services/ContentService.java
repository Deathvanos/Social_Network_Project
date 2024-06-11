package com.isep.appli.services;


import com.isep.appli.dbModels.*;
import com.isep.appli.repositories.ContentRepository;
import com.isep.appli.repositories.FriendshipRepository;
import com.isep.appli.repositories.UserLikeRepository;
import com.isep.appli.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserLikeRepository userLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;




    public Content getById(Long contentId) {return contentRepository.findById(contentId).orElse(null);}



    public List<Map<String, Object>> getUserAndFriendContents(User currentUser) {
        List<User> friends = new ArrayList<>(friendshipRepository.findByUserAndStatus(currentUser, FriendshipStatus.ACCEPTED)
                .stream()
                .map(Friendship::getFriend)
                .toList());

        // Ajouter le currentUser à la liste
        friends.add(currentUser);

        List<Content> userAndFriendContent = contentRepository.findAllByUserInOrderByPostedDateDesc(friends);
        return addLikeButtonForContents(userAndFriendContent, currentUser.getId());
    }


    public List<Map<String, Object>> getAllUserContentsWithLikes(Long userId) {
        List<Content> contents = contentRepository.findAllByUserIdOrderByPostedDateDesc(userId);
        return addLikeButtonForContents(contents, userId);
    }


    private List<Map<String, Object>> addLikeButtonForContents(List<Content> contents, Long userId) {
        List<Map<String, Object>> contentsWithLikes = new ArrayList<>();

        for (Content content : contents) {
            Map<String, Object> contentWithLikes = new HashMap<>();
            contentWithLikes.put("content", content);

            UserLike userLike = userLikeRepository.findByUserIdAndContentId(userId, content.getId());
            contentWithLikes.put("liked", userLike != null);

            contentsWithLikes.add(contentWithLikes);
        }

        return contentsWithLikes;
    }



    public void createContent(Content newContent) {
        contentRepository.save(newContent);
    }


    public void addLike(Long userId, Long contentId) {
        // add like
        UserLike userLike = userLikeRepository.findByUserIdAndContentId(userId, contentId);
        if (userLike == null) {
            Content content = contentRepository.findById(contentId).orElseThrow(() -> new RuntimeException("Content not found"));
            content.incrementLikeCounter();
            contentRepository.save(content);

            UserLike newUserLike = new UserLike();
            newUserLike.setUser(userRepository.getReferenceById(userId));
            newUserLike.setContent(contentRepository.getReferenceById(contentId));
            userLikeRepository.save(newUserLike);
        }
        // remove like
        else {
            Content content = contentRepository.findById(contentId).orElseThrow(() -> new RuntimeException("Content not found"));
            content.decrementLikeCounter();
            contentRepository.save(content);
            userLikeRepository.delete(userLike);
        }
    }


    public void destroyContent(Long contentId) {
        // Supprime d'abord les "likes" associés au contenu
        List<UserLike> userLikes = userLikeRepository.findAllByContentId(contentId);
        userLikeRepository.deleteAll(userLikes);
        // Ensuite, supprime le contenu
        contentRepository.deleteById(contentId);
    }
}
