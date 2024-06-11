package com.isep.appli.services;

import com.isep.appli.dbModels.Notification;
import com.isep.appli.dbModels.User;
import com.isep.appli.models.FormattedNotification;
import com.isep.appli.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class NotificationService {
    private NotificationRepository notificationRepository;
    private MessageService messageService;
    private UserService userService;

    NotificationService(NotificationRepository notificationRepository, MessageService messageService, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.messageService = messageService;
        this.userService = userService;
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotificationsByUser(User user) {
        return notificationRepository.findByUserId(user.getId());
    }

    public String getNotificationTitle(Notification notification) {
        String title;
        String userFrom = messageService.displayDestination(userService.getUserById(notification.getUserFrom()));
        switch (notification.getType()) {
            case "MESSAGE":
                title = "You have received a message from " + userFrom + ".";
                break;
            case "FRIEND_REQUEST":
                title = "You have received a friend request from " + userFrom + ".";
                break;
            case "FRIEND_REQUEST_ACCEPTANCE":
                title = userFrom + " has accepted your friend request.";
                break;
            case "CONTENT_SHARED_BY_A_FRIEND":
                title = userFrom + " has shared a publication.";
                break;
            default:
                title = "You have received a notification.";
        }
        return title;
    }

    public String getLink(Notification notification) {
        String link;
        switch (notification.getType()) {
            case "MESSAGE":
                link = "chatPage/" + notification.getUserFrom();
                break;
            default:
                link = "";
        }
        return link;
    }

    public List<FormattedNotification> getAllFormattedNotificationByUser(User user) {
        List<Notification> notifications = getAllNotificationsByUser(user);
        List<FormattedNotification> formattedNotificationList = new ArrayList<>();
        for (Notification notification : notifications) {
            FormattedNotification formattedNotification = new FormattedNotification(
                    notification.getId(),
                    getNotificationTitle(notification),
                    messageService.formatDate(notification.getDate()),
                    getLink(notification),
                    notification.getReadStatus()
            );
            formattedNotificationList.add(formattedNotification);
            if (!notification.getReadStatus()) {
                notification.setReadStatus(true);
                save(notification);
            }
        }
        Collections.sort(formattedNotificationList, Comparator.comparing(FormattedNotification::getDate).reversed());
        return formattedNotificationList;
    }

    public int getNumberOfUnreadNotifications(User user) {
        return notificationRepository.countByUserIdAndReadStatus(user.getId(), false);
    }
}
