package com.sylvester.dempproject.controller;


import com.sylvester.dempproject.dto.NoteRequest;
import com.sylvester.dempproject.models.Note;
import com.sylvester.dempproject.security.service.UserDetailsImpl;
import com.sylvester.dempproject.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping("/new-note")
    public Note addNote( @RequestBody String content ) {
        UserDetailsImpl userDetails1 = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails1.getDisplayName();
        System.out.println("username : " + username);
        return noteService.createNoteForUser(username, content);
    }

    @GetMapping("/notes")
    public ResponseEntity<List<Note>> getAllNotes(){
        return ResponseEntity.ok().body(noteService.getAllNotes());
    }




}
