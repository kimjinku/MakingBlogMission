package com.korea.basic1.note;

import com.korea.basic1.postService.Post;
import com.korea.basic1.postService.PostRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final PostRepository postRepository;
    public List<Note> getNoteList(){
        return noteRepository.findAll();
    }
    public Note getNote(Long noteId){
        Optional<Note> note = this.noteRepository.findById(noteId);
        if(note.isPresent()){
            return note.get();
        }else{
            throw new RuntimeException("question not found");
        }
    }
    public List<Note> getParentNotePage(){
        return noteRepository.findByParentNoteNoteId(null);
    }
    public List<Note> getParentNoteList(){
        return noteRepository.findByParentNoteNoteId(null);
    }

}
