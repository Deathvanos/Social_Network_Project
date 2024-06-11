package com.isep.appli.repositories;

import com.isep.appli.dbModels.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    int countByUserIdAndReadStatus(Long userId, boolean readStatus);
}
