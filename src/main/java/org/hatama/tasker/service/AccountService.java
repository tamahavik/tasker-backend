package org.hatama.tasker.service;

import org.hatama.tasker.model.dto.ChangePasswordRequest;
import org.hatama.tasker.model.dto.MessageResponse;
import org.hatama.tasker.model.dto.ResetPasswordResponse;

public interface AccountService {

    MessageResponse activatedAccount(String token);

    MessageResponse requestResetPassword(String email);

    ResetPasswordResponse resetPassword(String token);

    MessageResponse changePassword(ChangePasswordRequest request);

}
