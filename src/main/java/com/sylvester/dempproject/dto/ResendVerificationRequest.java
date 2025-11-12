package com.sylvester.dempproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendVerificationRequest {
    @NotBlank(message = "Please provide your email account you used to open your account")
    @Email
    private String email;
}
