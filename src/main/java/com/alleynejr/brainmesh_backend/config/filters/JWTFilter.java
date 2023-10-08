package com.alleynejr.brainmesh_backend.config.filters;

import com.alleynejr.brainmesh_backend.config.CustomUserDetailsService;
import com.alleynejr.brainmesh_backend.model.TokenBlacklist;
import com.alleynejr.brainmesh_backend.repository.TokenBlacklistRepository;
import com.alleynejr.brainmesh_backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


@Component
public class JWTFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    TokenBlacklistRepository tokenBlacklistRepository;


    public JWTFilter(CustomUserDetailsService userDetailsService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Pass request down the filter.
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract Authorization header from request
        jwt = authorizationHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        System.out.println("Username " + userEmail);

        if (userEmail != null) {
            // Load user details from the userDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            try {
                Optional<TokenBlacklist> tokenBlacklist = tokenBlacklistRepository.findById(jwt);
                if (jwtService.isTokenValid(response, jwt, userDetails) && tokenBlacklist.isEmpty()) {
                    System.out.println("Token is valid");
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    System.out.println("TOKEN IS IN THE BLACKLIST");
                }

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

        }
        filterChain.doFilter(request, response);
    }
}
