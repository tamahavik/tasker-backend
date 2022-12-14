package org.hatama.tasker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegisterRequest {
    @NotBlank(message = "firstName cant empty")
    private String firstName;
    @NotBlank(message = "lastName cant empty")
    private String lastName;
    @NotBlank(message = "email cant empty")
    private String email;
    @NotBlank(message = "username cant empty")
    private String username;
    @NotBlank(message = "password cant empty")
    private String password;
    @NotBlank(message = "phone cant empty")
    private String phone;
}
