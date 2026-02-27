package com.gaggledemo.controllers.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AppUserRequestDto {

    @NotBlank(message = "Name cannot be blank")
    public final String name;

    @Email(message = "Email must be valid")
    public final String email;

    @NotBlank(message = "Name cannot be blank")
    public final String schoolAccount;

    public AppUserRequestDto(@JsonProperty("name") String name,
                             @JsonProperty("email") String email,
                             @JsonProperty("schoolAccount") String schoolAccount) {
        this.name = name;
        this.email = email;
        this.schoolAccount = schoolAccount;
    }
}
