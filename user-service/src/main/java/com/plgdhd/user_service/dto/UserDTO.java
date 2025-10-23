package com.plgdhd.user_service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class UserDTO {

    private String id;

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
