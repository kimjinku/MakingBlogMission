package com.korea.basic1.postService;

import com.korea.basic1.note.Note;
import com.korea.basic1.note.NoteRepository;
import com.korea.basic1.note.NoteService;
import com.korea.basic1.user.SiteUser;
import com.korea.basic1.user.UserService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

//질문사항
//검색기능 고치기


@Controller
public class PostController {
    @Autowired
    PostRepository postRepository;
    @Autowired
    NoteRepository noteRepository;

    @Autowired
    UserService userService;
    @Autowired
    NoteService noteService;

    @RequestMapping("/")
    public String main(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword, Pageable pageable, @RequestParam(value = "page", defaultValue = "0") int page) {
        List<Post> postList = postRepository.findAll();
        List<Note> noteList = noteService.getParentNoteList();
        List<Post> postListForNote = noteList.get(0).getPosts();
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
        model.addAttribute("postList", postListForNote);
        model.addAttribute("targetPost", postList.get(0));
        model.addAttribute("noteList", noteList);
        model.addAttribute("targetNote", noteList.get(0));
        return "main";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(Long noteId, Long postId, Principal principal) {
        Post post = new Post();
        Note note = noteRepository.findById(noteId).get();
        SiteUser siteUser = userService.getUser(principal.getName());
        post.setTitle("newTitle");
        post.setContent("");
        post.setCreateDate(LocalDateTime.now());
        post.setNote(note);
        post.setAuthor(siteUser);
        postRepository.save(post);
        return "redirect:detail/" + noteId + "/" + postId;
    }

    @GetMapping("/detail/{noteId}/{postId}")
    public String detail(Model model, @PathVariable Long postId, @PathVariable Long noteId, @RequestParam(value = "keyword", defaultValue = "") String keyword, Pageable pageable, @RequestParam(value = "page", defaultValue = "0") int page) {
        Post post = postRepository.findById(postId).get();
        Note note = noteRepository.findById(noteId).get();
        pageable = PageRequest.of(page, 10);
        List<Post> postListForNote = note.getPosts();
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

        return "main";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String update(@RequestParam Long postId, String title, String editorContent) {
        Post post = postRepository.findById(postId).get();
        post.setTitle(title);
        post.setContent(editorContent);
        if (post.getTitle().equals("")) {
            post.setTitle("제목없음");
        }
        postRepository.save(post);
        return "redirect:/detail/" + post.getNote().getNoteId() + "/" + postId;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete")
    public String delete(Long postId) {
        Post post = postRepository.findById(postId).get();
        postRepository.delete(post);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam(value = "keyword", defaultValue = "") String keyword, Model model, Pageable pageable, @RequestParam(value = "page", defaultValue = "0") int page) {
        List<Post> postList = postRepository.findAll();
        List<Note> noteList = noteService.getParentNoteList();
        pageable = PageRequest.of(page, 10);
        Page<Post> searchResults = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        model.addAttribute("searchResults", searchResults);
        List<Note> searchNoteResults = noteRepository.findByPosts_TitleContainingOrPosts_ContentContaining(keyword, keyword);
        model.addAttribute("searchNoteResults", searchNoteResults);
        List<Post> postListForNote = noteList.get(0).getPosts();
        model.addAttribute("keyword", keyword);
        model.addAttribute("postList", postListForNote);
        model.addAttribute("targetPost", postList.get(0));
        model.addAttribute("noteList", noteList);
        model.addAttribute("targetNote", noteList.get(0));

        return "main";
    }


}



