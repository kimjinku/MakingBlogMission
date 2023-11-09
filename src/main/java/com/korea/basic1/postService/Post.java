package com.korea.basic1.postService;

import com.korea.basic1.note.Note;
import com.korea.basic1.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createDate;
    @ManyToOne
    private Note note;
    @ManyToOne
    private SiteUser author;
    private LocalDateTime modifyDate;

}
