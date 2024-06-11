package com.isep.appli.services;

import com.isep.appli.dbModels.Friendship;
import com.isep.appli.repositories.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public List<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }
}
