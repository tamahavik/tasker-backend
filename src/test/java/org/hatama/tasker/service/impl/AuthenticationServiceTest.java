package org.hatama.tasker.service.impl;

import org.hatama.tasker.enums.UserStatus;
import org.hatama.tasker.exception.TaskerException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "jwt.token.type=Bearer",
        "token.valid.active=60"
})
class AuthenticationServiceTest {

    private AuthenticationService underTest;

    @Value("${jwt.token.type}")
    private String tokenType;

    @Value("${token.valid.active}")
    private long tokenActivatedValidity;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private AppUsersRepository appUsersRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private AppUserValidatedRepository appUserValidatedRepository;
    @Mock
    private HttpServletRequest request;

    private final Clock clock = Clock.systemDefaultZone();

    @BeforeEach
    void setUp() {
        underTest = new AuthenticationServiceImpl(
                authenticationManager,
                jwtTokenService,
                appUsersRepository,
                passwordEncoder,
                emailService,
                appUserValidatedRepository,
                clock
        );
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    LoginRequest loginRequest = LoginRequest.builder()
            .username("username")
            .password("password")
            .build();

    RegisterRequest registerRequest = RegisterRequest.builder()
            .firstName("firstName")
            .lastName("lastName")
            .email("email@gmail.com")
            .username("username")
            .password("password")
            .phone("08028302830")
            .build();

    AppUsers appUsers = AppUsers.builder()
            .id("63299cb2-1fd8-4e51-8e69-f44882ac2dd8")
            .createdBy("hatama")
            .createdDate(LocalDateTime.now())
            .firstName("firstName")
            .lastName("lastName")
            .email("email@gmail.com")
            .username("username")
            .password("password")
            .phone("08028302830")
            .status(UserStatus.INACTIVE.name())
            .build();

    AppUsersValidated validated = AppUsersValidated.builder()
            .id("2b47ddd5-d5bb-4260-a13b-1d73e5f06cb0")
            .userId(appUsers)
            .activationId("2b47ddd5-d5bb-4260-a13b-1d73e5f06cb1")
            .activationExpired(LocalDateTime.now(clock).plus(tokenActivatedValidity, ChronoUnit.MINUTES))
            .build();

    ArgumentCaptor<AppUsers> acAppUsers = ArgumentCaptor.forClass(AppUsers.class);
    ArgumentCaptor<AppUsersValidated> acAppUsersValidate = ArgumentCaptor.forClass(AppUsersValidated.class);
    ArgumentCaptor<EmailDetailRequest> acEmailDetailRequest = ArgumentCaptor.forClass(EmailDetailRequest.class);

    @Test
    void givenValidInput_whenDoLogin_thenSuccess() {
        //given
        String token = "token";
        String refreshToken = "refresh-token";
        UserDetails user = new User(loginRequest.getUsername(), loginRequest.getPassword(), new ArrayList<>());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities()));
        when(jwtTokenService.generateToken(any(UserDetails.class)))
                .thenReturn(token);
        when(jwtTokenService.generateRefreshToken(any(UserDetails.class)))
                .thenReturn(refreshToken);

        //when
        LoginResponse loginResponse = underTest.doLogin(loginRequest);

        //then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenService, times(1)).generateToken(any(UserDetails.class));
        verify(jwtTokenService, times(1)).generateRefreshToken(any(UserDetails.class));

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getTokenType()).isEqualTo(tokenType);
        assertThat(loginResponse.getAccessToken()).isEqualTo(token);
        assertThat(loginResponse.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    void givenInvalidInput_whenDoLogin_thenFailed() {
        //given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(RuntimeException.class);

        //when & then
        assertThatThrownBy(() -> underTest.doLogin(loginRequest))
                .isInstanceOf(TaskerException.class);
    }

    @Test
    void givenValidInput_whenDoRegister_thenSuccess() {
        //given
        String expected = "Please check your email for activated account";
        when(appUsersRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());
        when(appUsersRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("password");
        when(appUsersRepository.save(any(AppUsers.class)))
                .thenReturn(appUsers);
        when(appUserValidatedRepository.save(any(AppUsersValidated.class)))
                .thenReturn(validated);
        when(emailService.sendSimpleMail(any(EmailDetailRequest.class)))
                .thenReturn("SUCCESS");

        //when
        MessageResponse messageResponse = underTest.doRegister(registerRequest);

        //then
        verify(appUsersRepository, times(1)).findByUsername(anyString());
        verify(appUsersRepository, times(1)).findByEmail(anyString());
        verify(appUsersRepository, times(1)).save(acAppUsers.capture());
        verify(appUserValidatedRepository, times(1)).save(acAppUsersValidate.capture());
        verify(emailService, times(1)).sendSimpleMail(acEmailDetailRequest.capture());

        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse.getMessage()).isEqualTo(expected);

        AppUsers appUserCapture = acAppUsers.getValue();
        assertThat(appUserCapture.getPassword()).isEqualTo(appUsers.getPassword());
        assertThat(appUserCapture.getUsername()).isEqualTo(appUsers.getUsername());
        assertThat(appUserCapture.getStatus()).isEqualTo(appUsers.getStatus());
        assertThat(appUserCapture.getEmail()).isEqualTo(appUsers.getEmail());
        assertThat(appUserCapture.getFirstName()).isEqualTo(appUsers.getFirstName());
        assertThat(appUserCapture.getLastName()).isEqualTo(appUsers.getLastName());
        assertThat(appUserCapture.getPhone()).isEqualTo(appUsers.getPhone());

        AppUsersValidated validatedCapture = acAppUsersValidate.getValue();
        assertThat(validatedCapture.getUserId()).isEqualTo(validated.getUserId());

        EmailDetailRequest emailDetailRequest = acEmailDetailRequest.getValue();
        assertThat(emailDetailRequest.getRecipient()).isEqualTo(appUsers.getEmail());
        System.out.println(emailDetailRequest.getMsgBody());
    }

    @Test
    void givenExistUsername_whenDoRegister_thenFailed() {
        when(appUsersRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(appUsers));

        assertThatThrownBy(() -> underTest.doRegister(registerRequest))
                .isInstanceOf(TaskerException.class)
                .hasMessageContaining("exist");
    }

    @Test
    void givenExistEmail_whenDoRegister_thenFailed() {
        when(appUsersRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());
        when(appUsersRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(appUsers));

        assertThatThrownBy(() -> underTest.doRegister(registerRequest))
                .isInstanceOf(TaskerException.class)
                .hasMessageContaining("exist");
    }
}