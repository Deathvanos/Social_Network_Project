package com.isep.appli.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FormattedMessage {
    private Long id;
    private String content;
    private String displaySender;
    private String date;
    private boolean received;
}