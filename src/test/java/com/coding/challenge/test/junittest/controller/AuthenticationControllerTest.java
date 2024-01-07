package com.coding.challenge.test.junittest.controller;

import com.coding.challenge.controller.AuthenticationController;
import com.coding.challenge.enums.Role;
import com.coding.challenge.payload.request.LoginRequest;
import com.coding.challenge.payload.request.RefreshTokenRequest;
import com.coding.challenge.payload.request.RegisterRequest;
import com.coding.challenge.payload.response.LoginResponse;
import com.coding.challenge.payload.response.RefreshTokenResponse;
import com.coding.challenge.service.AuthenticationService;
import com.coding.challenge.service.JwtService;
import com.coding.challenge.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JwtService jwtService;

    private HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

    @Test
    public void registerNewUserTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        LoginResponse response = new LoginResponse(Long.parseLong("2"), "johncena@someemail.com", Arrays.asList("ADMIN"), "someToken", "refreshtoken", "BEARER");
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("SomeName", "SomeValue")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict");
        when(authenticationService.register(any())).thenReturn(response);
        when(jwtService.generateJwtCookie(anyString())).thenReturn(cookieBuilder.maxAge(86400).build());
        when(refreshTokenService.generateRefreshTokenCookie(anyString())).thenReturn(cookieBuilder.maxAge(900).build());

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstname("John");
        registerRequest.setLastname("Cena");
        registerRequest.setEmail("johncena@someemail.com");
        registerRequest.setPassword("1234test");
        registerRequest.setRole(Role.ADMIN);
        ResponseEntity<LoginResponse> responseEntity = authenticationController.register(registerRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }

    @Test
    public void authenticateTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        LoginResponse response = new LoginResponse(Long.parseLong("2"), "johncena@someemail.com", Arrays.asList("ADMIN"), "someToken", "refreshtoken", "BEARER");
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("SomeName", "SomeValue")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict");
        when(authenticationService.authenticate(any())).thenReturn(response);
        when(jwtService.generateJwtCookie(anyString())).thenReturn(cookieBuilder.maxAge(86400).build());
        when(refreshTokenService.generateRefreshTokenCookie(anyString())).thenReturn(cookieBuilder.maxAge(900).build());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("johncena@someemail.com");
        loginRequest.setPassword("test1234");

        ResponseEntity<LoginResponse> responseEntity = authenticationController.authenticate(loginRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody().getAccessToken()).isEqualTo("someToken");
        assertThat(responseEntity.getBody().getRefreshToken()).isEqualTo("refreshtoken");
    }

    @Test
    public void refreshTokenTest() {

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("SomeToken");

        ResponseEntity<RefreshTokenResponse> responseEntity = authenticationController.refreshToken(request);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }

    @Test
    public void logoutTest() {

        ResponseCookie responseCookie = ResponseCookie.from("jwt-cookie", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        ResponseCookie refreshtokenCookie = ResponseCookie.from("refresh-jwt-cookie", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        Cookie[] localCookies = new Cookie[]{new Cookie("name", "value")};
        when(httpServletRequest.getCookies()).thenReturn(localCookies);
        when(jwtService.getCleanJwtCookie()).thenReturn(responseCookie);
        when(refreshTokenService.getCleanRefreshTokenCookie()).thenReturn(refreshtokenCookie);
        ResponseEntity<Void> responseEntity = authenticationController.logout(httpServletRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }
}
