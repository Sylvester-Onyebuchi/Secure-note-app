package com.sylvester.dempproject.serviceimpl;


import com.sylvester.dempproject.dto.NoteRequest;
import com.sylvester.dempproject.models.Note;
import com.sylvester.dempproject.models.User;
import com.sylvester.dempproject.repository.NoteRepository;
import com.sylvester.dempproject.repository.UserRepository;
import com.sylvester.dempproject.service.NoteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {


    private final NoteRepository noteRepository;



    @Override
    public Note createNoteForUser(String username, String content) {
        Note note = Note.builder()
                .content(content)
                .ownerUsername(username)
                .build();
        return noteRepository.save(note);
    }

    @Override
    public Note updateNoteForUser(Long noteId, String content) {
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new EntityNotFoundException("Note with id " + noteId + " not found")
        );
        note.setContent(content);
        return noteRepository.save(note);
    }

    @Override
    public void deleteNoteForUser(Long noteId, String username) {
        noteRepository.findById(noteId).orElseThrow(
                () -> new EntityNotFoundException("Note with id " + noteId + " not found")
        );
        noteRepository.deleteById(noteId);

    }

    @Override
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @Override
    public List<Note> getNotesForUser(String username) {

        return noteRepository.findByOwnerUsername(username);
    }
}
