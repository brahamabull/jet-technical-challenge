package com.coding.challenge.service;


import com.coding.challenge.payload.request.AuthenticationRequest;
import com.coding.challenge.payload.request.RegisterRequest;
import com.coding.challenge.payload.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
