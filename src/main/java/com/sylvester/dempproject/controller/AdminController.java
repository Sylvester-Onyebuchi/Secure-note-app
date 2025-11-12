package com.sylvester.dempproject.controller;


import com.sylvester.dempproject.dto.Response;
import com.sylvester.dempproject.dto.UserRequest;
import com.sylvester.dempproject.dto.UserRoleUpdate;
import com.sylvester.dempproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor

public class AdminController {

    private final UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<Response> getUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Response> updateUserRole(@PathVariable Long id, @RequestBody UserRoleUpdate roleUpdate) {
        Response response = userService.updateUserRole(id,roleUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
