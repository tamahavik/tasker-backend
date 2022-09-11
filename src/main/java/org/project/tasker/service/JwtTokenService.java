package org.project.tasker.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface JwtTokenService {

    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    Boolean validateToken(String token);

    String getUsernameFromToken(String token);

    String resolveToken(HttpServletRequest request);

    Authentication getAuthentication(String token);


}
