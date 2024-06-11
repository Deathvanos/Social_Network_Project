package com.isep.appli.services;

import com.isep.appli.dbModels.Message;
import com.isep.appli.dbModels.User;
import com.isep.appli.models.FormattedDiscussion;
import com.isep.appli.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;

    MessageService(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    public Optional<Message> getById(Long id) {
        return messageRepository.findById(id);
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public String displayDestination(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    public String formatDate(Date date) {
        if (date == null) {
            return " ";
        }
        Date now = new Date();
        SimpleDateFormat simpleDateFormat;
        if (now.getDate() == date.getDate() && now.getMonth() == date.getMonth() && now.getYear() == date.getYear()) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        }
        else {
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        }
        return simpleDateFormat.format(date);
    }

    public List<Message> getAllMessagesByUserId(User user) {
        List<Message> messages = messageRepository.findBySenderId(user.getId());
        List<Message> otherMessages = messageRepository.findByDestinationId(user.getId());
        messages.addAll(otherMessages);
        return messages;
    }

    public Long getDiscussionUser(Message message, User selectedUser) {
        Long discussionUser;
        if (message.getSenderId().equals(selectedUser.getId())) {
            discussionUser = message.getDestinationId();
        }
        else {
            discussionUser = message.getSenderId();
        }
        return discussionUser;
    }

    public List<User> getDiscussionUserList(User selectedUser) {
        List<Message> messages = getAllMessagesByUserId(selectedUser);
        List<User> discussionUserList = new ArrayList();
        for (Message message : messages) {
            Long discussionUser = getDiscussionUser(message, selectedUser);
            boolean isInDiscussionUserList = false;
            for (User user : discussionUserList) {
                if (user.getId().equals(discussionUser)) {
                    isInDiscussionUserList = true;
                }
            }
            if (!isInDiscussionUserList) {
                discussionUserList.add(userService.getUserById(discussionUser));
            }
        }
        return discussionUserList;
    }

    public List<Message> getAllMessagesByDiscussionUsersId(Long firstUserId, Long secondUserId) {
        List<Message> messages = messageRepository.findBySenderIdAndDestinationId(firstUserId, secondUserId);
        List<Message> otherMessages = messageRepository.findBySenderIdAndDestinationId(secondUserId, firstUserId);
        messages.addAll(otherMessages);
        Collections.sort(messages, Comparator.comparing(Message::getDate));
        return messages;
    }

    public Date getLastMessageDate(Long firstUserId, Long secondUserId) {
        List<Message> messages = getAllMessagesByDiscussionUsersId(firstUserId, secondUserId);
        if (messages.isEmpty()) {
            return null;
        }
        Date maxDate = messages.get(0).getDate();
        for (Message message : messages) {
            if (message.getDate().after(maxDate)) {
                maxDate = message.getDate();
            }
        }
        return maxDate;
    }

    public FormattedDiscussion getFormattedDiscussionUser(User user, User discussionUser) {
        FormattedDiscussion formattedDiscussion = new FormattedDiscussion(
                discussionUser.getId(),
                displayDestination(discussionUser),
                formatDate(getLastMessageDate(user.getId(), discussionUser.getId()))
        );
        return formattedDiscussion;
    }

    public List<FormattedDiscussion> getFormattedDiscussionList(User selectedUser) {
        List<User> discussionUsers = getDiscussionUserList(selectedUser);
        List<FormattedDiscussion> formattedDiscussionList = new ArrayList<>();
        for (User user : discussionUsers) {
            FormattedDiscussion formattedDiscussion = getFormattedDiscussionUser(selectedUser, user);
            formattedDiscussionList.add(formattedDiscussion);
        }
        Collections.sort(formattedDiscussionList, Comparator.comparing(FormattedDiscussion::getLastMessageDate));
        return formattedDiscussionList;
    }
}