package com.isep.appli.repositories;

import com.isep.appli.dbModels.Content;
import com.isep.appli.dbModels.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

	Optional<Content> findById(Long id);


    List<Content> findAllByUserIdOrderByPostedDateDesc(Long userId);

    List<Content> findByUserId(Long userId);

    List<Content> findAllByUserInOrderByPostedDateDesc(List<User> friends);
}
