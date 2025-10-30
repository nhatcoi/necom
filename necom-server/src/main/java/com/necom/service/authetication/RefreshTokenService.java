package com.necom.service.authetication;

import com.necom.entity.authentication.RefreshToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Authentication authentication);

    RefreshToken verifyExpiration(RefreshToken refreshToken);

}
