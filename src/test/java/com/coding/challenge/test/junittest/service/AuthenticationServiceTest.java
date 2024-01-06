package com.coding.challenge.test.junittest.service;

import com.coding.challenge.entity.RefreshToken;
import com.coding.challenge.entity.UserInfo;
import com.coding.challenge.enums.Role;
import com.coding.challenge.payload.request.LoginRequest;
import com.coding.challenge.payload.request.RegisterRequest;
import com.coding.challenge.payload.response.LoginResponse;
import com.coding.challenge.repository.UserRepository;
import com.coding.challenge.service.JwtService;
import com.coding.challenge.service.RefreshTokenService;
import com.coding.challenge.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks
    AuthenticationServiceImpl authenticationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Test
    public void registerServiceImplTest() {
        UserInfo user = createUserInfo();
        RefreshToken refreshToken = createRefreshToken(user);
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(refreshToken);
        LoginResponse loginResponse = authenticationService.register(createRequest());
        verify(userRepository, times(1)).save(Mockito.any(UserInfo.class));
        verify(jwtService, times(1)).generateToken(Mockito.any(UserDetails.class));
        verify(refreshTokenService, times(1)).createRefreshToken(Mockito.anyLong());
        assertThat(loginResponse.getRefreshToken()).isEqualTo("token")
;    }

    @Test
    public void authenticateServiceImplTest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("test1234");
        loginRequest.setEmail("abc@email.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserInfo()));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(createRefreshToken(createUserInfo()));
        LoginResponse loginResponse = authenticationService.authenticate(loginRequest);
        verify(jwtService, times(1)).generateToken(Mockito.any(UserDetails.class));
        verify(refreshTokenService, times(1)).createRefreshToken(Mockito.anyLong());
        verify(authenticationManager, times(1)).authenticate(Mockito.any());
        verify(userRepository, times(1)).findByEmail(Mockito.anyString());
        assertThat(loginResponse.getAccessToken()).isEqualTo("jwt-token");
    }

    private UserInfo createUserInfo() {
        UserInfo user = new UserInfo();
        user.setEmail("johncena@someemail.com");
        user.setFirstname("John");
        user.setLastname("Cena");
        user.setPassword("test123");
        user.setRole(Role.ADMIN);
        user.setId(1L);
        return user;
    }

    private RegisterRequest createRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("johncena@someemail.com");
        registerRequest.setFirstname("John");
        registerRequest.setLastname("Cena");
        registerRequest.setPassword("test123");
        registerRequest.setRole(Role.ADMIN);
        return registerRequest;
    }

    private RefreshToken createRefreshToken(UserInfo user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("token");
        refreshToken.setRevoked(false);
        refreshToken.setId(2L);
        refreshToken.setExpiryDate(Instant.now());
        refreshToken.setUser(user);
        return refreshToken;
    }
}
