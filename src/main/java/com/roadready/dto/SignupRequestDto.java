package com.roadready.dto;

import jakarta.validation.constraints.NotNull;

public record SignupRequestDto(
                @NotNull(message = "name cannot be blank. Are you nameless ?")
                String name,
                @NotNull(message = "Don't come here without an email. Are you from the stoneage ?")
                String email,
                @NotNull(message = "This is not your father's website, go type a password")
                String password,
                @NotNull(message = "Really bruh ? No mobile number ? How poor are you ?")
                String phoneNumber) {
}
