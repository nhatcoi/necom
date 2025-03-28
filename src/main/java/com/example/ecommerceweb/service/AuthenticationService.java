package com.example.ecommerceweb.service;

import com.example.ecommerceweb.dto.request.auth.AuthenticationRequest;
import com.example.ecommerceweb.dto.request.auth.IntrospectRequest;
import com.example.ecommerceweb.dto.response.auth.AuthenticationResponse;
import com.example.ecommerceweb.dto.response.auth.IntrospectResponse;
import com.example.ecommerceweb.dto.response.auth.TokenResponse;
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
