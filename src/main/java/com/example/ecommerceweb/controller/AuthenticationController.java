package com.example.ecommerceweb.controller;

import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.auth.AuthenticationRequest;
import com.example.ecommerceweb.dto.auth.IntrospectRequest;
import com.example.ecommerceweb.dto.auth.AuthenticationResponse;
import com.example.ecommerceweb.dto.auth.IntrospectResponse;
import com.example.ecommerceweb.dto.response_data.ResponseData;
import com.example.ecommerceweb.service.services_impl.AuthenticationServiceImpl;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;
    private final Translator translator;

    @PostMapping("/log-in")
    public ResponseData<AuthenticationResponse> logIn(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response) {
        log.info("Login attempt for user: {}", request.getUserIdentifier());
        AuthenticationResponse result = authenticationService.authenticate(request);
        
        // Set refresh token cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        log.info("Login successful for user: {}", request.getUserIdentifier());
        
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), result);
    }

    @PostMapping("/log-out")
    public ResponseData<?> logOut(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info("Logout attempt");
        
        // Extract and revoke access token
        String accessToken = authHeader.substring(7);
        authenticationService.revokeAccessToken(accessToken);

        // Extract and revoke refresh token
        String refreshToken = extractRefreshToken(request);
        if (refreshToken != null) {
            authenticationService.revokeToken(refreshToken);
        }

        // Clear the refresh token cookie
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        
        response.addHeader("Set-Cookie", clearCookie.toString());
        log.info("Logout successful");
        
        return new ResponseData<>(
            HttpStatus.OK.value(), 
            translator.toLocated("response.success"), 
            "Successfully logged out"
        );
    }

    @PostMapping("/introspect")
    public ResponseData<?> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.introspect(request);
        return new ResponseData<>(HttpStatus.OK.value(), translator.toLocated("response.success"), result);
    }

    @PostMapping("/refresh-token")
    public ResponseData<?> refreshToken(HttpServletRequest request) {
        String refreshToken = extractRefreshToken(request);
        log.info("Token refresh attempt");
        
        if (refreshToken == null) {
            return new ResponseData<>(
                HttpStatus.UNAUTHORIZED.value(), 
                translator.toLocated("response.failed"), 
                "Invalid refresh token"
            );
        }

        return new ResponseData<>(
            HttpStatus.OK.value(), 
            translator.toLocated("response.success"), 
            authenticationService.refreshAccessToken(refreshToken)
        );
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
