package com.sylvester.dempproject.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "username is required")
    private String username;
    @Column(unique = true)
    @Email
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
    @Column(unique = true)
    private String passwordResetToken;
    private Instant passwordResetDate;
    private String twoFactorSecret;
    private boolean isTwoFactorEnabled = false;
   private boolean accountEnabled = false;
   private Long verificationCode;
   private LocalDateTime verificationCreatedAt;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @JsonBackReference
    @ToString.Exclude
    private Roles roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}



