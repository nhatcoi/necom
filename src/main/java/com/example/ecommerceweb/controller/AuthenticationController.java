package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.request.AuthenticationRequest;
import com.example.ecommerceweb.dto.request.IntrospectRequest;
import com.example.ecommerceweb.dto.response.AuthenticationResponse;
import com.example.ecommerceweb.dto.response.IntrospectResponse;
import com.example.ecommerceweb.dto.response.ResponseData;
import com.example.ecommerceweb.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final Translator translator;

    @PostMapping("/log-in")
    public ResponseData<AuthenticationResponse> logIn(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success") , result);
    }

    @PostMapping("/introspect")
    public ResponseData<?> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.introspect(request);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), result);
    }

}
