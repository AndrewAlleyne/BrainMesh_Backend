package com.alleynejr.brainmesh_backend.controller;

import com.alleynejr.brainmesh_backend.dto.LoginRequestDTO;
import com.alleynejr.brainmesh_backend.exceptions.ApiError;
import com.alleynejr.brainmesh_backend.exceptions.ApiResponse;
import com.alleynejr.brainmesh_backend.exceptions.TokenRefreshException;
import com.alleynejr.brainmesh_backend.exceptions.UserAlreadyExistException;
import com.alleynejr.brainmesh_backend.mappers.LoginRequestMapper;
import com.alleynejr.brainmesh_backend.model.LoginRequest;
import com.alleynejr.brainmesh_backend.model.RefreshToken;
import com.alleynejr.brainmesh_backend.model.RegistrationRequest;
import com.alleynejr.brainmesh_backend.model.User;
import com.alleynejr.brainmesh_backend.repository.RefreshTokenRepository;
import com.alleynejr.brainmesh_backend.service.JwtService;
import com.alleynejr.brainmesh_backend.service.RefreshTokenService;
import com.alleynejr.brainmesh_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class AuthenticationController {
    final
    LoginRequestMapper loginRequestMapper;

    final
    UserService userService;

    final
    JwtService jwtService;

    final
    RefreshTokenService refreshTokenService;

    final
    RefreshTokenRepository refreshTokenRepository;

    public AuthenticationController(LoginRequestMapper loginRequestMapper, UserService userService, JwtService jwtService, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository) {
        this.loginRequestMapper = loginRequestMapper;
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest) {
        try {
            String registeredUserToken = userService.register(registrationRequest);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "Bearer " + registeredUserToken);

            return ResponseEntity.ok().headers(httpHeaders).body("Registered successfully.");

        } catch (UserAlreadyExistException userAlreadyExistException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration.");
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponse<LoginRequestDTO>> login(@RequestBody LoginRequest loginRequest) {
        LoginRequestDTO loginRequestDTO = loginRequestMapper.loginRequestToDTO(loginRequest);

        try {
            String userLoginToken = userService.login(loginRequest);


            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest);


            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + userLoginToken);
            headers.add("Refresh-Token", refreshToken.getToken());
            headers.add("Refresh-Token-Expiry", String.valueOf(refreshToken.getExpiryDate()));

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(ApiResponse.success(loginRequestDTO, "User logged in."));
        } catch (Exception e) {
            List<ApiError> errors = Collections.singletonList(ApiError.of("USER_NOT_FOUND.", "User not found."));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(errors));
        }
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authorizationHeader) {
        System.out.println(authorizationHeader);
        return "endpoint hit";
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest httpServletRequest) {
        String jwt = httpServletRequest.getHeader("Authorization").substring(7);
        String refreshToken = httpServletRequest.getHeader("Refresh-Token");

        HttpHeaders headers = new HttpHeaders();
        return refreshTokenRepository.findByToken(refreshToken).map(refreshTokenService::verifyExpiration).map(RefreshToken::getUser).map(user -> {
                    String token = jwtService.generateToken(user);
                    headers.add("Authorization", "Bearer " + token);
                    return ResponseEntity.ok(new TokenRefreshResponse(token, refreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(refreshToken,
                        "Refresh token is not in database!"));
    }

    @GetMapping("/generateToken")
    public String generateToken(@RequestBody User user) {
        String token = userService.generateToken(user);
        System.out.println("Token" + token);
        return token;
    }
}
