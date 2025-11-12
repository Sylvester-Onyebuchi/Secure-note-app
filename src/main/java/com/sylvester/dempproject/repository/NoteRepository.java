package com.sylvester.dempproject.repository;


import com.sylvester.dempproject.models.Note;
import com.sylvester.dempproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByOwnerUsername(String ownerUsername);
}
