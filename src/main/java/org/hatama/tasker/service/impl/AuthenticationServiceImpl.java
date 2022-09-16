package org.hatama.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.hatama.tasker.exception.TaskerException;
import org.hatama.tasker.mapper.AppUsersMapper;
import org.hatama.tasker.model.dto.EmailDetailRequest;
import org.hatama.tasker.model.dto.LoginRequest;
import org.hatama.tasker.model.dto.LoginResponse;
import org.hatama.tasker.model.dto.MessageResponse;
import org.hatama.tasker.model.dto.RegisterRequest;
import org.hatama.tasker.model.entity.AppUsers;
import org.hatama.tasker.model.entity.AppUsersValidated;
import org.hatama.tasker.repository.AppUserValidatedRepository;
import org.hatama.tasker.repository.AppUsersRepository;
import org.hatama.tasker.service.AuthenticationService;
import org.hatama.tasker.service.EmailService;
import org.hatama.tasker.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

import static org.hatama.tasker.enums.UserStatus.INACTIVE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${jwt.token.type}")
    private String tokenType;

    @Value("${auth.url.activated}")
    private String urlActivated;

    @Value("${token.valid.active}")
    private long tokenActivatedValidity;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final AppUsersRepository appUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AppUserValidatedRepository appUserValidatedRepository;
    private final Clock clock;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, AppUsersRepository appUsersRepository, PasswordEncoder passwordEncoder, EmailService emailService, AppUserValidatedRepository appUserValidatedRepository, Clock clock) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.appUsersRepository = appUsersRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.appUserValidatedRepository = appUserValidatedRepository;
        this.clock = clock;
    }

    @Override
    public LoginResponse doLogin(LoginRequest request) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            throw new TaskerException(e.getMessage(), UNAUTHORIZED);
        }
        UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
        String accessToken = jwtTokenService.generateToken(userDetails);
        String refreshToken = jwtTokenService.generateRefreshToken(userDetails);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();
    }

    @Transactional
    @Override
    public MessageResponse doRegister(RegisterRequest request) {
        boolean existUsername = appUsersRepository.findByUsername(request.getUsername())
                .isPresent();
        if (existUsername) {
            throw new TaskerException("Username already exist", BAD_REQUEST);
        }

        boolean existEmail = appUsersRepository.findByEmail(request.getEmail())
                .isPresent();
        if (existEmail) {
            throw new TaskerException("Email already exist", BAD_REQUEST);
        }

        AppUsers user = AppUsersMapper.INSTANCE.registerToAppUsers(request);
        user = user.toBuilder()
                .password(passwordEncoder.encode(request.getPassword()))
                .createdBy(request.getUsername())
                .status(INACTIVE.name())
                .build();

        AppUsers saved = appUsersRepository.save(user);

        String token = Base64.getEncoder().withoutPadding().encodeToString(UUID.randomUUID().toString().getBytes()).toLowerCase();
        AppUsersValidated validated = AppUsersValidated.builder()
                .activationId(token)
                .activationExpired(LocalDateTime.now(clock).plus(tokenActivatedValidity, ChronoUnit.MINUTES))
                .userId(saved)
                .build();

        appUserValidatedRepository.save(validated);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        EmailDetailRequest emailDetailRequest = EmailDetailRequest.builder()
                .recipient(saved.getEmail())
                .subject("Active Your Account")
                .msgBody("To active your account please use this link " + url + urlActivated + token)
                .build();

        emailService.sendSimpleMail(emailDetailRequest);

        return MessageResponse.builder()
                .message("Please check your email for activated account")
                .build();
    }


}
