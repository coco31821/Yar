package io.yar.yar2026.common.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.yar.yar2026.user.constants.Role;
import io.yar.yar2026.user.dto.TokenBody;
import io.yar.yar2026.user.dto.TokenPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public TokenPair issueTokenPair(Long id, Role role) {
        String accessToken = issueAccessToken(id, role);
        String refreshToken = issueRefreshToken(id, role);

        return new TokenPair(accessToken, refreshToken);
    }

    public String issueAccessToken(Long id, Role role) {
        return issue(id, role, jwtProperties.getValidations().getAccess());
    }

    public String issueRefreshToken(Long id, Role role) {
        return issue(id, role, jwtProperties.getValidations().getRefresh());
    }

    private String issue(Long id, Role role, Long validTime) {
        return Jwts.builder()
                .subject(id.toString())
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + validTime))
                .signWith(getSecretKey())
                .compact();
    }
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecrets().getAppKey().getBytes(StandardCharsets.UTF_8));
    }

    public boolean validate(String token) {

        try {
            parseClaims(token);
            return true;
        } catch ( JwtException e ) {
            log.error("Token validation failed: {}", e.getMessage());
        } catch ( IllegalStateException e ) {
            log.error("Illegal state during token validation");
        } catch ( Exception e ) {
            log.error("Unexpected error during token validation: {}", e.getMessage());
        }

        return false;
    }

    public Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
    }

    // filter에서 사용
    public TokenBody parseJwt(String token) {

        Jws<Claims> claimsJws = parseClaims(token);

        String sub = claimsJws.getPayload().getSubject();
        Object role = claimsJws.getPayload().get("role");

        return new TokenBody(
                Long.parseLong(sub),
                Role.valueOf(role.toString())
        );
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getValidations().getAccess() / 1000;
    }


}
