package com.isep.appli.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FormattedDiscussion {
    private Long userId;
    private String displayUser;
    private String lastMessageDate;
}