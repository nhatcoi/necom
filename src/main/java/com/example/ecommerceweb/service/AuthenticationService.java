package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.auth.AuthenticationRequest;
import com.example.ecommerceweb.dto.auth.IntrospectRequest;
import com.example.ecommerceweb.dto.auth.AuthenticationResponse;
import com.example.ecommerceweb.dto.auth.IntrospectResponse;
import com.example.ecommerceweb.dto.auth.TokenResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    AuthenticationResponse authenticate(AuthenticationRequest request);
    TokenResponse refreshAccessToken(String refreshToken);
    void revokeToken(String refreshToken);

    void revokeAccessToken(String accessToken);
}
