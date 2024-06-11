package com.isep.appli.dbModels;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date postedDate;

    @Column(nullable = false)
    private Long likeCounter;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;


    @Column(nullable = false)
    private String userPseudo;

    @Column(nullable = false)
    private String texte;

    public Content() {
        this.likeCounter = 0L;
    }

    public void incrementLikeCounter() {
        this.likeCounter++;
    }
    public void decrementLikeCounter() {
        this.likeCounter--;
    }


}
