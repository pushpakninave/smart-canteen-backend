package com.smartcanteen.security.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartcanteen.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // <<< EXPLICIT CONSTRUCTOR ADDED HERE >>>
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserDetailsService = customUserDetailsService;
    }
    // <<< END OF EXPLICIT CONSTRUCTOR >>>

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        logger.info("JwtAuthenticationFilter: Processing request for {}", request.getRequestURI());
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("JwtAuthenticationFilter: No JWT found or invalid format. Continuing filter chain.");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtTokenUtil.getUserNameFromJwtToken(jwt);

        if (username != null) {
            logger.info("JwtAuthenticationFilter: Username from token: {}", username);

            try {
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
                logger.info("JwtAuthenticationFilter: UserDetails loaded for: {}, Roles: {}", userDetails.getUsername(), userDetails.getAuthorities());

                if (jwtTokenUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("JwtAuthenticationFilter: Authentication set in SecurityContext for user: {}", userDetails.getUsername());
                } else {
                    logger.warn("JwtAuthenticationFilter: Token valid, but not valid for user details (e.g., expired, or user changed).");
                }
            } catch (Exception e) {
                logger.error("JwtAuthenticationFilter: Error loading user details or validating token: {}", e.getMessage());
            }
        } else {
            logger.info("JwtAuthenticationFilter: Username not found in token or token is invalid.");
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}