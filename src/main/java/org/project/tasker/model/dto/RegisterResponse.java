package org.project.tasker.model.dto;

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
public class RegisterResponse {
    public String accessToken;
    public String tokenType;
    public String refreshToken;
}
