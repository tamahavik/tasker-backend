package org.project.tasker.service;

import org.project.tasker.model.dto.ChangePasswordRequest;
import org.project.tasker.model.dto.MessageResponse;
import org.project.tasker.model.dto.ResetPasswordResponse;

public interface AccountService {

    MessageResponse activatedAccount(String token);

    MessageResponse requestResetPassword(String email);

    ResetPasswordResponse resetPassword(String token);

    MessageResponse changePassword(ChangePasswordRequest request);

}
