package com.sylvester.dempproject.service;


import com.sylvester.dempproject.dto.NoteRequest;
import com.sylvester.dempproject.dto.Response;
import com.sylvester.dempproject.models.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NoteService {

    Note createNoteForUser(String username, String content);

    Note updateNoteForUser(Long noteId, String content);

    void deleteNoteForUser(Long noteId, String username);

    List<Note> getAllNotes();

    List<Note> getNotesForUser(String username);


}
