package com.alleynejr.brainmesh_backend.service;

import com.alleynejr.brainmesh_backend.exceptions.RefreshTokenException;
import com.alleynejr.brainmesh_backend.model.LoginRequest;
import com.alleynejr.brainmesh_backend.model.RefreshToken;
import com.alleynejr.brainmesh_backend.model.User;
import com.alleynejr.brainmesh_backend.repository.RefreshTokenRepository;
import com.alleynejr.brainmesh_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Value("${refreshToken.time}")
    Long refreshTokenTime;


    public RefreshToken createRefreshToken(LoginRequest loginRequest) {
        RefreshToken refreshToken = new RefreshToken();
        Optional<User> byUsername = userRepository.findByUsername(loginRequest.getUsername());

        User user = byUsername.get();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenTime));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            // 500 Request prompt user to re-login
            throw new RefreshTokenException(refreshToken.getToken(), "Token was expired.");
        }
        return refreshToken;
    }
}
