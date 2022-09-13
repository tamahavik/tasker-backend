package org.hatama.tasker.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.hatama.tasker.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    @Value("${jwt.valid.token}")
    long tokenValidity;

    @Value("${jwt.valid.refresh}")
    long refreshTokenValidity;

    @Value("${jwt.secret}")
    String tokenSecret;

    private final UserDetailsService userDetailsService;
    private final Clock clock;

    public JwtTokenServiceImpl(UserDetailsService userDetailsService, Clock clock) {
        this.userDetailsService = userDetailsService;
        this.clock = clock;
    }

    @PostConstruct
    protected void init() {
        tokenSecret = Base64.getEncoder().encodeToString(tokenSecret.getBytes());
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString())
                .withIssuedAt(Date.from(Instant.now(clock)))
                .withExpiresAt(Date.from(Instant.now(clock).plus(tokenValidity, ChronoUnit.MINUTES)))
                .withClaim("role", claims)
                .sign(Algorithm.HMAC256(tokenSecret));
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString())
                .withIssuedAt(Date.from(Instant.now(clock)))
                .withExpiresAt(Date.from(Instant.now(clock).plus(refreshTokenValidity, ChronoUnit.MINUTES)))
                .sign(Algorithm.HMAC256(tokenSecret));
    }

    @Override
    public Boolean validateToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(tokenSecret)).build();
        DecodedJWT decodeToken = verifier.verify(token);
        boolean isTokenExpired = decodeToken.getExpiresAt().before(Date.from(Instant.now(clock)));
        return !isTokenExpired;
    }

    @Override
    public String getUsernameFromToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(tokenSecret)).build();
        DecodedJWT decodeToken = verifier.verify(token);
        return decodeToken.getSubject();
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    @Override
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
