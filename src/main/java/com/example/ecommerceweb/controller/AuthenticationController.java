package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.dto.request.AuthenticationRequest;
import com.example.ecommerceweb.dto.request.IntrospectRequest;
import com.example.ecommerceweb.dto.response.AuthenticationResponse;
import com.example.ecommerceweb.dto.response.IntrospectResponse;
import com.example.ecommerceweb.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/auth")
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
