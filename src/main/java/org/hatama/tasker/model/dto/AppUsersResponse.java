package org.hatama.tasker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AppUsersResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phone;
    private String status;
}
