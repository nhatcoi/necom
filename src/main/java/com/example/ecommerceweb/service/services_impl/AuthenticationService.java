package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.request.auth.AuthenticationRequest;
import com.example.ecommerceweb.dto.request.auth.IntrospectRequest;
import com.example.ecommerceweb.dto.response.auth.AuthenticationResponse;
import com.example.ecommerceweb.dto.response.auth.IntrospectResponse;
import com.example.ecommerceweb.entity.Token;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.enums.TokenType;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.repository.TokenRepository;
import com.example.ecommerceweb.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY);
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        User user;
        if (isNumeric(request.getUserIdentifier())) {
            user = userRepository.findByPhoneNumber(request.getUserIdentifier())
                    .orElseThrow(() -> new ResourceException(ErrorCode.PHONE_NUMBER_NOT_EXISTED));
        } else {
            user = userRepository.findByUsername(request.getUserIdentifier())
                    .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));
        }

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated)
            throw new ResourceException(ErrorCode.UNAUTHENTICATED);

        String accessToken = generateToken(user, 1, ChronoUnit.HOURS);
        String refreshToken = generateToken(user, 7, ChronoUnit.DAYS);

        Token token = Token.builder()
                .token(refreshToken)
                .expirationDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .tokenType(TokenType.REFRESH)
                .user(user)
                .build();
        tokenRepository.save(token);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isAuthenticated(true)
                .build();
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }


    private String generateToken(User user, int amountToAdd, ChronoUnit unit) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("jackieshop.dev")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(amountToAdd, unit)))
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(claimSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error signing token", e);
            throw new RuntimeException(e);
        }
    }


    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> stringJoiner.add(role.getName()));
        }

        return stringJoiner.toString();
    }
}
