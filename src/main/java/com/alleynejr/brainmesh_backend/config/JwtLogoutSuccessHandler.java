package com.alleynejr.brainmesh_backend.config;

import com.alleynejr.brainmesh_backend.model.TokenBlacklist;
import com.alleynejr.brainmesh_backend.repository.TokenBlacklistRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtLogoutSuccessHandler implements LogoutHandler {

    @Autowired
    TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        String authToken = request.getHeader("Authorization").substring(7);
        tokenBlacklist.setToken(authToken);

        tokenBlacklistRepository.save(tokenBlacklist);
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
