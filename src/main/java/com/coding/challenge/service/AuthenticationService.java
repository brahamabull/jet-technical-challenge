package com.coding.challenge.service;


import com.coding.challenge.payload.request.LoginRequest;
import com.coding.challenge.payload.request.RegisterRequest;
import com.coding.challenge.payload.response.LoginResponse;

public interface AuthenticationService {
    LoginResponse register(RegisterRequest request);
    LoginResponse authenticate(LoginRequest request);
}
