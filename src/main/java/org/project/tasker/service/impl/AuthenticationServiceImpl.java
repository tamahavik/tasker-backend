package org.project.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.project.tasker.exception.TaskerException;
import org.project.tasker.mapper.AppUsersMapper;
import org.project.tasker.model.dto.LoginRequest;
import org.project.tasker.model.dto.LoginResponse;
import org.project.tasker.model.dto.RegisterRequest;
import org.project.tasker.model.dto.RegisterResponse;
import org.project.tasker.model.entity.AppUsers;
import org.project.tasker.repository.AppUsersRepository;
import org.project.tasker.service.AuthenticationService;
import org.project.tasker.service.EmailService;
import org.project.tasker.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.project.tasker.enums.UserStatus.INACTIVE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${jwt.token.type}")
    private String tokenType;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final AppUsersRepository appUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, AppUsersRepository appUsersRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.appUsersRepository = appUsersRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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

    @Override
    public RegisterResponse doRegister(RegisterRequest request) {
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
        UserDetails userDetails = new User(saved.getUsername(), saved.getPassword(), new ArrayList<>());
        String accessToken = jwtTokenService.generateToken(userDetails);
        String refreshToken = jwtTokenService.generateRefreshToken(userDetails);

        return RegisterResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();
    }
}
