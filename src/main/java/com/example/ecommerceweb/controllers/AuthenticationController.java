package com.example.ecommerceweb.controllers;

import com.example.ecommerceweb.dtos.request.AuthenticationRequest;
import com.example.ecommerceweb.dtos.request.IntrospectRequest;
import com.example.ecommerceweb.dtos.response.AuthenticationResponse;
import com.example.ecommerceweb.dtos.response.IntrospectResponse;
import com.example.ecommerceweb.services.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/log-in")
    public ResponseEntity<AuthenticationResponse> logIn(@RequestBody  AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                        .token(result.getToken())
                        .isAuthenticated(result.isAuthenticated())
                        .build()
        );
    }

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ResponseEntity.ok(IntrospectResponse.builder()
                .valid(result.isValid())
                .build());
    }

}
