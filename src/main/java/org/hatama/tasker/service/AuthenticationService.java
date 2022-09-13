package org.hatama.tasker.service;

import org.hatama.tasker.model.dto.LoginRequest;
import org.hatama.tasker.model.dto.LoginResponse;
import org.hatama.tasker.model.dto.MessageResponse;
import org.hatama.tasker.model.dto.RegisterRequest;

public interface AuthenticationService {

    LoginResponse doLogin(LoginRequest request);

    MessageResponse doRegister(RegisterRequest request);

}
