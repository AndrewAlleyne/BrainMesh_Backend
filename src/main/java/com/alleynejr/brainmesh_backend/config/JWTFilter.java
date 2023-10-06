//package com.alleynejr.brainmesh_backend.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//
//@Component
//public class JWTFilter extends OncePerRequestFilter {
//
//    private final CustomUserDetailsService userDetailsService;
//    private final JWTService jwtService;
////    private final AuthenticationManager authenticationManager;
//
//    /*&& SecurityContextHolder.getContext().getAuthentication() == null*/
//
//
//    public JWTAuthenticationFilter(CustomUserDetailsService userDetailsService, JWTService jwtService) {
//        this.userDetailsService = userDetailsService;
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
//
//        final String authorizationHeader = request.getHeader("Authorization");
//        final String jwt;
//        final String userEmail;
//
//        //If the request doesn't have an Authorization header or doesn't start with bearer we need to pass the request down the filter.
//        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        //Extract the JWT
//        jwt = authorizationHeader.substring(7);
//        userEmail = jwtService.extractUsername(jwt);
//
//        if (userEmail != null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
//            if (jwtService.isTokenValid(jwt, userDetails)) {
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}
