package com.isep.appli.repositories;


import com.isep.appli.dbModels.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    UserLike findByUserIdAndContentId(Long userId, Long contentId);

    List<UserLike> findAllByContentId(Long contentId);
}
