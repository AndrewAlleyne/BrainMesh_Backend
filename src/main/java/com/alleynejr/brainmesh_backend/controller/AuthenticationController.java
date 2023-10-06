package com.alleynejr.brainmesh_backend.controller;

import com.alleynejr.brainmesh_backend.dto.LoginRequestDTO;
import com.alleynejr.brainmesh_backend.exceptions.ApiError;
import com.alleynejr.brainmesh_backend.exceptions.ApiResponse;
import com.alleynejr.brainmesh_backend.exceptions.UserAlreadyExistException;
import com.alleynejr.brainmesh_backend.mappers.LoginRequestMapper;
import com.alleynejr.brainmesh_backend.model.LoginRequest;
import com.alleynejr.brainmesh_backend.model.RegistrationRequest;
import com.alleynejr.brainmesh_backend.service.JwtService;
import com.alleynejr.brainmesh_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class AuthenticationController {
    @Autowired
    LoginRequestMapper loginRequestMapper;

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

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

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + userLoginToken);

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(ApiResponse.success(loginRequestDTO, "User logged in."));
        } catch (Exception e) {
            List<ApiError> errors = Collections.singletonList(ApiError.of("USER_NOT_FOUND.", "User not found."));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(errors));
        }
    }
}
