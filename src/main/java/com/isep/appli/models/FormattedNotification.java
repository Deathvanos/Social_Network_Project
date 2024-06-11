package com.isep.appli.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FormattedNotification {
    private Long id;
    private String title;
    private String date;
    private String link;
    private boolean readStatus;
}