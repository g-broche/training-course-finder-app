package com.example.finder.utils.jwt;

import com.example.finder.model.AppUser;
import com.example.finder.repository.AppUserRepository;
import com.example.finder.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AppUserRepository appUserRepository;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService,
            AppUserRepository appUserRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.appUserRepository = appUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        UUID userId = null;

        // Step 1: Extract JWT from Authorization header (Bearer token)
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Remove "Bearer " prefix
        }

        // Step 2: If no JWT in header, check for accessToken cookie as fallback
        if (jwt == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        // Step 3: Extract user id from JWT if token exists
        if (jwt != null) {
            try {
                userId = jwtUtil.extractUserId(jwt);
            } catch (Exception e) {
                // If JWT is malformed or invalid, set user id to null
                userId = null;
            }
        }

        // Step 4: Authenticate user if user id found and not already authenticated
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Load user from database
                AppUser user = appUserRepository.findById(userId).orElseThrow();

                // Build user details from user entity
                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

                // Validate JWT token against user identity
                boolean isJwtvalid = jwtUtil.isTokenValid(jwt, user);

                if (isJwtvalid) {
                    // Convert user roles to Spring Security authorities (add "ROLE_" prefix)
                    var authorities = user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
                            .toList();

                    // Create authentication token with user details and authorities
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // No credentials needed (already authenticated via JWT)
                            authorities);

                    // Attach request details (IP, session, etc.) to authentication
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in SecurityContext for this request
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Clear security context if any error occurs during authentication
                SecurityContextHolder.clearContext();
            }
        }

        // Step 5: Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
