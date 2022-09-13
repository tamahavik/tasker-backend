package org.hatama.tasker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hatama.tasker.enums.UserStatus;
import org.hatama.tasker.validator.ValueOfEnum;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AppUsersRequest {
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
    @ValueOfEnum(enumClass = UserStatus.class)
    private String status;
}
