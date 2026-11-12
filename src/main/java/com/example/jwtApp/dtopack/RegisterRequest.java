package com.example.jwtApp.dtopack;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30,
          message = "Username must be 3 to 30 characters")
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Minimum 8 characters required")
    String password

) {
}
