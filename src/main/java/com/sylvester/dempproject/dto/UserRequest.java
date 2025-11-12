package com.sylvester.dempproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "username required")
    @Size(min = 5,max = 30)
    private String username;
    @NotBlank(message = "email is required")
    @Email(message = "it must be in email form")
    private String email;
    @NotBlank(message = "password is required")
    @Size(min = 6, max = 15, message = "Password should be minimum of 6 character and maximum of 15")
    private String password;

}
