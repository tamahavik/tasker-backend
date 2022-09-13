package org.hatama.tasker.service.impl;

import org.hatama.tasker.model.entity.AppUsersValidated;
import org.hatama.tasker.repository.AppUserValidatedRepository;
import org.hatama.tasker.service.AccountService;
import org.hatama.tasker.enums.UserStatus;
import org.hatama.tasker.exception.TaskerException;
import org.hatama.tasker.model.dto.ChangePasswordRequest;
import org.hatama.tasker.model.dto.EmailDetailRequest;
import org.hatama.tasker.model.dto.MessageResponse;
import org.hatama.tasker.model.dto.ResetPasswordResponse;
import org.hatama.tasker.model.entity.AppUsers;
import org.hatama.tasker.repository.AppUsersRepository;
import org.hatama.tasker.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

import static org.hatama.tasker.enums.UserStatus.ACTIVE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AccountServiceImpl implements AccountService {


    @Value("${auth.url.reset}")
    private String urlReset;

    @Value("${token.valid.reset}")
    private long resetPasswordTokenValidity;

    private final AppUserValidatedRepository appUserValidatedRepository;
    private final AppUsersRepository appUsersRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public AccountServiceImpl(AppUserValidatedRepository appUserValidatedRepository, AppUsersRepository appUsersRepository, EmailService emailService, PasswordEncoder passwordEncoder, Clock clock) {
        this.appUserValidatedRepository = appUserValidatedRepository;
        this.appUsersRepository = appUsersRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional
    @Override
    public MessageResponse activatedAccount(String token) {
        AppUsersValidated validatedUser = appUserValidatedRepository.findByActivationId(token)
                .orElseThrow(() -> new TaskerException("Invalid token", UNAUTHORIZED));

        if (Instant.now(clock).toEpochMilli() > validatedUser.getActivationExpired()) {
            throw new TaskerException("Token expired", UNAUTHORIZED);
        }

        validatedUser = validatedUser.toBuilder()
                .activationId(null)
                .activationExpired(null)
                .build();

        AppUsers users = validatedUser.getUserId().toBuilder()
                .modifiedBy("SYSTEM")
                .status(UserStatus.ACTIVE.name())
                .build();

        appUserValidatedRepository.save(validatedUser);
        appUsersRepository.save(users);

        return MessageResponse.builder()
                .message("User active, please login")
                .build();
    }

    @Transactional
    @Override
    public MessageResponse requestResetPassword(String email) {
        AppUsers user = appUsersRepository.findByEmail(email)
                .orElseThrow(() -> new TaskerException("Invalid Email", BAD_REQUEST));

        String token = Base64.getEncoder().withoutPadding().encodeToString(UUID.randomUUID().toString().getBytes());
        AppUsersValidated validated = appUserValidatedRepository.findByUserId(user)
                .orElse(new AppUsersValidated()).toBuilder()
                .userId(user)
                .forgotPasswordId(token)
                .forgotPasswordExpired(Instant.now(clock).plus(resetPasswordTokenValidity, ChronoUnit.MINUTES).toEpochMilli())
                .build();

        appUserValidatedRepository.save(validated);
        appUsersRepository.save(user);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        EmailDetailRequest emailDetailRequest = EmailDetailRequest.builder()
                .recipient(user.getEmail())
                .subject("Reset Your Password")
                .msgBody("To reset your account please use this link " + url + urlReset + token)
                .build();

        emailService.sendSimpleMail(emailDetailRequest);

        return MessageResponse.builder()
                .message("Please check your email for reset password account")
                .build();
    }

    @Override
    public ResetPasswordResponse resetPassword(String token) {
        AppUsersValidated validatedUser = appUserValidatedRepository.findByForgotPasswordId(token)
                .orElseThrow(() -> new TaskerException("Invalid token", UNAUTHORIZED));

        if (Instant.now(clock).toEpochMilli() > validatedUser.getForgotPasswordExpired()) {
            throw new TaskerException("Token expired", UNAUTHORIZED);
        }

        return ResetPasswordResponse.builder()
                .token(token)
                .email(validatedUser.getUserId().getEmail())
                .message("Please change your password")
                .build();
    }

    @Transactional
    @Override
    public MessageResponse changePassword(ChangePasswordRequest request) {
        AppUsersValidated validatedUser = appUserValidatedRepository.findByForgotPasswordId(request.getToken())
                .orElseThrow(() -> new TaskerException("Invalid token", UNAUTHORIZED))
                .toBuilder()
                .forgotPasswordId(null)
                .forgotPasswordExpired(null)
                .build();

        if (!request.getEmail().equalsIgnoreCase(validatedUser.getUserId().getEmail())) {
            throw new TaskerException("request token not valid", BAD_REQUEST);
        }

        AppUsers users = validatedUser.getUserId().toBuilder()
                .modifiedBy("SYSTEM")
                .password(passwordEncoder.encode(request.getPassword()))
                .status(ACTIVE.name())
                .build();

        appUserValidatedRepository.save(validatedUser);
        appUsersRepository.save(users);

        return MessageResponse.builder()
                .message("Password has been change")
                .build();
    }
}
