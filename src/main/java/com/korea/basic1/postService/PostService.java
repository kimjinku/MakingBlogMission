package com.korea.basic1.postService;

import com.korea.basic1.note.Note;
import com.korea.basic1.note.NoteRepository;
import com.korea.basic1.note.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final NoteRepository noteRepository;
    private final NoteService noteService;

    public List<Post> getPostList(){
        return postRepository.findAll();
    }

    public Post getPost(Long postId){
        Optional<Post> post = this.postRepository.findById(postId);
        if(post.isPresent()){
            return post.get();
        }else{
            throw new RuntimeException("post not found"); //Todo
        }
    }
    //Note note = noteRepository.findById(noteId).get();
    //        List<Post> postListForNote = note.getPosts();
    public List<Post> getPostInNote(Long noteId){
        return noteService.getNote(noteId).getPosts();
    }
    public void deletePost(Long postId){
        postRepository.delete(getPost(postId));
    }
    public Post addPost(Long noteId){
        Post post = new Post();
        Note note = noteService.getNote(noteId);
        post.setTitle("newTitle");
        post.setContent("");
        post.setCreateDate(LocalDateTime.now());
        post.setNote(note);
        postRepository.save(post);
        return post;
    }
    public Page<Post> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.postRepository.findAll(pageable);
    }
    public Page<Post> getPageListByCreateDate(int page,Note note) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.postRepository.findAllByNote(note,pageable);
    }
    public Page<Post> getPageListByModifyDate(int page,Note note) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("modifyDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.postRepository.findAllByNote(note,pageable);
    }
    public Page<Post> getPageListByTitle(int page,Note note) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("title"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.postRepository.findAllByNote(note,pageable);
    }
}
