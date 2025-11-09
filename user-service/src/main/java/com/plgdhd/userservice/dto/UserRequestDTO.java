package com.plgdhd.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDTO {

    @NotBlank(message = "Users name is required")
    @Size(min = 1)
    private String name;

    @Size(min = 1)
    private String surname;

    private LocalDate birthDate;

    @NotBlank(message = "Users email is required")
    @Size(min = 1)
    private String email;

}
