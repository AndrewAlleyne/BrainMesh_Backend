package com.alleynejr.brainmesh_backend.controller;

import com.alleynejr.brainmesh_backend.dto.LoginRequestDTO;
import com.alleynejr.brainmesh_backend.exceptions.ApiError;
import com.alleynejr.brainmesh_backend.exceptions.ApiResponse;
import com.alleynejr.brainmesh_backend.mappers.LoginRequestMapper;
import com.alleynejr.brainmesh_backend.model.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponse<LoginRequestDTO>> login(@RequestBody LoginRequest loginRequest) {
        LoginRequestDTO loginRequestDTO = loginRequestMapper.loginRequestToDTO(loginRequest);

        try {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(loginRequestDTO, "User logged in"));
        } catch (Exception e) {
            List<ApiError> errors = Collections.singletonList(ApiError.of("USER_NOT_FOUND", "User not found."));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(errors));
        }
    }
}
