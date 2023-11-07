package com.korea.basic1.note;

import com.korea.basic1.postService.Post;
import com.korea.basic1.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    private String title;
    @OneToMany(mappedBy = "note", cascade = CascadeType.REMOVE)
    private List<Post> posts;
    @ManyToOne
    private SiteUser author;
    @ManyToOne
    private Note parentNote;

    @OneToMany(mappedBy = "parentNote")  // 자식 노트 목록을 가리킴
    private List<Note> childNotes;

}
