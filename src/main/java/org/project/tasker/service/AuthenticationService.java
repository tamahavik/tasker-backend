package org.project.tasker.service;

import org.project.tasker.model.dto.LoginRequest;
import org.project.tasker.model.dto.LoginResponse;
import org.project.tasker.model.dto.RegisterRequest;
import org.project.tasker.model.dto.MessageResponse;

public interface AuthenticationService {

    LoginResponse doLogin(LoginRequest request);

    MessageResponse doRegister(RegisterRequest request);

}
