package com.sylvester.dempproject.serviceimpl;

import com.sylvester.dempproject.dto.*;
import com.sylvester.dempproject.exception.UserAlreadyExistException;
import com.sylvester.dempproject.exception.UserNotFoundException;
import com.sylvester.dempproject.models.Role;
import com.sylvester.dempproject.models.Roles;
import com.sylvester.dempproject.models.User;
import com.sylvester.dempproject.repository.RoleRepository;
import com.sylvester.dempproject.repository.UserRepository;
import com.sylvester.dempproject.service.TwoFAService;
import com.sylvester.dempproject.service.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final TwoFAService twoFAService;

    @Override
    public Response createUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("User already exists");
        }

        Roles userRole = roleRepository.findByRoleType(Role.ROLE_USER).orElseGet(
                () -> roleRepository.save(new Roles(Role.ROLE_USER))
        );

        Random random = new Random();
        long verificationCode = 100000 + random.nextInt(900000);
        User newUser = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .roles(userRole)
                .accountEnabled(false)
                .verificationCode(verificationCode)
                .verificationCreatedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(newUser);
        emailService.sendEmail(savedUser.getEmail(),savedUser.getUsername(), savedUser.getVerificationCode());
        return Response.builder()
                .message("User created successfully")
                .data(savedUser)
                .build();
    }



    @Override
    public Response getUser(Long id) {
        User user  = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
        return Response.builder()
                .message("User found")
                .data(user)
                .build();
    }

    @Override
    public Response updateUserRole(Long id, UserRoleUpdate roleUpdate) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );

        if (!user.isAccountEnabled()){
            return Response.builder()
                    .message("You are not allowed to update your account\nPlease verify your account and try again.")
                    .build();
        }


        Roles roles = roleRepository.findByRoleType(user.getRoles().getRoleType()).orElseThrow(
                () -> new UserNotFoundException("user's role not found")
        );

        System.out.println(roles);


        roles.setRoleType(Role.valueOf(roleUpdate.getNewRole()));

        user.setRoles(roles);
        User newUserRole = userRepository.save(user);
        return Response.builder()
                .message("User role updated successfully to "+newUserRole.getRoles())
                .data(newUserRole)
                .build();

    }

    @Override
    public Response deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
        userRepository.deleteById(id);
        return Response.builder()
                .message("User deleted successfully")
                .build();
    }

    @Override
    public Response updateUser(Long id,UserRequest userRequest) {
        User checkUser = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
        if (!checkUser.isAccountEnabled()) {
            return Response.builder()
                    .message("Account is not enabled\nPlease verify your account")
                    .build();
        }
        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .build();
        User savedUser = userRepository.save(user);
        return Response.builder()
                .message("User updated successfully")
                .data(savedUser)
                .build();
    }

    @Override
    public Response verifyUser(VerifyUserRequest verifyUser, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );

        if (user.getVerificationCode().equals(verifyUser.getVerificationCode())
        && Duration.between(user.getVerificationCreatedAt(), LocalDateTime.now()).toHours() <= 10) {
            user.setAccountEnabled(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            emailService.sendVerifiedEmail(user.getEmail(), user.getUsername());
            return Response.builder()
                    .message("User verified successfully")
                    .build();
        }

        return  Response.builder()
                .message("Invalid verification code")
                .build();
    }

    @Override
    public Response checkIfVerified(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
        if (!user.isAccountEnabled()){
            return Response.builder()
                    .message("Your account is not enabled\nPlease verify your account")
                    .build();
        }
        return null;
    }

    @Override
    public Response generateNewVerificationCode(ResendVerificationRequest verificationRequest) {
        User user = userRepository.findByEmail(verificationRequest.getEmail()).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
        Random random = new Random();
        long verificationCode = 100000 + random.nextInt(900000);
        user.setVerificationCode(verificationCode);
        user.setVerificationCreatedAt(LocalDateTime.now());
        User savedCode = userRepository.save(user);
        emailService.sendNewVerificationCode(savedCode.getEmail(), savedCode.getUsername(), savedCode.getVerificationCode());
        return Response.builder()
                .message("New verification code generated successfully")
                .build();

    }


    @Override
    public GoogleAuthenticatorKey generate2FASecret(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user not found")

        );
        GoogleAuthenticatorKey key = twoFAService.generateSecret();
        user.setTwoFactorSecret(key.getKey());
        userRepository.save(user);
        return key;

    }

    @Override
    public boolean validate2FACode(Long userId, int code) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
        return twoFAService.verifyQrCode(user.getTwoFactorSecret(), code);

    }

    @Override
    public void enable2FA(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disable2FA(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
        user.setTwoFactorEnabled(false);
        userRepository.save(user);
    }

    @Override
    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername((authentication.getName())).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("user not found")
        );
        return Optional.of(user);


    }

    @Override
    public User registerUser(User user){
        if (user.getPassword() != null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    @Override
    public void updatePassword(Long userId, String password) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update password");
        }
    }

    @Override
    public void generatePasswordResetToken(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
        user.setPasswordResetToken(token);
        user.setPasswordResetDate(expiryDate);
        userRepository.save(user);
        String frontendUrl = "http://localhost:3000"; // for example
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl, user.getUsername());
        } catch (Exception e) {
            user.setPasswordResetToken(null);
            user.setPasswordResetDate(null);
            userRepository.save(user);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }


    @Override
    public void resetPassword(String token, String newPassword) {
        User resetToken = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new UserNotFoundException("Invalid password reset token"));

        if (resetToken.getPasswordResetDate().isBefore(Instant.now()))
            throw new RuntimeException("Password reset token has expired");

        resetToken.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(resetToken);

    }


}
