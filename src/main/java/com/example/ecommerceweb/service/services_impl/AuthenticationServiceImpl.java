package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.request.auth.AuthenticationRequest;
import com.example.ecommerceweb.dto.request.auth.IntrospectRequest;
import com.example.ecommerceweb.dto.response.auth.AuthenticationResponse;
import com.example.ecommerceweb.dto.response.auth.IntrospectResponse;
import com.example.ecommerceweb.dto.response.auth.TokenResponse;
import com.example.ecommerceweb.entity.Token;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.enums.TokenType;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.repository.TokenRepository;
import com.example.ecommerceweb.repository.UserRepository;
import com.example.ecommerceweb.service.AuthenticationService;
import com.example.ecommerceweb.service.RedisService;
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

import java.time.Duration;
import java.util.Date;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RedisService redisService;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @Override
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

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        User user = new User();
        if (request.getUserIdentifier() != null) {
            user = userRepository.findByEmail(request.getUserIdentifier())
                    .orElseThrow(() -> new ResourceException(ErrorCode.EMAIL_NOT_EXISTED));
        }
        if (user.getIsActive().equals(Boolean.FALSE)) {
            throw new ResourceException(ErrorCode.ACCOUNT_LOCKED);
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

        redisService.saveAccessToken(accessToken, user.getId().toString(), 3600); // 1 hour

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isAuthenticated(true)
                .build();
    }

    @Override
    public TokenResponse refreshAccessToken(String refreshToken) {
        Token token = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResourceException(ErrorCode.TOKEN_NOT_FOUND));
        if (token.getExpirationDate().isBefore(Instant.now())) {
            token.setExpired(true);
            tokenRepository.save(token);
            throw new ResourceException(ErrorCode.TOKEN_EXPIRED);
        }
        return TokenResponse.builder()
                .accessToken(generateToken(token.getUser(), 1, ChronoUnit.HOURS))
                .build();
    }

    @Override
    public void revokeToken(String refreshToken) {
        try {
            Token token = tokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new ResourceException(ErrorCode.TOKEN_NOT_FOUND));

            if (token.getExpirationDate().isBefore(Instant.now())) {
                throw new ResourceException(ErrorCode.TOKEN_EXPIRED);
            }

            // Set token as revoked in database
            token.setRevoked(true);
            tokenRepository.save(token);

            // Add to Redis blacklist with remaining TTL
            long ttl = Duration.between(Instant.now(), token.getExpirationDate()).getSeconds();
            redisService.saveRevokedToken(refreshToken, ttl);

            log.info("Successfully revoked refresh token for user: {}", token.getUser().getUsername());
        } catch (Exception e) {
            log.error("Error revoking refresh token: {}", e.getMessage());
            throw new ResourceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void revokeAccessToken(String accessToken) {
        try {
            // Decode the access token to get expiration time
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            
            // Calculate remaining TTL
            long ttl = Duration.between(Instant.now(), expirationTime.toInstant()).getSeconds();
            
            // Add to Redis blacklist
            redisService.saveRevokedToken(accessToken, ttl);
            
            log.info("Successfully revoked access token");
        } catch (ParseException e) {
            log.error("Invalid access token format: {}", e.getMessage());
            throw new ResourceException(ErrorCode.TOKEN_INVALID);
        } catch (Exception e) {
            log.error("Error revoking access token: {}", e.getMessage());
            throw new ResourceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
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
