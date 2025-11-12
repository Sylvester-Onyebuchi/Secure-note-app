package com.sylvester.dempproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyUserRequest {

    @NotBlank
    @Size(min = 6, max = 6)
    private Long verificationCode;
}
