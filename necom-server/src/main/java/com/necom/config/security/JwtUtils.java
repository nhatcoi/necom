package com.necom.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    private final SecretKey signingKey;

    private final int jwtExpiration;

    public JwtUtils(@Value("${necom.app.jwtSecret}") String jwtSecret,
                    @Value("${necom.app.jwtExpirationMs}") int jwtExpiration) {
        this.signingKey = Keys.hmacShaKeyFor(toKeyBytes(jwtSecret));
        this.jwtExpiration = jwtExpiration;
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + this.jwtExpiration))
                .signWith(signingKey, Jwts.SIG.HS512)
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Invalid JWT expired {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Invalid JWT unsupported {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT {}", e.getMessage());
        }

        return false;
    }

    /**
     * HS512 cần khóa tối thiểu 512 bit. Secret ngắn hơn 64 byte được băm SHA-512 để đủ độ dài
     * (jjwt 0.12 từ chối khóa yếu, bản 0.9 cũ thì chấp nhận).
     */
    private static byte[] toKeyBytes(String secret) {
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        if (raw.length >= 64) {
            return raw;
        }
        try {
            return MessageDigest.getInstance("SHA-512").digest(raw);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

}
