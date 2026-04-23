package com.devx.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    final String path = request.getRequestURI();

    if (path.startsWith("/api/v1/auth") || request.getMethod().equalsIgnoreCase("OPTIONS")) {
        filterChain.doFilter(request, response);
        return;
    }

    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }

    final String token = authHeader.substring(7);

    try {
        final String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (!jwtService.isTokenValid(token, username)) {
                filterChain.doFilter(request, response);
                return;
            }

            List<?> rawAuthorities =
                    jwtService.extractClaim(token, claims -> claims.get("authorities", List.class));

            List<String> authoritiesFromToken =
                    rawAuthorities.stream()
                            .map(Object::toString)
                            .toList();

            List<SimpleGrantedAuthority> authorities =
                    authoritiesFromToken.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("✅ AUTH OK");
            System.out.println("USER: " + username);
            System.out.println("AUTHORITIES: " + authorities);
            System.out.println("AUTH HEADER: " + request.getHeader("Authorization"));
        }

    } catch (Exception ex) {
        System.out.println("❌ JWT ERROR: " + ex.getMessage());
    }

    filterChain.doFilter(request, response);
    }
}

