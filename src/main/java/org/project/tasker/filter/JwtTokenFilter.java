package org.project.tasker.filter;

import org.project.tasker.service.JwtTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    public JwtTokenFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/v1/authenticate") || request.getServletPath().equals("/api/v1/register")) {
            filterChain.doFilter(request, response);
        } else {
            String token = jwtTokenService.resolveToken(request);
            try {
                if (token != null && jwtTokenService.validateToken(token)) {
                    Authentication auth = jwtTokenService.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        }
    }
}
