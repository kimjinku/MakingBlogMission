package com.korea.basic1.note;

import com.korea.basic1.postService.Post;
import com.korea.basic1.postService.PostController;
import com.korea.basic1.postService.PostRepository;
import com.korea.basic1.postService.PostService;
import com.korea.basic1.user.SiteUser;
import com.korea.basic1.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class NoteController {
    @Autowired
    PostRepository postRepository;
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    PostController postController;
    @Autowired
    NoteService noteService;
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;

    @RequestMapping("/note")
    public String main(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword, Pageable pageable, @RequestParam(value = "page", defaultValue = "0") int page) {
        pageable = PageRequest.of(page, 10);
        Page<Post> postList = postRepository.findAll(pageable);
        List<Note> noteList = noteService.getParentNoteList();
        //List<Post> postListForNote = noteList.get(0).getPosts();
        Page<Post> postListForNote = postService.getPageListByCreateDate(page,noteList.get(0));
        if (keyword != null && !keyword.isEmpty()) {
            Page<Post> searchResults = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
            model.addAttribute("searchResults", searchResults);
        } else {
            model.addAttribute("searchResults", Collections.emptyList()); // 빈 결과를 전달
        }
        List<Note> searchNoteResults = noteRepository.findByPosts_TitleContainingOrPosts_ContentContaining(keyword, keyword);
        model.addAttribute("searchNoteResults", searchNoteResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("postList", postListForNote);
        model.addAttribute("targetPost", postList.getContent().get(0));// postList.get(0);
        model.addAttribute("noteList", noteList);
        model.addAttribute("targetNote", noteList.get(0));
        model.addAttribute("parentNoteId", noteList.get(0).getNoteId());
        return "main";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/noteWrite")
    public String noteWrite(Long noteId, Long postId, Principal principal) {
        Note note = new Note();
        List<Post> postList = new ArrayList<>();
        SiteUser siteUser = userService.getUser(principal.getName());
        note.setPosts(postList);
        note.setTitle("새 노트");
        note.setAuthor(siteUser);
        noteRepository.save(note);
        postController.write(noteRepository.findMaxNoteId(), postId, principal);
        return "redirect:/noteDetail/" + note.getNoteId() + "/" + postId;
    }

    @GetMapping("/noteDetail/{noteId}/{postId}")
    public String noteDetail(Model model, @PathVariable Long postId, @PathVariable Long noteId, @RequestParam(value = "keyword", defaultValue = "") String keyword, Pageable pageable, @RequestParam(value = "page", defaultValue = "0") int page) {
        Post post = postRepository.findById(postId).get();
        Note note = noteRepository.findById(noteId).get();
        //List<Post> postListForNote = note.getPosts();
        Page<Post> postListForNote = postService.getPageListByCreateDate(page,note);
        pageable = PageRequest.of(page, 10);
        if (keyword != null && !keyword.isEmpty()) {
            Page<Post> searchResults = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
            model.addAttribute("searchResults", searchResults);
        } else {
            model.addAttribute("searchResults", Collections.emptyList()); // 빈 결과를 전달
        }
        List<Note> searchNoteResults = noteRepository.findByPosts_TitleContainingOrPosts_ContentContaining(keyword, keyword);
        model.addAttribute("searchNoteResults", searchNoteResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("targetPost", post);
        model.addAttribute("postList", postListForNote);
        model.addAttribute("noteList", noteService.getParentNoteList());
        model.addAttribute("targetNote", note);
        if (note.getParentNote() != null) {
            model.addAttribute("parentNoteId", note.getParentNote().getNoteId());
        } else {
            model.addAttribute("parentNoteId", note.getNoteId());
        }

        return "main";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/noteDelete")
    public String noteDelete(Long noteId) {
        Note note = noteRepository.findById(noteId).get();
        noteRepository.delete(note);
        return "redirect:/note";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/noteUpdate")
    public String noteUpdate(Long noteId, String title) {
        Note note = noteRepository.findById(noteId).get();
        note.setTitle(title);
        noteRepository.save(note);
        return ("redirect:/note");
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/noteGroupWrite")
    public String noteGroupWrite(Long noteId,Principal principal,Long postId){
        Note note = new Note();
        List<Post> postList = new ArrayList<>();
        SiteUser siteUser = userService.getUser(principal.getName());
        note.setPosts(postList);
        note.setTitle("새 하위노트");
        note.setAuthor(siteUser);
        note.setParentNote(noteRepository.findById(noteId).get());
        noteRepository.save(note);
        postController.write(noteRepository.findMaxNoteId(), postId, principal);
        return "redirect:/noteDetail/" + note.getNoteId() + "/" + postId;
    }
}
