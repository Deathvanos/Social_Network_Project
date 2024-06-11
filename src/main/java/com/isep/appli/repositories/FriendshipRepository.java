package com.isep.appli.repositories;

import com.isep.appli.dbModels.Friendship;
import com.isep.appli.dbModels.FriendshipStatus;
import com.isep.appli.dbModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUser(User user);
    Friendship findByUserAndFriend(User user, User friend);
    List<Friendship> findByFriendAndStatus(User friend, FriendshipStatus status);

    List<Friendship> findByUserAndStatus(User currentUser, FriendshipStatus friendshipStatus);

    List<Friendship> findByUserId(Long userId);
}
