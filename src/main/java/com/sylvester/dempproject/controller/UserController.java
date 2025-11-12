package com.sylvester.dempproject.controller;


import com.sylvester.dempproject.dto.*;
import com.sylvester.dempproject.models.User;
import com.sylvester.dempproject.security.jwt.JwtUtils;
import com.sylvester.dempproject.security.service.UserDetailsImpl;
import com.sylvester.dempproject.service.TwoFAService;
import com.sylvester.dempproject.service.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {



    private final UserService userService;



    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;


    private final TwoFAService twoFAService;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, TwoFAService twoFAService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.twoFAService = twoFAService;
    }


    @PostMapping("/public/signup")
    public ResponseEntity<Response> createUser(@Valid @RequestBody UserRequest request) {
        Response response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);


    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Message","No authenticated user"));

        }
        UserDetailsImpl userDetails1 = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails1.getDisplayName();
        return ResponseEntity.ok().body(username);

    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Response> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(id));
    }


    @PostMapping("/public/login")
    public ResponseEntity<?> signin(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();


            if (!userDetails.isEnabled()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Your account is not verified. Please check your email.");
                response.put("status", false);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtUtils.generateTokenFromUsername(userDetails);
            Response successResponse = Response.builder()
                    .message("Authentication successful")
                    .jwtToken(token)
                    .roles(roles)
                    .username(userDetails.getUsername())
                    .build();

            return ResponseEntity.ok(successResponse);

        } catch (DisabledException e){
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Your account is not verified. Please verify your account with code sent to your email.");
            response.put("status", false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid username or password");
            response.put("status", false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }



    @PostMapping("/public/verify-user")
    public ResponseEntity<Response> verifyUser( @RequestBody VerifyUserRequest request, @RequestParam Long userId) {
        Response response = userService.verifyUser(request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/public/new-code")
    public ResponseEntity<Response> resendVerificationCode(@Valid @RequestBody ResendVerificationRequest request) {
        Response response = userService.generateNewVerificationCode(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/enable-2fa")
    public ResponseEntity<String> enable2FA(){
        Long userId = userService.loggedInUser().getId();
        GoogleAuthenticatorKey secret = userService.generate2FASecret(userId);
        String qrCodeUrl = twoFAService.getQrCodeURL(secret,
                userService.getUser(userId).getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(qrCodeUrl);
    }

    @PostMapping("/disable-2fa")
    public ResponseEntity<String> disable2FA(){
        Long userId = userService.loggedInUser().getId();
        userService.disable2FA(userId);
        return ResponseEntity.status(HttpStatus.OK).body("2FA has been disabled");
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<String> verify2FA(int code){
        Long userId = userService.loggedInUser().getId();
        boolean isValid = userService.validate2FACode(userId,code);
        if(isValid){
            userService.enable2FA(userId);
            return ResponseEntity.status(HttpStatus.OK).body("2FA has been enabled");
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code");
        }
    }


    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.generatePasswordResetToken(email);
            return ResponseEntity.ok("Password reset email sent!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error sending password reset email"));
        }

    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @RequestParam String newPassword) {

        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok().body(Map.of("message", "Password reset successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }




}
