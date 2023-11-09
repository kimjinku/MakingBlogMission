package com.korea.basic1.postService;

import com.korea.basic1.note.Note;
import com.korea.basic1.postService.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findAllByNote(Note note, Pageable pageable);

}
